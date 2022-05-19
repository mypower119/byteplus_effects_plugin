package com.example.byteplus_effects_plugin.common.base;

import static android.opengl.GLSurfaceView.RENDERMODE_WHEN_DIRTY;
import static com.example.byteplus_effects_plugin.common.config.ImageSourceConfig.IMAGE_SOURCE_CONFIG_KEY;
import static com.example.byteplus_effects_plugin.common.config.ImageSourceConfig.ImageSourceType.TYPE_IMAGE;
import static com.example.byteplus_effects_plugin.common.config.ImageSourceConfig.ImageSourceType.TYPE_VIDEO;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ConfigurationInfo;
import android.graphics.Bitmap;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.media.ImageReader;
import android.media.ThumbnailUtils;
import android.opengl.EGL14;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import com.example.byteplus_effects_plugin.R;
import com.example.byteplus_effects_plugin.common.config.ImageSourceConfig;
import com.example.byteplus_effects_plugin.common.imgsrc.ImageSourceProvider;
import com.example.byteplus_effects_plugin.common.imgsrc.bitmap.BitmapSourceImpl;
import com.example.byteplus_effects_plugin.common.imgsrc.camera.CameraSourceImpl;
import com.example.byteplus_effects_plugin.common.imgsrc.video.SimplePlayer;
import com.example.byteplus_effects_plugin.common.imgsrc.video.VideoEncodeHelper;
import com.example.byteplus_effects_plugin.common.imgsrc.video.VideoSourceImpl;
import com.example.byteplus_effects_plugin.common.model.ProcessInput;
import com.example.byteplus_effects_plugin.common.model.ProcessOutput;
import com.example.byteplus_effects_plugin.common.utils.BitmapUtils;
import com.example.byteplus_effects_plugin.common.utils.FrameRator;
import com.example.byteplus_effects_plugin.common.utils.PreviewSizeManager;
import com.example.byteplus_effects_plugin.common.utils.ToastUtils;
import com.example.byteplus_effects_plugin.core.util.ImageUtil;
import com.example.byteplus_effects_plugin.core.util.LogUtils;
import com.example.byteplus_effects_plugin.core.util.OrientationSensor;
import com.bytedance.labcv.effectsdk.BytedEffectConstants;
import com.dmcbig.mediapicker.PickerActivity;
import com.dmcbig.mediapicker.PickerConfig;
import com.dmcbig.mediapicker.entity.Media;
import com.google.gson.Gson;

import java.util.ArrayList;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/** {zh} 
 * 包含一个GLSurfaceView的基类Activity,根据参数选择不同的数据源，并启动它
 */

/** {en}
 * Contains a GLSurfaceView base class Activity, selects different data sources according to the parameters, and starts it
 */

public abstract class BaseGLActivity extends FragmentActivity implements GLSurfaceView.Renderer, SurfaceTexture.OnFrameAvailableListener,  View.OnClickListener {
    protected static final int REQUEST_CODE_CHOOSE = 10;
    protected Context mContext;
    protected GLSurfaceView mSurfaceView;
    protected ImageSourceProvider mImageSourceProvider;
    protected FrameRator mFrameRator;
    protected int mSurfaceWidth = 0;
    protected int mSurfaceHeight = 0;
    //  {zh} 转正后的纹理尺寸  {en} Texture size after becoming a full member
    protected int mTextureWidth = 0;
    protected int mTextureHeight = 0;
    //  {zh} 设备支持的最大纹理宽高，4000是一个上限值，过大会导致内存资源紧张  {en} The maximum texture width and height supported by the device, 4000 is an upper limit, too large will cause memory resources to be tight
    protected int mMaxTextureSize =3000;

    //  {zh} 视频播放器相关  {en} Video player related
    protected ImageView mPlayIcon;
    protected ImageView mThubnail;
    protected FrameLayout mContainer;

