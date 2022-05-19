package com.example.byteplus_effects_plugin.sports.task;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;

import com.example.byteplus_effects_plugin.core.util.LogUtils;

import java.lang.ref.WeakReference;

/**
 * Created on 2021/7/19 19:37
 */
public class CountDownTask {
    private static final int DEFAULT_INTERVAL = 1000;

    private final int mKey;
    private int mCountNumber;
    private final WeakReference<CountDownCallback> mCallback;

    private volatile boolean mCanceled = true;
    private volatile boolean mPaused;
    private long mStopTimeInFuture;
    private long mPausedMillisInFuture;
    private final long mMillisInFuture;
    private final long mCountDownInterval;

    public CountDownTask(int key, int countNumber, CountDownCallback callback) {
        mCountDownInterval = DEFAULT_INTERVAL;
        mMillisInFuture = countNumber * DEFAULT_INTERVAL;
        this.mKey = key;
        this.mCountNumber = countNumber;
        this.mCallback = new WeakReference<>(callback);
    }

    public CountDownTask(int key, int countNumber, CountDownCallback callback, int interval) {
        mCountDownInterval = interval;
        mMillisInFuture = countNumber * interval;
        this.mKey = key;
        this.mCountNumber = countNumber;
        this.mCallback = new WeakReference<>(callback);
    }

    public synchronized final CountDownTask start() {
        if (!mCanceled) {
            return null;
        }
        mCanceled = false;
        mPaused = false;
        if (mMillisInFuture <= 0) {
            onFinish();
            return this;
        }
        mStopTimeInFuture = SystemClock.elapsedRealtime() + mMillisInFuture;
        mHandler.sendMessage(mHandler.obtainMessage(MSG));
        return this;
    }

    public synchronized final void cancel() {
        mCanceled = true;
        mHandler.removeMessages(MSG);
    }

    public synchronized final void resume() {
        if (!mPaused) {
            return;
        }
        mPaused = false;
        mStopTimeInFuture = SystemClock.elapsedRealtime() + mPausedMillisInFuture;
        mHandler.sendMessage(mHandler.obtainMessage(MSG));
    }

    public synchronized final void pause() {
        if (mPaused) {
            return;
        }
        mPaused = true;
        mHandler.removeMessages(MSG);
        mPausedMillisInFuture = mStopTimeInFuture - SystemClock.elapsedRealtime();
        if (mPausedMillisInFuture < 0) {
            mPausedMillisInFuture = 0;
        }
    }

    public int currentCount() {
        return mCountNumber;
    }

    public void onTick(long millisUntilFinished) {
        mCountNumber -= 1;
        if (mCallback.get() == null) {
            cancel();
            return;
        }

        LogUtils.e("tick " + mKey + " " + mCountNumber);
        mCallback.get().onCountDownTo(mKey, mCountNumber);
    }

    public void onFinish() {
        if (mCallback.get() == null) {
            return;
        }
        mCallback.get().onCountDownDone(mKey);
    }

    private static final int MSG = 1;

    @SuppressLint("HandlerLeak")
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            synchronized (CountDownTask.this) {
                if (mCanceled || mPaused) {
                    return;
                }

                final long millisLeft = mStopTimeInFuture - SystemClock.elapsedRealtime();

                if (millisLeft <= 0) {
                    onFinish();
                    return;
                }

                long lastTickStart = SystemClock.elapsedRealtime();
                onTick(millisLeft);

                long lastTickDuration = SystemClock.elapsedRealtime() - lastTickStart;
                long delay;
                if (millisLeft < mCountDownInterval) {
                    delay = millisLeft - lastTickDuration;
                    if (delay < 0) {
                        delay = 0;
                    }
                } else {
                    delay = mCountDownInterval - lastTickDuration;
                    while (delay < 0) {
                        delay += mCountDownInterval;
                    }
                }

                sendMessageDelayed(obtainMessage(MSG), delay);
            }
        }
    };

    public interface CountDownCallback {
        void onCountDownDone(int key);

        void onCountDownTo(int key, int count);
    }
}
