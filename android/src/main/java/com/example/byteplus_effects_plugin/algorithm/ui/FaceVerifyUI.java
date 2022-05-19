package com.example.byteplus_effects_plugin.algorithm.ui;

import static com.bytedance.labcv.effectsdk.FaceVerify.SAME_FACE_SCORE;

import android.graphics.Bitmap;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.byteplus_effects_plugin.algorithm.fragment.FaceVerifyFragment;
import com.example.byteplus_effects_plugin.algorithm.task.faceverify.FaceVerifyResult;
import com.example.byteplus_effects_plugin.algorithm.task.faceverify.RepeatedVerifyHandler;
import com.example.byteplus_effects_plugin.common.utils.ToastUtils;
import com.example.byteplus_effects_plugin.common.view.PropertyTextView;
import com.example.byteplus_effects_plugin.core.algorithm.FaceVerifyAlgorithmTask;
import com.example.byteplus_effects_plugin.core.algorithm.base.AlgorithmTaskKey;
import com.example.byteplus_effects_plugin.R;

import java.text.DecimalFormat;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created on 2020/8/18 21:26
 */
public class FaceVerifyUI extends BaseAlgorithmUI<FaceVerifyAlgorithmTask.FaceVerifyCaptureResult>
        implements FaceVerifyFragment.IFaceVerifyCallback, RepeatedVerifyHandler.RepeatedVerifyCallback {
    private LinearLayout llFaceVerify;
    private PropertyTextView ptvSimilarity;
    private PropertyTextView ptvCost;
    private TextView tvResultFaceVerify;
    private ImageView ivFaceVerifyShow;

    private ConcurrentLinkedQueue<Message> mMessageQueue = new ConcurrentLinkedQueue<>();
    private RepeatedVerifyHandler mFaceVerifyHandler;
    private boolean mIsOn;

    @Override
    void initView() {
        super.initView();
        addLayout(R.layout.layout_face_verify_info, R.id.fl_algorithm_info);

        if (!checkAvailable(provider())) return;
        ivFaceVerifyShow = provider().findViewById(R.id.iv_face_verify_show);
        llFaceVerify = provider().findViewById(R.id.ll_face_verify);
        ptvSimilarity = provider().findViewById(R.id.ptv_similarity_face_verify);
        ptvCost = provider().findViewById(R.id.ptv_cost_face_verify);
        tvResultFaceVerify = provider().findViewById(R.id.tv_result_face_verify);

        if (!checkAvailable(provider(), provider().getContext())) return;
        if (mFaceVerifyHandler == null) {
            mFaceVerifyHandler = new RepeatedVerifyHandler(provider().getContext(), this);
        }
    }

    @Override
    public void onEvent(AlgorithmTaskKey key, boolean flag) {
        super.onEvent(key, flag);

        mIsOn = flag;
        if (flag) {
            mFaceVerifyHandler.resume();
        } else {
            mFaceVerifyHandler.release();
            ivFaceVerifyShow.setImageBitmap(null);
            onVerifyCallback(null);
        }

        ivFaceVerifyShow.setVisibility(flag ? View.VISIBLE : View.INVISIBLE);
        llFaceVerify.setVisibility(flag ? View.VISIBLE : View.INVISIBLE);
    }

    @Override
    public void onReceiveResult(FaceVerifyAlgorithmTask.FaceVerifyCaptureResult algorithmResult) {
        if (!mMessageQueue.isEmpty() && algorithmResult != null) {
            Message msg = mMessageQueue.poll();
            if (msg != null && algorithmResult.buffer != null) {
                msg.obj = algorithmResult.buffer;
                msg.arg1 = algorithmResult.width;
                msg.arg2 = algorithmResult.height;
                msg.sendToTarget();
            }
        }
    }

    @Override
    public IFragmentGenerator getFragmentGenerator() {
        return new IFragmentGenerator() {
            @Override
            public Fragment create() {
                return new FaceVerifyFragment().setCallback(FaceVerifyUI.this);
            }

            @Override
            public int title() {
                return R.string.tab_face_verify;
            }

            @Override
            public AlgorithmTaskKey key() {
                return FaceVerifyAlgorithmTask.FACE_VERIFY;
            }
        };
    }

    @Override
    public void onPicChoose(final Bitmap bitmap) {
        runOnUIThread(new Runnable() {
            @Override
            public void run() {
                if (mIsOn) {
                    mFaceVerifyHandler.resume();
                }
                mFaceVerifyHandler.setOriginalBitmap(bitmap);
                ivFaceVerifyShow.setImageBitmap(bitmap);
            }
        });
    }

    @Override
    public void faceVerifyOn(boolean flag) {
        onEvent(FaceVerifyAlgorithmTask.FACE_VERIFY, flag);
    }

    @Override
    public void onVerifyCallback(final FaceVerifyResult faceVerifyResult) {
        runOnUIThread(new Runnable() {
            @Override
            public void run() {
                if (!checkAvailable(provider(),
                        provider().getContext(), ptvCost,
                        ptvSimilarity, tvResultFaceVerify)) return;
                if (null == faceVerifyResult) {
                    ptvSimilarity.setValue("0.00");
                    ptvCost.setValue("0ms");
                    tvResultFaceVerify.setText("");
                } else {
                    DecimalFormat df = new DecimalFormat("0.00");
                    ptvSimilarity.setValue(df.format(faceVerifyResult.getSimilarity()));
                    ptvCost.setValue(faceVerifyResult.getCost() + "ms");
                    if (SAME_FACE_SCORE.compareTo(faceVerifyResult.getSimilarity()) < 0) {
                        tvResultFaceVerify.setText(R.string.face_verify_detect);
                    } else {
                        tvResultFaceVerify.setText(R.string.face_verify_no_detect);
                    }
                }
            }
        });
    }

    @Override
    public void onPicChoose(final int validNum) {
        runOnUIThread(new Runnable() {
            @Override
            public void run() {
                if (!checkAvailable(provider(), provider().getContext())) return;
                if (validNum < 1) {
                    ToastUtils.show(provider().getContext().getString(R.string.no_face_detected));
                } else if (validNum > 1) {
                    ToastUtils.show(provider().getContext().getString(R.string.face_more_than_one));
                }
            }
        });
    }

    @Override
    public void requestVerifyFrame(Message msg) {
        mMessageQueue.add(msg);
    }
}