    protected volatile boolean mFrameAvailable;
    protected ImageSourceConfig mImageSourceConfig = null;
    //  {zh} 相册中选中的图片或者视频路径  {en} Picture or video path selected in the album
    protected String mMediaPath;
    protected volatile boolean mIsPaused = false;
    // {zh} imageUtil是负责处理纹理的Program封装类,与GLContext强关联，因此GL上下文发生变化之前，需要调用各自的销毁函数，但对象本身不销毁，再次使用时会根据内部变量的状态，重新关联当前上下文初始化 {en} ImageUtil is a Program encapsulation class responsible for processing textures. It is strongly associated with GLContext, so before the GL context changes, you need to call the respective destruction function, but the object itself is not destroyed. When you use it again, you will reassociate the current context according to the state of the internal variable Initialization
    protected ImageUtil mImageUtil;
    //  {zh} 视频编码辅助类  {en} Video coding assistant class
    protected VideoEncodeHelper mVideoEncodeHelper;
    //  {zh} 每一帧处理的结果  {en} The result of each frame processing
    protected ProcessOutput output = new ProcessOutput();
    protected ProcessInput mInput = new ProcessInput();
    protected ProcessInput mPictureInput = new ProcessInput();
    protected long mAlgorithmTime = 0;
    protected int mFrameIndex = 1;
    protected long mProcessTimeSum = 0;
    protected long mMaxProcessTime = 0;
    protected long mMinProcessTime = 100000L;
    protected long mCameraChangeSkipFrame = 0;

    /** {zh} 
     * 每个子类负责实现各自的纹理处理逻辑，如果算法类子类，需要先将2D纹理转出Buffer
     * @param input 输入
     * @return 处理后结果
     */
    /** {en} 
     * Each subclass is responsible for implementing its own texture processing logic. If the algorithm subclass is used, the 2D texture needs to be transferred out of Buffer
     * @param input  input
     * @return  after processing the result
     */
    public  abstract ProcessOutput processImpl(ProcessInput input);

    public ProcessOutput processImageImpl(ProcessInput input) { return null; }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtils.d("onCreate");

