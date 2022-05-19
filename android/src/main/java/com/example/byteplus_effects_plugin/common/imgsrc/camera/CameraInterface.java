package com.example.byteplus_effects_plugin.common.imgsrc.camera;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.media.ImageReader;
import android.view.SurfaceView;
import android.view.View;

import java.util.List;


public interface CameraInterface {
    int CAMERA_BACK = Camera.CameraInfo.CAMERA_FACING_BACK;
    int CAMERA_FRONT = Camera.CameraInfo.CAMERA_FACING_FRONT;

    void init(Context context);

    boolean open(int position, CameraListener listener);

    void enableTorch(boolean enable);

    void close();

    void stopPreview();

    void startPreview(SurfaceTexture surfaceTexture);

    void addPreview(SurfaceView surface);

    void changeCamera(int cameraPosition, CameraListener cameraListener);
    //  {zh} 返回size    {en} Return size 
    int[] initCameraParam();

    int[] getPreviewWH();

    boolean isTorchSupported();

    void cancelAutoFocus();

    boolean currentValid();

    boolean setFocusAreas(View previewView, float[] pos, int rotation);

    List<int[]> getSupportedPreviewSizes();

    void setZoom(float scaleFactor);

    int getCameraPosition();

    boolean setVideoStabilization(boolean toggle);

    //  {zh} 检测video稳定性    {en} Detect video stability 
    boolean isVideoStabilizationSupported();

    int getOrientation();

    boolean isFlipHorizontal();

    //   {zh} 获取相机fov数组       {en} Get camera fov array  
    float[] getFov();

    //   {zh} 设置相机捕获的分辨率       {en} Set the resolution of camera capture  
    void setPreferSize(SurfaceTexture texture, int height, int width);

    void captureImage(ImageReader.OnImageAvailableListener listener);
}
