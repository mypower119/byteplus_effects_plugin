package com.example.byteplus_effects_plugin.common.imgsrc.camera;

import static com.example.byteplus_effects_plugin.common.imgsrc.camera.RectUtils.*;
import static com.example.byteplus_effects_plugin.common.imgsrc.camera.RectUtils.rotateRectForOrientation;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.SurfaceTexture;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.media.ImageReader;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.util.Size;
import android.view.SurfaceView;
import android.view.View;

import androidx.annotation.Nullable;

import com.example.byteplus_effects_plugin.common.imgsrc.camera.focus.FocusStrategy;
import com.example.byteplus_effects_plugin.common.imgsrc.camera.focus.FocusStrategyFactory;
import com.example.byteplus_effects_plugin.core.util.LogUtils;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;


public class Camera1 implements CameraInterface {

    private static final String TAG = "Camera1";
    private Camera mCurrentCamera;
    public int sCamIdx = 0;
    private int sWidth;
    private int sHeight;
    private FocusStrategy mFocusStrategy = FocusStrategyFactory.getFocusStrategy();
    private Camera.CameraInfo cameraInfo;
    private int cameraIndex;

    @Override
    public void init(Context context) {
        cameraInfo = new Camera.CameraInfo();
    }

    /**
     * This method would open camera and then close it first time calls it
     *
     * @return support or not
     */
    @Override
    public boolean isTorchSupported() {
        boolean support = false;
        close();
        if (open(Camera.CameraInfo.CAMERA_FACING_BACK, null)) {
            support = mCurrentCamera.getParameters() != null
                    && mCurrentCamera.getParameters().getSupportedFlashModes() != null;
            close();
        }
        return support;
    }

    @Override
    public void enableTorch(boolean enable) {
        if (mCurrentCamera == null) return;
        try {
            Camera.Parameters parameters = mCurrentCamera.getParameters();
            if (parameters != null) {
                parameters.setFlashMode(enable ?
                        Camera.Parameters.FLASH_MODE_TORCH : Camera.Parameters.FLASH_MODE_OFF);
                mCurrentCamera.setParameters(parameters);
            }
        } catch (RuntimeException ignore) {
            // empty params
        }
    }

    @Override
    public boolean open(int position, @Nullable CameraListener listener) {
        if (CameraInterface.CAMERA_BACK == position) {
            cameraIndex = Camera.CameraInfo.CAMERA_FACING_BACK;
        } else {
            cameraIndex = Camera.CameraInfo.CAMERA_FACING_FRONT;
        }
        mCurrentCamera = getCamera(cameraIndex);
        if (mCurrentCamera != null) {
            if (listener != null) {
                listener.onOpenSuccess();
            }
            return true;
        }
        if (listener != null) {
            listener.onOpenFail();
        }
        return false;
    }

    @Override
    public void close() {
        if (mCurrentCamera != null) {
            try {
                mCurrentCamera.setPreviewCallback(null);
                mCurrentCamera.stopPreview();//   {zh} 停掉原来摄像头的预览       {en} Stop the preview of the original camera  
                mCurrentCamera.release();
            } catch (Exception ignore) {
            }
        }
        mCurrentCamera = null;
    }


    @Override
    public void addPreview(SurfaceView surface) {
        mSmallPreviewSurface = surface;
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
    }

    byte[] mPreBuffer = null;
    @Override
    public void startPreview(SurfaceTexture surfaceTexture) {
        if (mCurrentCamera == null || surfaceTexture == null) {
            LogUtils.d( "Camera || SurfaceTexture is null.");
            return;
        }
        try {
            mCurrentCamera.setPreviewTexture(surfaceTexture);

            setPreviewCallback();
            mCurrentCamera.startPreview();
        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.e("startPreview: Error " + e.getMessage());
            close();
        }//try

    }




    @Override
    public void changeCamera(int cameraPosition, CameraListener cameraListener) {
        close();
        open(cameraPosition, cameraListener);
    }

