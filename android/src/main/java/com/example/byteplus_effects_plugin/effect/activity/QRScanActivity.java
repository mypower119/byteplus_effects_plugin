package com.example.byteplus_effects_plugin.effect.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.Nullable;
import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.byteplus_effects_plugin.ByteplusEffectsPlugin;
import com.example.byteplus_effects_plugin.common.base.BaseGLActivity;
import com.example.byteplus_effects_plugin.common.imgsrc.camera.CameraSourceImpl;
import com.example.byteplus_effects_plugin.common.model.ProcessInput;
import com.example.byteplus_effects_plugin.common.model.ProcessOutput;
import com.example.byteplus_effects_plugin.common.utils.DensityUtils;
import com.example.byteplus_effects_plugin.common.utils.ToastUtils;
import com.example.byteplus_effects_plugin.core.effect.EffectManager;
import com.example.byteplus_effects_plugin.core.effect.EffectResourceHelper;
import com.example.byteplus_effects_plugin.core.license.EffectLicenseHelper;
import com.example.byteplus_effects_plugin.R;
import com.example.byteplus_effects_plugin.effect.gesture.GestureManager;
import com.example.byteplus_effects_plugin.effect.task.DownloadResourceTask;
import com.example.byteplus_effects_plugin.effect.view.ProgressBar;
import com.example.byteplus_effects_plugin.effect.view.ViewfinderView;
import com.bytedance.labcv.effectsdk.BytedEffectConstants;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.FormatException;
import com.google.zxing.LuminanceSource;
import com.google.zxing.NotFoundException;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;

import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created on 2021/5/19 20:04
 */
public class QRScanActivity extends BaseGLActivity implements DownloadResourceTask.DownloadResourceTaskCallback, ProgressBar.OnProgressChangedListener, GestureManager.OnTouchListener {
    private ViewfinderView vfvQRScan;
    private ProgressDialog pdDownload;
    private TextView tvScanTip;

    protected GestureManager mGestureManager;
    private EffectManager mManager;
    private CameraSourceImpl mCameraSource;
    private boolean mShowingQRScan = true;
    private DownloadResourceTask mDownloadTask;
    private DownloadResourceTask.ResourceType mCurrentResourceType;

    private String mLastDownloadParam = null;
    private AlertDialog mVersionCheckDialog = null;

    private ProgressBar pb;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater.from(this)
                .inflate(R.layout.activity_qrscan,
                        findViewById(R.id.fl_container), true);
        initView();
        initManager();