        mContext = this;
        mImageUtil = new ImageUtil();
        parseSrcConfig();
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_gl_base);
        mFrameRator = new FrameRator();

        initView();
        customBySrcConfig();

        //vehook_start don't delete this line

    }

    private void initView(){
        mSurfaceView = findViewById(R.id.glview);
        ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        ConfigurationInfo ci = am.getDeviceConfigurationInfo();
        if(ci.reqGlEsVersion >= 0x30000)
        {
            mSurfaceView.setEGLContextClientVersion(3);
        }
        else
        {
            mSurfaceView.setEGLContextClientVersion(2);
        }

        mSurfaceView.setRenderer(this);
        mSurfaceView.setRenderMode(RENDERMODE_WHEN_DIRTY);
        //  {zh} 视频播放器相关  {en} Video player related
        mPlayIcon = findViewById(R.id.img_play);
        mPlayIcon.setOnClickListener(this);
        LogUtils.d("mPlayIcon.setOnClickListener(this)");
        mThubnail = findViewById(R.id.img_thubnail);

        mContainer = findViewById(R.id.fl_container);

    }

    /** {zh} 
     *解析数据源
     */
    /** {en} 
     *Parsing data sources
     */
    private void parseSrcConfig(){
        String sImageSourceConfig = getIntent().getStringExtra(IMAGE_SOURCE_CONFIG_KEY);
        LogUtils.d("sImageSourceConfig ="+sImageSourceConfig);
        if (sImageSourceConfig == null) {
            throw new IllegalStateException("image source config must be set");
        }

        mImageSourceConfig = new Gson().fromJson(sImageSourceConfig, ImageSourceConfig.class);



    }

    private void customBySrcConfig(){
        if (null == mImageSourceConfig)return;

        if (mImageSourceConfig.isRecordable()){
            mVideoEncodeHelper = new VideoEncodeHelper();
        }
        switch (mImageSourceConfig.getType()){
            case TYPE_CAMERA:
                mImageSourceProvider = new CameraSourceImpl(getApplicationContext(), mSurfaceView);

                OrientationSensor.start(this);
                break;
            case TYPE_VIDEO:
                mImageSourceProvider = new VideoSourceImpl(null, new SimplePlayer.IPlayStateListener() {
                    @Override
                    public void videoAspect(int width, int height, int videoRotation) {

                    }

                    @Override
                    public void onVideoEnd() {
                        runOnUiThread(()->{
                            if (null != mVideoEncodeHelper){
                                ToastUtils.show(getString(R.string.video_process_ok) + mVideoEncodeHelper.getVideoPath());
                                LogUtils.d(getString(R.string.video_process_ok)+mVideoEncodeHelper.getVideoPath());
                            }
                            finish();

                        });



                    }
                }, mVideoEncodeHelper);
                mPlayIcon.setVisibility(View.VISIBLE);
                mThubnail.setVisibility(View.VISIBLE);


                break;
            case TYPE_IMAGE:
                mImageSourceProvider = new BitmapSourceImpl();
                break;
            default:
                break;
        }
    }

    @Override
    public void onClick(View view) {
         if (view.getId() == R.id.img_play){
            startPlay();
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        LogUtils.d("onPause");
        if (null == mSurfaceView) return;
        mIsPaused = true;
        mSurfaceView.onPause();
        mFrameRator.stop();
        mSurfaceView.queueEvent(new Runnable() {
            @Override
            public void run() {
                mImageUtil.release();
                mImageSourceProvider.close();
                if (mVideoEncodeHelper != null){
                    mVideoEncodeHelper.destroy();
                }
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        LogUtils.d("onResume "+this);
        if (null == mSurfaceView) return;
        //  {zh} 确保在setRender之后调用，否则会出现NPE崩溃  {en} Make sure to call after setRender, otherwise NPE crash will occur
        mSurfaceView.onResume();
        mIsPaused = false;
    }

    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
        LogUtils.d("onSurfaceCreated");
        //  {zh} 获取能够支持的最大纹理尺寸  {en} Get the maximum texture size that can be supported
        int [] max = new int[1];
        GLES20.glGetIntegerv(GLES20.GL_MAX_TEXTURE_SIZE, max, 0);
        if (max[0]  > 1){
            mMaxTextureSize = Integer.min(max[0],mMaxTextureSize);

        }

        if (null == mImageSourceProvider){
            LogUtils.e("null == imageSourceProvider  when onSurfaceCreated invoked");
            return;
        }
        if (null == mImageSourceConfig || TextUtils.isEmpty(mImageSourceConfig.getMedia())){
            LogUtils.e("media is empty  when onSurfaceCreated invoked");
            return;
        }
        if (mIgnorCameraOnce){
            mIgnorCameraOnce = false;
            return;
        }
        //  {zh} 视频模式需要手动开始  {en} Video mode needs to start manually
        if (mImageSourceProvider instanceof VideoSourceImpl){
            runOnUiThread(()->{
                mPlayIcon.setVisibility(View.VISIBLE);
                mThubnail.setVisibility(View.VISIBLE);
                mThubnail.setImageBitmap(ThumbnailUtils.createVideoThumbnail(mImageSourceConfig.getMedia(), MediaStore.Images.Thumbnails.FULL_SCREEN_KIND));

            });


        }else if (mImageSourceProvider instanceof CameraSourceImpl){
            int cameraId = Integer.valueOf(mImageSourceConfig.getMedia());
            if (cameraId != Camera.CameraInfo.CAMERA_FACING_FRONT && cameraId != Camera.CameraInfo.CAMERA_FACING_BACK){
                cameraId = Camera.CameraInfo.CAMERA_FACING_FRONT;
            }
            if (mImageSourceConfig.getRequestWidth() != 0 && mImageSourceConfig.getRequestHeight() != 0) {
                ((CameraSourceImpl)mImageSourceProvider).setPreferSize(mImageSourceConfig.getRequestWidth(), mImageSourceConfig.getRequestHeight());
            }


            mImageSourceProvider.open(cameraId, this);
        }else if (mImageSourceProvider instanceof BitmapSourceImpl){
            Bitmap bitmap = prepareBitmap(mImageSourceConfig.getMedia(), mImageSourceConfig.getRequestWidth(), mImageSourceConfig.getRequestHeight() );
            if (bitmap!= null && !bitmap.isRecycled()){
                mImageSourceProvider.open(bitmap, null);
                //  {zh} 开始循环取帧  {en} Start loop fetch frame
                mSurfaceView.requestRender();
            }

        }

        mFrameRator.start();

    }

    /**
     *
     * @param imagePath
     * @param requestWidth
     * @param requestHeight
     * @return
     */
    private Bitmap prepareBitmap(String imagePath, int requestWidth,int requestHeight){
        LogUtils.d("requestWidth ="+requestWidth);
        LogUtils.d("requestHeight ="+requestHeight);
        Bitmap bitmap = BitmapUtils.decodeBitmapFromFile(imagePath, requestWidth, requestHeight);
        if (null == bitmap) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ToastUtils.show("图片解析失败");
                    finish();
                }
            });
            return null;
        }
        LogUtils.d("bitmap 尺寸："+bitmap.getWidth() + "  "+bitmap.getHeight()+"  mMaxTextureSize ="+mMaxTextureSize);

        if (bitmap.getWidth() >  mMaxTextureSize || bitmap.getHeight() > mMaxTextureSize){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ToastUtils.show("图片超过OPENGL纹理支持的最大尺寸");
                    finish();
                }
            });
            return null;
        }
        return bitmap;


    }

    @Override
    public void onSurfaceChanged(GL10 gl10, int width, int height) {
        if (width != 0 && height != 0) {
           mSurfaceWidth = width;
           mSurfaceHeight = height;
        }


    }



    @Override
    public void onDrawFrame(GL10 gl10) {
        if (mIsPaused){
            return;
        }

        if (mImageSourceProvider == null || !mImageSourceProvider.isReady()) {
            return;
        }
        if (mImageUtil == null ) {
            return;
        }
        // {zh} 清空缓冲区颜色 {en} Clear buffer color
        //Clear buffer color
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        if (!isHalDrawOnScreen()) {
            mImageSourceProvider.update();
            //  {zh} 将输入纹理转换出人脸为正的2D纹理  {en} Convert the input texture to a 2D texture with a positive face
            ProcessInput input = transToPortrait();
            //  {zh} 屏幕坐标和纹理坐标转换工具需要刷新当前的一些关键参数  {en} The screen coordinate and texture coordinate conversion tool needs to refresh some of the current key parameters
            PreviewSizeManager.getInstance().update(mSurfaceView.getWidth(), mSurfaceView.getHeight(),
                    input.getWidth(), input.getHeight(), mImageSourceProvider.getScaleType() == ImageView.ScaleType.CENTER_INSIDE);
            ProcessOutput output = processImpl(input);
            //  {zh} 上屏  {en} Top screen
            drawOnScreen(output);
        }

        if (mVideoEncodeHelper != null){
            mVideoEncodeHelper.onVideoData(EGL14.eglGetCurrentContext(), output.getTexture(),output.getWidth(),
                    output.getHeight(), mImageSourceProvider.getTimestamp());

        }
        if (mImageSourceProvider instanceof BitmapSourceImpl){
            mSurfaceView.requestRender();

        }
        mFrameRator.addFrameStamp();

    }

    boolean isHalDrawOnScreen() {
        return false;
    }

    /** {zh} 
     * 上屏
     * @param output
     */
    /** {en} 
     * Upscreen
     * @param output
     */
    private void drawOnScreen(ProcessOutput output){
        if (output == null){
            return;
        }
        if (!GLES20.glIsTexture(output.getTexture())){
            LogUtils.e("output texture not a valid texture");
            return;
        }
        ImageUtil.Transition drawTransition = new ImageUtil.Transition()
                .crop(mImageSourceProvider.getScaleType(), 0, output.getWidth(), output.getHeight(), mSurfaceWidth, mSurfaceHeight);
        mImageUtil.drawFrameOnScreen(output.getTexture(), BytedEffectConstants.TextureFormat.Texure2D,   mSurfaceWidth, mSurfaceHeight,  drawTransition.getMatrix());


    }

    /** {zh} 
     * 将输入纹理转为正的2D纹理，如果是前置摄像头，还会镜像翻转
     * @return 输入封装
     */
    /** {en} 
     * Change the input texture to a positive 2D texture, and if it is a front camera, it will also mirror and flip
     * @return the input package
     */
    protected ProcessInput transToPortrait(){
        if (mImageSourceProvider == null)return null;
        ProcessInput processInput = new ProcessInput();
        mTextureWidth = mImageSourceProvider.getWidth();
        mTextureHeight = mImageSourceProvider.getHeight();
        processInput.setTexture(mImageSourceProvider.getTexture());

        processInput.setSensorRotation(OrientationSensor.getOrientation());
            if (mImageSourceProvider.getOrientation() %180 == 90 || mImageSourceProvider.getTextureFormat() != BytedEffectConstants.TextureFormat.Texure2D){
                if (mImageSourceProvider.getOrientation() %180 == 90){
                    mTextureWidth =  mImageSourceProvider.getHeight();
                    mTextureHeight =  mImageSourceProvider.getWidth();
                }

                ImageUtil.Transition transition = new ImageUtil.Transition().rotate(mImageSourceProvider.getOrientation()).flip(false, mImageSourceProvider.isFront());

                int texture = mImageUtil.transferTextureToTexture(mImageSourceProvider.getTexture(), mImageSourceProvider.getTextureFormat(), BytedEffectConstants.TextureFormat.Texure2D,
                        mImageSourceProvider.getWidth(), mImageSourceProvider.getHeight(),transition);
                processInput.setTexture(texture);


        }

        processInput.setWidth(mTextureWidth);
        processInput.setHeight(mTextureHeight);

        return processInput;


    }

    protected ProcessInput transToPortrait(int texture, int width, int height, int orientation, boolean flipHorizontal) {
        return null;
    }

    @Override
    public void onFrameAvailable(SurfaceTexture surfaceTexture) {
        if (null != mSurfaceView) {
            mSurfaceView.requestRender();
        }

    }



    /** {zh} 
     * 开始播放视频
     */
    /** {en} 
     * Start playing video
     */
    protected void startPlay() {
        new Handler().postDelayed(()-> {

            mPlayIcon.setVisibility(View.INVISIBLE);
            mThubnail.setVisibility(View.INVISIBLE);


        }, 200);
        mSurfaceView.queueEvent(()->{
            //  {zh} 自动化测试仅视频模式需要自动启动  {en} Automated testing video-only mode requires automatic start
            if (mImageSourceProvider instanceof VideoSourceImpl){
                mImageSourceProvider.open(mImageSourceConfig.getMedia(), this);

            }

        });
    }



    protected void startChoosePic() {

        Intent intent =new Intent(this, PickerActivity.class);
        intent.putExtra(PickerConfig.SELECT_MODE,PickerConfig.PICKER_IMAGE_VIDEO);// {zh} 设置选择类型，默认是图片和视频可一起选择(非必填参数) {en} Set the selection type, the default is that pictures and videos can be selected together (non-required parameters)
        long maxSize=188743680L;// {zh} long long long long类型 {en} Long long long long type
        intent.putExtra(PickerConfig.MAX_SELECT_SIZE,maxSize); // {zh} 最大选择大小，默认180M（非必填参数） {en} Maximum selection size, default 180M (not required)
        intent.putExtra(PickerConfig.MAX_SELECT_COUNT,1);  // {zh} 最大选择数量，默认40（非必填参数） {en} Maximum number of selections, default 40 (not required)
        startActivityForResult(intent,REQUEST_CODE_CHOOSE);
    }



    /** {zh} 
     * 从相册选择页回来后，如果已经选中了视频或者图片，默认回来后的页面不开相机，避免相机快速开关
     */
    /** {en} 
     * After returning from the photo album selection page, if the video or picture has been selected, the default page after returning will not open the camera, avoiding the quick switch of the camera
     */
    protected boolean mIgnorCameraOnce = false;
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_CHOOSE && resultCode == PickerConfig.RESULT_CODE) {
            ArrayList<Media> select = data.getParcelableArrayListExtra(PickerConfig.EXTRA_RESULT);// {zh} 选择完后返回的list {en} Returning list after selection
            if (select == null || select.size() == 0)return;
            mMediaPath = select.get(0).path;
            Intent intent = this.getIntent();
            intent.setClass(this,this.getClass());
            ImageSourceConfig imageSourceConfig = mImageSourceConfig.clone();

            imageSourceConfig.setMedia(mMediaPath);
            if (mMediaPath.endsWith(".mp4")
                    || mMediaPath.endsWith("mov")) {
                mIgnorCameraOnce = true;
                imageSourceConfig.setRecordable(true);
                imageSourceConfig.setType(TYPE_VIDEO);
            } else if (mMediaPath.endsWith(".jpg")
                    || mMediaPath.endsWith(".png")
                    || mMediaPath.endsWith(".jpeg")) {
                mIgnorCameraOnce = true;
                imageSourceConfig.setType(TYPE_IMAGE);
                imageSourceConfig.setRecordable(false);
                imageSourceConfig.setRequestWidth(mMaxTextureSize);
                imageSourceConfig.setRequestHeight(mMaxTextureSize);
            } else {
                ToastUtils.show("格式暂不支持");
                return;
            }
            intent.putExtra(IMAGE_SOURCE_CONFIG_KEY,  new Gson().toJson(imageSourceConfig));
            startActivity(intent);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LogUtils.d("onDestroy");
        mSurfaceView = null;
        OrientationSensor.stop();


        //vehook_End don't delete this line
    }

    public void processImageYUV(byte[] data, int format, BytedEffectConstants.Rotation rotation, int width, int height, int runtimes) {}


    private ImageReader.OnImageAvailableListener mOnImageAvailableListener = new ImageReader.OnImageAvailableListener() {
        @Override
        public void onImageAvailable(ImageReader imageReader) {
        }
    };

    protected ProcessInput transYUVToTexture(ProcessInput input) {
        return null;
    }
}