    @Override
    public int[] initCameraParam() {
        if (mCurrentCamera != null) {
            List<Camera.Size> ret = mCurrentCamera.getParameters().getSupportedPreviewSizes();
            //  {zh} 完成后给sWidth,sHeight    {en} Give sWidth, sHeight 
            getBestMatchCameraPreviewSize(ret, new Size(sWidth, sHeight));
            try {
                Camera.Parameters parameters = mCurrentCamera.getParameters();
                parameters.setPreviewSize(sWidth, sHeight);
//                  {zh} 某些手机给前置摄像头设置这个参数会导致初始化失败                                                    {en} Some phones set this parameter to the front camera will cause initialization failure                 
//                  {zh} 如果以后app支持闪光灯, 需要把摄像头的前后置参数带过来                                                    {en} If the future app supports flash, you need to bring the front and rear parameters of the camera                 
//                  {zh} parameters.setFlashMode("off"); // 无闪光灯                                                    {en} Parameters.setFlashMode ("off "); // no flash                 
                List<Integer> rates = parameters.getSupportedPreviewFrameRates();
                if (rates != null) {
                    int frameRate = 0;
                    for (int rate : rates) {
                        if (rate == 30) {
                            frameRate = rate;
                        }
                    }
                    if (frameRate == 0 && rates.size() > 0) {
                        frameRate = rates.get(0);
                    }
                    if (frameRate != 0) {
                        parameters.setPreviewFrameRate(frameRate);
                    }
                }
                if (parameters.getSupportedFocusModes().contains(
                        Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO)) {
                    parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
                }
                mCurrentCamera.setParameters(parameters);
//                parameters.setPreviewFormat(PixelFormat.JPEG);
            } catch (Throwable e) {
                e.printStackTrace();
                Log.e(TAG, "Set camera params failed");
            }

            //alloc buffer for camera callback data with once.
//                setupCallback(camera, previewCallback, sWidth * sHeight * 3 / 2);
        }
        return new int[]{sWidth, sHeight};
    }

    @Override
    public void setZoom(float scaleFactor) {
        if (mCurrentCamera == null) return;

        Camera.Parameters parameters = mCurrentCamera.getParameters();
        int maxZoom = parameters.getMaxZoom();
        int zoom = Math.min(maxZoom, (int) Math.ceil(maxZoom * (scaleFactor - 1) / 2));
        parameters.setZoom(zoom);
        mCurrentCamera.setParameters(parameters);
    }

    @Override
    public void cancelAutoFocus() {
        if (mCurrentCamera == null) return;
        try {
            mCurrentCamera.cancelAutoFocus();
        } catch (Throwable ignore) {

        }
    }

    @Override
    public boolean currentValid() {
        return mCurrentCamera != null;
    }

    @Override
    @Nullable
    public int[] getPreviewWH() {
        Camera.Parameters parameters = null;
        try {
            parameters = mCurrentCamera.getParameters();
        } catch (Exception e) {
            e.printStackTrace();
            return new int[]{0, 0};
        }
        Camera.Size size = parameters.getPreviewSize();

        return new int[]{size.width, size.height};
    }

    @Override
    public boolean setFocusAreas(View view, float[] pos, int rotation) {
        if (mCurrentCamera == null) return false;

        Rect rect = calculateTapArea(view, pos, 1, rotation);
        Camera.Parameters parameters = null;
        List<Camera.Area> focusAreas = new ArrayList<>();
        focusAreas.add(new Camera.Area(rect, 1000));
        try {
            parameters = mCurrentCamera.getParameters();
            if (parameters.getMaxNumFocusAreas() > 0) {
                if (!TextUtils.equals(Build.BRAND, "Multilaser") && !TextUtils.equals(Build.BRAND, "MS40")) {
                    parameters.setFocusAreas(focusAreas);
                }
                parameters.setMeteringAreas(focusAreas);
            } else {
                Log.e(TAG, "focus areas not supported");
                return false;
            }
            mFocusStrategy.focusCamera(mCurrentCamera, parameters);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public List<int[]> getSupportedPreviewSizes() {
        if (mCurrentCamera == null) return new ArrayList<>();

        List<Camera.Size> sizes = mCurrentCamera.getParameters().getSupportedPreviewSizes();
        List<int[]> retSizes = new ArrayList<>();
        for (Camera.Size size : sizes) {
            retSizes.add(new int[]{size.width, size.height});
        }
        return retSizes;
    }

    @Override
    public int getCameraPosition() {
        return sCamIdx;
    }

    @Override
    public boolean isVideoStabilizationSupported() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) return false;
        if (mCurrentCamera == null) return false;
        Camera.Parameters parameters = mCurrentCamera.getParameters();
        return parameters != null && parameters.isVideoStabilizationSupported();
    }