        if (mImageSourceProvider instanceof CameraSourceImpl) {
            mCameraSource = (CameraSourceImpl) mImageSourceProvider;
        } else {
            throw new IllegalStateException("QR scan activity must run in camera mode");
        }
    }

    private void initView() {
        vfvQRScan = findViewById(R.id.vfv_qr_scan);
        tvScanTip = findViewById(R.id.tv_qr_scan_tip);
        findViewById(R.id.img_back).setOnClickListener((view) -> {
            finish();
        });
        pb = findViewById(R.id.pb_qr_scan);
        pb.setOnProgressChangedListener(this);

        findViewById(R.id.glview).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (mGestureManager == null) {
                    mGestureManager = new GestureManager(getApplicationContext(), QRScanActivity.this);
                }
                return mGestureManager.onTouchEvent(event);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        checkAndSetTextViewMargin();
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (mDownloadTask == null || mDownloadTask.getStatus() != AsyncTask.Status.FINISHED) {
            return;
        }

        mDownloadTask.cancel(true);
        mShowingQRScan = true;
        vfvQRScan.setVisibility(View.VISIBLE);
        tvScanTip.setVisibility(View.VISIBLE);
        hideProgress();
    }

    private void checkAndSetTextViewMargin() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Rect maskRect = vfvQRScan.getScreenRect();
                if (maskRect == null) {
                    checkAndSetTextViewMargin();
                    return;
                }
                ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) tvScanTip.getLayoutParams();
                lp.topMargin = (int) (vfvQRScan.getTop() + maskRect.bottom + DensityUtils.dp2px(getApplicationContext(), 16));
                tvScanTip.setLayoutParams(lp);
            }
        }, 0);
    }

    private void initManager() {
        mManager = new EffectManager(this, new EffectResourceHelper(this), EffectLicenseHelper.getInstance(this));
    }

    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
        super.onSurfaceCreated(gl10, eglConfig);

        mManager.init();
        mManager.recoverStatus();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSurfaceView.queueEvent(new Runnable() {
            @Override
            public void run() {
                mManager.destroy();
            }
        });
    }

    @Override
    public ProcessOutput processImpl(ProcessInput input) {
        int dstTexture = mImageUtil.prepareTexture(input.getWidth(), input.getHeight());
        if (mManager.process(input.getTexture(), dstTexture, input.getWidth(), input.getHeight(), input.sensorRotation, mImageSourceProvider.getTimestamp())) {
            output.texture = dstTexture;
        } else {
            output.texture = input.getTexture();
        }
        output.width = input.getWidth();
        output.height = input.getHeight();

        if (mShowingQRScan) {
            doQRScan(input);
        }

        return output;
    }

    private void doQRScan(ProcessInput input) {
        ByteBuffer buffer = mImageUtil.transferTextureToBuffer(input.getTexture(),
                BytedEffectConstants.TextureFormat.Texure2D,
                BytedEffectConstants.PixlFormat.RGBA8888,
                input.getWidth(), input.getHeight(), 1);

        int width = input.getWidth();
        int height = input.getHeight();
        int top = height / 3;
        int left = (width - top) / 2;
        IntBuffer intBuffer;
        try {
            intBuffer = IntBuffer.allocate(width * height).put(buffer.asIntBuffer());
        } catch (BufferOverflowException e) {
            e.printStackTrace();
            return;
        }
        LuminanceSource source = new RGBLuminanceSource(width, height, intBuffer.array()).crop(left, top, height / 3, height / 3);
        BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
        QRCodeReader reader = new QRCodeReader();
        try {
            Result result = reader.decode(bitmap);
            mLastDownloadParam = result.getText();
            mDownloadTask = new DownloadResourceTask(QRScanActivity.this);
            mDownloadTask.execute(mLastDownloadParam);
            // hide qr scan view once decoding succeed
            onQRScanStop();
        } catch (NotFoundException | ChecksumException | FormatException e) {
            e.printStackTrace();
        }
    }

    private void onQRScanStop() {
        mShowingQRScan = false;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                vfvQRScan.setVisibility(View.GONE);
                tvScanTip.setVisibility(View.GONE);
                showProgress();
            }
        });
    }

    @Override
    public void onSuccess(String path, DownloadResourceTask.ResourceType resourceType) {
        hideProgress();
        mCurrentResourceType = resourceType;
        if (resourceType == DownloadResourceTask.ResourceType.STICKER) {
            mManager.setStickerAbs(path);
        } else if (resourceType == DownloadResourceTask.ResourceType.FILTER) {
            mManager.setFilterAbs(path);
            mManager.updateFilterIntensity(0.8f);
            pb.setVisibility(View.VISIBLE);
            pb.setProgress(0.8f);
        }

        int cameraId = 1 - mCameraSource.getCameraID();
        mImageSourceConfig.setMedia(String.valueOf(cameraId));
        mCameraSource.changeCamera(cameraId, null);
    }

    @Override
    public void onFail(int errorCode, String message) {
        hideProgress();
        if (errorCode == ERROR_CODE_VERSION_NOT_MATCH) {
            if (mVersionCheckDialog == null) {
                mVersionCheckDialog = new AlertDialog.Builder(this, R.style.VersionCheckDialogTheme)
                        .setTitle(R.string.question_ignore_version_not_match)
                        .setPositiveButton(R.string.button_yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                showProgress();
                                mDownloadTask = new DownloadResourceTask(QRScanActivity.this, true);
                                mDownloadTask.execute(mLastDownloadParam);
                            }
                        })
                        .setNegativeButton(R.string.button_no, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        })
                        .setCancelable(false)
                        .create();
            }
            mVersionCheckDialog.show();
        } else {
            ToastUtils.show(message);
        }
    }

    @Override
    public void onProgressUpdate(float progress) {
        if (pdDownload == null) return;
        pdDownload.setProgress((int) (progress * 100));
    }

    @Override
    public String getAppVersionName() {
//        try {
//            return getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
//        } catch (PackageManager.NameNotFoundException e) {
//            return null;
//        }
        return ByteplusEffectsPlugin.getVersionName();
    }

    private void showProgress() {
        if (pdDownload == null) {
            pdDownload = new ProgressDialog(this, R.style.ProgressTheme);
            pdDownload.setTitle(R.string.resource_download_progress);
            pdDownload.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            pdDownload.setCancelable(false);
        }
        pdDownload.show();
        pdDownload.setProgress(0);
    }

    private void hideProgress() {
        if (pdDownload == null) return;
        pdDownload.dismiss();
    }

    @Override
    public void onProgressChanged(ProgressBar progressBar, float progress, boolean isFormUser) {
        if (mCurrentResourceType == DownloadResourceTask.ResourceType.FILTER) {
            mManager.updateFilterIntensity(progress);
        }
    }

    @Override
    public void onTouchEvent(BytedEffectConstants.TouchEventCode eventCode, float x, float y, float force, float majorRadius, int pointerId, int pointerCount) {
        mSurfaceView.queueEvent(new Runnable() {
            @Override
            public void run() {
                mManager.processTouch(eventCode, x, y, force, majorRadius, pointerId, pointerCount);
            }
        });
    }

    @Override
    public void onGestureEvent(BytedEffectConstants.GestureEventCode eventCode, float x, float y, float dx, float dy, float factor) {
        mSurfaceView.queueEvent(new Runnable() {
            @Override
            public void run() {
                mManager.processGesture(eventCode, x, y, dx, dy, factor);
            }
        });
    }
}
