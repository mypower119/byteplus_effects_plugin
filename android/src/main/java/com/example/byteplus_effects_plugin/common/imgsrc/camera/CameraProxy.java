package com.example.byteplus_effects_plugin.common.imgsrc.camera;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.media.ImageReader;
import android.os.Build;
import android.view.Surface;
import android.view.SurfaceView;

import com.example.byteplus_effects_plugin.common.imgsrc.TextureHolder;
import com.example.byteplus_effects_plugin.core.util.LogUtils;

import java.util.ArrayList;
import java.util.List;


public class CameraProxy {
    private boolean isDebug = true;
    private Context mContext;
    private int mCameraId;
    private CameraInterface mCamera;
    private TextureHolder textureHolder;
    private List<Surface> previewSurfaces;
    private int preview_width, preview_height;

    public CameraProxy(Context context) {
        mContext = context;
        LogUtils.d("Build.MODEL.toLowerCase() ="+Build.MODEL.toLowerCase());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && !Camera2BlackList.CAM2_BLACK_LIST.contains(Build.MODEL.toLowerCase())) {
            mCamera = new Camera2();
        } else {
            mCamera = new Camera1();
        }
        textureHolder = new TextureHolder();
        mCamera.init(context);
        previewSurfaces = new ArrayList<>();
    }

    public boolean isCameraValid() {
        return mCamera.currentValid();
    }

    private void makeSureCameraRelease() {
        int tryCount = 2;
        do {
            releaseCamera();

        } while (isCameraValid() && tryCount-- > 0);
    }

    public boolean openCamera(final int cameraId, final CameraListener listener) {
        try {
            makeSureCameraRelease();
            mCamera.open(cameraId, new CameraListener() {
                @Override
                public void onOpenSuccess() {
                    mCamera.initCameraParam();
                    listener.onOpenSuccess();
                    mCameraId = cameraId;
                }

                @Override
                public void onOpenFail() {
                    listener.onOpenFail();
                }
            });
        } catch (Exception e) {
            mCamera = null;
            LogUtils.e( "openCamera fail msg=" + e.getMessage());
            return false;
        }
        return true;
    }

    public void changeCamera(final int cameraId, final CameraListener listener) {
        try {
            mCamera.changeCamera(cameraId, new CameraListener() {
                @Override
                public void onOpenSuccess() {
                    mCamera.initCameraParam();
                    listener.onOpenSuccess();
                    mCameraId = cameraId;
                }

                @Override
                public void onOpenFail() {
                    listener.onOpenFail();
                }
            });
        } catch (Exception e) {
            mCamera = null;
            LogUtils.e("openCamera fail msg=" + e.getMessage());
        }
    }


    public void releaseCamera() {
        mCamera.close();
        deleteTexture();
        previewSurfaces.clear();
    }

    public void updateTexture() {
        textureHolder.updateTexImage();
    }

    public long getTimeStamp()
    {
        if (textureHolder.getSurfaceTexture() == null) {
            return System.currentTimeMillis();
        }
        return textureHolder.getSurfaceTexture().getTimestamp();
    }

    public void addPreviewSurface(SurfaceView surface) {
        mCamera.addPreview(surface);
        previewSurfaces.add(surface.getHolder().getSurface());
    }


    public void startPreview(SurfaceTexture.OnFrameAvailableListener listener) {
        LogUtils.d("startPreview");
        textureHolder.onCreate(listener);
        mCamera.startPreview(textureHolder.getSurfaceTexture());
    }

    public void deleteTexture() {
        textureHolder.onDestroy();
    }

    public int getOrientation() {
        return mCamera.getOrientation();
    }

    public boolean isFlipHorizontal() {
        return mCamera.isFlipHorizontal();
    }

    public int getCameraId() {
        return mCameraId;
    }

    public boolean isFrontCamera() {
        return mCameraId == Camera.CameraInfo.CAMERA_FACING_FRONT;
    }

    public int getPreviewHeight() {
        return mCamera.getPreviewWH()[1];
    }

    public int getPreviewWidth() {
        return mCamera.getPreviewWH()[0];
    }

    public int getPreviewTexture() {
        return textureHolder.getSurfaceTextureID();
    }

    public float[] getFov() {
        return mCamera.getFov();
    }

    public List<int[]> getSupportPreviewSizes() {return mCamera.getSupportedPreviewSizes();}

    public void setPreferSize(int height, int width) {
        mCamera.setPreferSize(textureHolder.getSurfaceTexture() ,height, width);
    }

    public void restartPreview(){
        mCamera.stopPreview();
        mCamera.startPreview(textureHolder.getSurfaceTexture());
    }

    public void captureImage(ImageReader.OnImageAvailableListener listener) {
        mCamera.captureImage(listener);
    }
}