    @Override
    public boolean setVideoStabilization(boolean toggle) {
        if (isVideoStabilizationSupported()) {
            Camera.Parameters parameters = mCurrentCamera.getParameters();
            try {
                if (parameters != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
                    parameters.setVideoStabilization(toggle);
                    return true;
                }
            } catch (Exception e) {

            }
        }
        return false;
    }

    public Rect calculateTapArea(View surfaceView, float[] pos, float coefficient, int rotation) {
        float x = pos[0];
        float y = pos[1];
        float focusAreaSize = dip2Px(surfaceView.getContext(), 60);
        int areaSize = Float.valueOf(focusAreaSize * coefficient).intValue();
        int centerX = (int) (x * 2000 / surfaceView.getWidth()) - 1000;
        int centerY = (int) (y * 2000 / surfaceView.getHeight()) - 1000;
        int left = clamp(centerX - areaSize / 2, -1000, 1000);
        int top = clamp(centerY - areaSize / 2, -1000, 1000);
        RectF rectF = new RectF(left, top, clamp(left + areaSize), clamp(top + areaSize));
        Rect partialRect = new Rect(Math.round(rectF.left), Math.round(rectF.top), Math.round(rectF.right), Math.round(rectF.bottom));
        Rect fullRect = new Rect(-1000, -1000, 1000, 1000);
        rotateRectForOrientation(rotation, fullRect, partialRect);
        Rect res = new Rect(partialRect.left - 1000, partialRect.top - 1000, partialRect.right - 1000, partialRect.bottom - 1000);
        res.left = clamp(res.left);
        res.right = clamp(res.right);
        res.top = clamp(res.top);
        res.bottom = clamp(res.bottom);
        return res;
    }

    public static float dip2Px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return dipValue * scale + 0.5f;
    }

    private static int clamp(int x, int min, int max) {
        if (x > max) {
            return max;
        }
        if (x < min) {
            return min;
        }
        return x;
    }

    private static int clamp(int x) {
        return clamp(x, -1000, 1000);
    }

    /* {zh} 
     * 寻找1280*720, 如果没有 再寻找16:9的
     */
    /* {en} 
     * Look for 1280 * 720, if not, look for 16:9
     */

    private void getBestMatchCameraPreviewSize(List<Camera.Size> ret, Size prefer) {
        if (ret != null) {
            int exactWidth = -1, exactHeight = -1;
            int bestWidth = -1, bestHeight = -1;
            for (Camera.Size size : ret) {
                int width = size.width;
                int height = size.height;

                if (width == prefer.getWidth() && height == prefer.getHeight()) {
                    exactHeight = height;
                    exactWidth = width;
                    break;
                }
                if (width * height < sWidth* sHeight && width*height > bestWidth*bestHeight ){
                    bestWidth = width;
                    bestHeight = height;

                }

            }
            if (exactHeight != -1) {
                sWidth = exactWidth;
                sHeight = exactHeight;
            } else {
                sWidth = bestWidth;
                sHeight = bestHeight;
            }
        }
    }

    private Camera getCamera(int position) {
        Camera cam = null;
        int cameraCount = Camera.getNumberOfCameras();
        for (int camIdx = 0; camIdx < cameraCount; camIdx++) {
            try {
                Camera.getCameraInfo(camIdx, cameraInfo);
                if (cameraInfo.facing == position || cameraCount == 1) {

                    cam = Camera.open(camIdx);
                    if (cam != null) {
                        Camera.Parameters mParameters = cam.getParameters();
                        cam.setParameters(mParameters);
                    }
                    sCamIdx = camIdx;

                    break;
                }
            } catch (RuntimeException e) {
                //   {zh} MX5 手机没有摄像头权限， 但是open返回数据   {en} The MX5 phone does not have camera access, but it is open to return data
                Log.e("Your_TAG", "Camera failed to open: " + e.getLocalizedMessage());
                if (cam != null) {
                    try {
                        cam.release();
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                }
                cam = null;
            } catch (Exception e) {
                e.printStackTrace();
                cam = null;
            }
        }
        return cam;
    }

    @Override
    public int getOrientation() {
        return cameraInfo.orientation;
    }

    @Override
    public boolean isFlipHorizontal() {
        return cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT;
    }

    @Override
    public float[] getFov() {
        float[] fov = new float[2];
        if (mCurrentCamera == null) {
            return fov;
        }
        Camera.Parameters parameters = mCurrentCamera.getParameters();
        if (parameters == null) {
            return fov;
        }
        fov[0] = parameters.getHorizontalViewAngle();
        fov[1] = parameters.getVerticalViewAngle();
        return fov;
    }


    @Override
    public void stopPreview() {
        mCurrentCamera.stopPreview();
    }

    @Override
    public void setPreferSize(SurfaceTexture texture, int width, int height) {
        try {
            Camera.Parameters parameters = mCurrentCamera.getParameters();
            parameters.setPreviewSize(width, height);
            sWidth = width;
            sHeight = height;

        }catch (Throwable e){
            Log.e("carmera1", "setPreviewSize: failed");
        }

    }


    //    {zh} -------------------------------- 处理双屏显示 --------------------------    {en} --------------------------------  processing dual-screen display  --------------------------
    private SurfaceView mSmallPreviewSurface;
    private Paint paint;
    /** {zh} 
     * 处理相机预览数据回调，数据默认写死为1280 * 720，如果有改动，修改为camera.previewSize即可。
     */
    /** {en} 
     * Processing camera preview data callback, the data default write dead to 1280 * 720, if there are changes, modify to camera.previewSize.
     */

    private void setPreviewCallback() {
        int size = 1280 * 720 * 3 / 2;
        if (mPreBuffer == null) {
            mPreBuffer = new byte[size];
        }

        mCurrentCamera.addCallbackBuffer(mPreBuffer);
        mCurrentCamera.setPreviewCallbackWithBuffer(new Camera.PreviewCallback() {
            @Override
            public void onPreviewFrame(byte[] data, Camera camera) {
                if (data == null || data.length != 1280 * 720 * 3 / 2) {
                    return;
                }
                Log.d(TAG, "onPreviewFrame: " + data.length);
                drawPreviewCallBackData(data);
                camera.addCallbackBuffer(mPreBuffer);
            }
        });
    }

    /** {zh} 
     * 绘制小屏的相机预览数据
     * @param data  camera 数据回调
     */
    /** {en} 
     * Draw the camera preview data of the small screen
     * @param data  camera data callback
     */

    private void drawPreviewCallBackData(byte[] data) {
        if (mSmallPreviewSurface != null) {
            Canvas canvas = mSmallPreviewSurface.getHolder().lockCanvas();
            canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
            Bitmap bitmap = getBitmap(data);
            if (bitmap != null) {
                Matrix matrix = new Matrix();
                int width = bitmap.getWidth();
                int height = bitmap.getHeight();
                if(isFront()) {
                    matrix.setRotate(-90, width / 2, height / 2);
                    matrix.postTranslate((height-width) * 3 /2,0);
                    matrix.postScale(-1,1);
                }else{
                    matrix.setRotate(90, width / 2, height / 2);
                    matrix.postTranslate((height-width) + 10 ,0);
                }
                canvas.drawBitmap(bitmap,matrix,paint);
            }
            mSmallPreviewSurface.getHolder().unlockCanvasAndPost(canvas);
            if (bitmap != null) {
                bitmap.recycle();
            }
        }
    }

    /** {zh} 
     * 将data转bitmap
      * @param data
     * @return
     */
    /** {en} 
     * Turn data to bitmap
      * @param data
     * @return
     */

    private Bitmap getBitmap(byte[] data){
        int width = 1280;
        int height = 720;

        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        newOpts.inJustDecodeBounds = true;
        YuvImage yuvimage = new YuvImage(
                data,
                ImageFormat.NV21,
                width,
                height,
                null);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        yuvimage.compressToJpeg(new Rect(0, 0, width, height), 100, baos);//    {zh} 80--JPG图片的质量[0-100],100最高    {en} 80--JPG picture quality [0-100], 100 highest
        byte[] rawImage = baos.toByteArray();
        //   {zh} 将rawImage转换成bitmap   {en} Convert rawImage to bitmap
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        Bitmap bitmap = BitmapFactory.decodeByteArray(rawImage, 0, rawImage.length, options);
        return bitmap;
    }


    private boolean isFront(){
        return cameraIndex == Camera.CameraInfo.CAMERA_FACING_FRONT;
    }

    @Override
    public void captureImage(ImageReader.OnImageAvailableListener listener) {

    }
}
