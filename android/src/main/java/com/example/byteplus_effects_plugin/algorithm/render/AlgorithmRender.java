// Copyright (C) 2018 Beijing Bytedance Network Technology Co., Ltd.
package com.example.byteplus_effects_plugin.algorithm.render;

import static com.example.byteplus_effects_plugin.algorithm.render.MaskProgram.FRAGMENT_AFFINE;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.opengl.GLES20;

import com.example.byteplus_effects_plugin.core.util.LogUtils;
import com.bytedance.labcv.effectsdk.BefActionRecognitionInfo;
import com.bytedance.labcv.effectsdk.BefBachSkeletonInfo;
import com.bytedance.labcv.effectsdk.BefCarDetectInfo;
import com.bytedance.labcv.effectsdk.BefChromaKeyingInfo;
import com.bytedance.labcv.effectsdk.BefDistanceInfo;
import com.bytedance.labcv.effectsdk.BefDynamicActionInfo;
import com.bytedance.labcv.effectsdk.BefFaceInfo;
import com.bytedance.labcv.effectsdk.BefGazeEstimationInfo;
import com.bytedance.labcv.effectsdk.BefGeneralObjectInfo;
import com.bytedance.labcv.effectsdk.BefHandInfo;
import com.bytedance.labcv.effectsdk.BefHeadSegInfo;
import com.bytedance.labcv.effectsdk.BefPetFaceInfo;
import com.bytedance.labcv.effectsdk.BefPublicDefine;
import com.bytedance.labcv.effectsdk.BefSkeletonInfo;
import com.bytedance.labcv.effectsdk.BefSkinSegInfo;
import com.bytedance.labcv.effectsdk.BefSkyInfo;
import com.bytedance.labcv.effectsdk.HairParser;
import com.bytedance.labcv.effectsdk.PortraitMatting;
import com.bytedance.labcv.effectsdk.SkySegment;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class AlgorithmRender {
    private final static String TAG = "OpenGLRender";
    private static float DRAW_POINT_SIZE = 4f;
    private static float DRAW_LINE_SIZE = 4f;

    /** {zh}
     * glReadPixel读取高分辨率图片速度较慢，可以根据情况使用CPU或者OpenGLES3.0读取数据，
     * 这里采用显示分辨率的一半做处理，稍微影响检测人脸的精度，较小的人脸可能检测不到
     * GlReadPixel is slow to read high-resolution images,
     * can use CPU or OpenGLES3.0 to read data as needed.
     * Half the display resolution is used here,
     * slightly affect the accuracy of face detection, smaller faces may not be detected
     */
    /**
     * {en}
     * GlReadPixel reads high-resolution pictures slowly, and can use CPU or OpenGLES3.0 to read data according to the situation.
     * Here, half of the display resolution is used for processing, which slightly affects the accuracy of detecting faces. Smaller faces may not be detected
     * GlReadPixel is slow to read high-resolution images,
     * can use CPU or OpenGLES3.0 to read data as needed.
     * Half the display resolution is used here,
     * slightly affect the accuracy of face detection, smaller faces may not be detected
     */

    private static float mResizeRatio = 0.5f;
    private static final String CAMERA_INPUT_VERTEX_SHADER = "" +
            "attribute vec4 position;\n" +
            "attribute vec4 inputTextureCoordinate;\n" +
            "\n" +
            "varying vec2 textureCoordinate;\n" +
            "\n" +
            "void main()\n" +
            "{\n" +
            "	textureCoordinate = inputTextureCoordinate.xy;\n" +
            "	gl_Position = position;\n" +
            "}";

    private static final String CAMERA_INPUT_FRAGMENT_SHADER_OES = "" +
            "#extension GL_OES_EGL_image_external : require\n" +
            "\n" +
            "precision mediump float;\n" +
            "varying vec2 textureCoordinate;\n" +
            "uniform samplerExternalOES inputImageTexture;\n" +
            "\n" +
            "void main()\n" +
            "{\n" +
            "	gl_FragColor = texture2D(inputImageTexture, textureCoordinate);\n" +
            "}";

    public static final String CAMERA_INPUT_FRAGMENT_SHADER = "" +
            "precision mediump float;\n" +
            "varying highp vec2 textureCoordinate;\n" +
            " \n" +
            "uniform sampler2D inputImageTexture;\n" +
            " \n" +
            "void main()\n" +
            "{\n" +
            "     gl_FragColor = texture2D(inputImageTexture, textureCoordinate);\n" +
            "}";

    // portraitmatting  mask color
    private float[] mPortraitColor = {1.0f, 0.0f, 0.0f, 0.3f};
    // hair color
    private float[] mHairColor = {0.5f, 0.08f, 1f, 0.3f};
    private float[] mSkyColor = {0.0f, 1.0f, 0.0f, 0.3f};

    private float[] mHeadColor = {0.1f, 0.28f, 0.6f, 0.3f};


    private float[] mFaceColor = {1f, 0.5f, 0.4f, 0.3f};


    private float[] mMouthColor = {1f, 0f, 0f, 0.7f};


    private float[] mTeethColor = {0f, 1f, 0.5f, 0.7f};
    private float[] mSkinSegColor = {0.5f, 0.5f, 0.8f, 0.8f};
    private float[] mChromaKeyingColor = {1.0f, 0.0f, 0.0f, 1.0f};


    private final static String PROGRAM_ID = "program";
    private final static String POSITION_COORDINATE = "position";
    private final static String TEXTURE_UNIFORM = "inputImageTexture";
    private final static String TEXTURE_COORDINATE = "inputTextureCoordinate";
    private final FloatBuffer mGLTextureBuffer;

    private final Context mContext;


    private FloatBuffer mOriginVertexBuffer;

    private CircleProgram mCircleProgram;
    private DashLineProgram mDashLineProgram;
    private PointProgram mPointProgram;
    private LineProgram mLineProgram;
    private LineColorProgram mLineColorProgram;
    private MaskProgram mPortraitMaskProgram;
    private MaskProgram mHairMaskProgram;
    private MaskProgram mHeadMaskProgram;
    private MaskProgram mSkyMaskProgram;
    private MaskProgram mSkinSegMaskProgram;
    private MaskProgram mChromaKeyingMaskProgram;
    private FaceSegmentProgram mFaceSegmentProgram;


    private boolean mIsInitialized;

    public boolean isInitialized() {
        return mIsInitialized;
    }

    private ArrayList<HashMap<String, Integer>> mArrayPrograms = new ArrayList<HashMap<String, Integer>>(2) {
        {
            for (int i = 0; i < 2; ++i) {
                HashMap<String, Integer> hashMap = new HashMap<>();
                hashMap.put(PROGRAM_ID, 0);
                hashMap.put(POSITION_COORDINATE, -1);
                hashMap.put(TEXTURE_UNIFORM, -1);
                hashMap.put(TEXTURE_COORDINATE, -1);
                add(hashMap);
            }
        }
    };
    //    {zh} 纹理宽度        {en} Texture width  
    // texture width
    private int mViewPortWidth;
    //    {zh} 纹理高度        {en} Texture height  
    // texture height
    private int mViewPortHeight;
    private final static int FRAME_BUFFER_NUM = 1;
    private int[] mFrameBuffers;

    public AlgorithmRender(Context context) {
        mContext = context;

        mGLTextureBuffer = ByteBuffer.allocateDirect(TextureRotationUtil.TEXTURE_FLIPPED.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();

        /* */
        /** {zh}
         * 用来绘制到屏幕的纹理坐标 注意需要y轴翻转
         * The texture coordinates used to draw to the screen need to be flipped on the y axis
         *          1  2
         *          3  4
         */
        /** {en}
         * The texture coordinates used to draw to the screen, note that the y-axis needs to be flipped
         * The texture coordinates used to draw to the screen need to be flipped on the y axis
         *          1 2
         *          3 4
         */

        mGLTextureBuffer.put(TextureRotationUtil.TEXTURE_FLIPPED).position(0);

    }

    public void init(int width, int height) {
        if (mViewPortWidth == width && mViewPortHeight == height) {
            return;
        }
        initProgram(CAMERA_INPUT_FRAGMENT_SHADER_OES, mArrayPrograms.get(0));
        initProgram(CAMERA_INPUT_FRAGMENT_SHADER, mArrayPrograms.get(1));
        mViewPortWidth = width;
        mViewPortHeight = height;
        initFrameBuffers();

        mIsInitialized = true;
    }

    private void initProgram(String fragment, HashMap<String, Integer> programInfo) {
        int proID = programInfo.get(PROGRAM_ID);
        if (proID == 0) {
            proID = ShaderHelper.buildProgram(CAMERA_INPUT_VERTEX_SHADER, fragment);
            programInfo.put(PROGRAM_ID, proID);
            programInfo.put(POSITION_COORDINATE, GLES20.glGetAttribLocation(proID, POSITION_COORDINATE));
            programInfo.put(TEXTURE_UNIFORM, GLES20.glGetUniformLocation(proID, TEXTURE_UNIFORM));
            programInfo.put(TEXTURE_COORDINATE, GLES20.glGetAttribLocation(proID, TEXTURE_COORDINATE));
        }
    }

    /** {zh}
     * 获取opengld读取图像时压缩的倍率
     * Gets the compression multiplier when opengld reads the image
     * @return
     */
    /**
     * {en}
     * Get the magnification of compression when opengld reads the image
     * Gets the compression multiplier when opengld reads the image
     *
     * @return
     */

    public float getResizeRatio() {
        return mResizeRatio;
    }

    public void setResizeRatio(float ratio) {
        mResizeRatio = ratio;
    }

    public void drawAlgorithmResult(Object result, int texture) {
        if (result instanceof BefFaceInfo) {
            BefFaceInfo faceInfo = (BefFaceInfo) result;
            drawFaces(faceInfo, texture);
            drawFaceSegment(faceInfo, texture);
        } else if (result instanceof BefHeadSegInfo) {
            BefHeadSegInfo headSegInfo = (BefHeadSegInfo) result;
            drawHeadSegment(headSegInfo, texture);
        } else if (result instanceof HairParser.HairMask) {
            HairParser.HairMask mask = (HairParser.HairMask) result;
            drawHairMask(mask, texture);
        } else if (result instanceof BefHandInfo) {
            drawHands((BefHandInfo) result, texture);
        } else if (result instanceof BefSkeletonInfo) {
            drawSkeleton((BefSkeletonInfo) result, texture);
        } else if (result instanceof BefPetFaceInfo) {
            drawPetFaces((BefPetFaceInfo) result, texture);
        } else if (result instanceof PortraitMatting.MattingMask) {
            drawMattingMask((PortraitMatting.MattingMask) result, texture);
        } else if (result instanceof BefSkyInfo) {
            drawSkyMask((BefSkyInfo) result, texture);
        } else if (result instanceof BefDistanceInfo) {
            drawHumanDist((BefDistanceInfo) result, texture);
        } else if (result instanceof BefGazeEstimationInfo) {
            drawGazeEstimation((BefGazeEstimationInfo) result, texture);
        } else if (result instanceof BefCarDetectInfo) {
            drawCarInfo((BefCarDetectInfo) result, texture);
        } else if (result instanceof BefActionRecognitionInfo) {
            drawActionRecognition((BefActionRecognitionInfo) result, texture, true);
        } else if (result instanceof BefSkinSegInfo) {
            drawSkinSegmentationMask((BefSkinSegInfo) result, texture);
        } else if (result instanceof BefBachSkeletonInfo) {
            drawBachSkeleton((BefBachSkeletonInfo) result, texture);
        } else if (result instanceof BefChromaKeyingInfo) {
            drawChromaKeyingResult((BefChromaKeyingInfo) result, texture);
        }
    }

    private float transformCenterAlign(float dstX) {
        return transformCenterAlign(dstX, 1 / mResizeRatio);
    }

    /** {zh}
     * 对齐源图像和目标图像的几何中心
     * Align the geometric centers of the source and target images
     * @param dstX
     * @param scale
     * @return
     */
    /**
     * {en}
     * Align the geometric center of the source image and the target image
     * Align the geometric centers of the source and target images
     *
     * @param dstX
     * @param scale
     * @return
     */

    protected float transformCenterAlign(float dstX, float scale) {
        return dstX * scale + 0.5f * (scale - 1);
    }

    public void drawSkeleton(BefDynamicActionInfo dynamicActionInfo, int texture) {
        if (mLineProgram == null) {
            mLineProgram = new LineProgram(mContext, mViewPortWidth, mViewPortHeight);
        }
        if (mPointProgram == null) {
            mPointProgram = new PointProgram(mContext, mViewPortWidth, mViewPortHeight);
        }

        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, mFrameBuffers[0]);
        GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0,
                GLES20.GL_TEXTURE_2D, texture, 0);

        GLES20.glViewport(0, 0, mViewPortWidth, mViewPortHeight);
        for (int i = 0; i < dynamicActionInfo.getPersonCount(); i++) {
            BefDynamicActionInfo.DynamicSkInfo info = dynamicActionInfo.getSkInfos()[i];
            BefPublicDefine.BefRect rect = info.getRect();
            float left = transformCenterAlign(rect.getLeft(), 1 / mResizeRatio);
            float right = transformCenterAlign(rect.getRight(), 1 / mResizeRatio);
            float top = transformCenterAlign(rect.getTop(), 1 / mResizeRatio);
            float bottom = transformCenterAlign(rect.getBottom(), 1 / mResizeRatio);
            mLineProgram.drawRect(new RectF(left, top, right, bottom), Color.RED, DRAW_POINT_SIZE);

            for (int j = 0; j < info.getKeyPoints().length; j++) {
                BefPublicDefine.BefKeyPoint point = info.getKeyPoints()[j];
                mPointProgram.draw(new PointF(transformCenterAlign(point.getX(), 1 / mResizeRatio),
                        transformCenterAlign(point.getY(), 1 / mResizeRatio)), Color.RED, DRAW_POINT_SIZE);
            }
        }
        mLineProgram.drawLines(getSkeletonLineDraws(dynamicActionInfo), Color.BLUE, 4);
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
    }

    public void drawFaces(BefFaceInfo faceInfo, int texture) {
        if (mLineProgram == null) {
            mLineProgram = new LineProgram(mContext, mViewPortWidth, mViewPortHeight);
        }
        if (mPointProgram == null) {
            mPointProgram = new PointProgram(mContext, mViewPortWidth, mViewPortHeight);
        }

        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, mFrameBuffers[0]);
        GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0,
                GLES20.GL_TEXTURE_2D, texture, 0);

        GLES20.glViewport(0, 0, mViewPortWidth, mViewPortHeight);
        for (BefFaceInfo.Face106 face106 : faceInfo.getFace106s()) {
            BefFaceInfo.FaceRect rect = face106.getRect();
            float left = transformCenterAlign(rect.getLeft(), 1 / mResizeRatio);
            float right = transformCenterAlign(rect.getRight(), 1 / mResizeRatio);
            float top = transformCenterAlign(rect.getTop(), 1 / mResizeRatio);
            float bottom = transformCenterAlign(rect.getBottom(), 1 / mResizeRatio);

            mLineProgram.drawRect(new RectF(left, top, right, bottom), Color.RED, DRAW_POINT_SIZE);

            for (BefFaceInfo.FacePoint point : face106.getPoints_array()) {
                PointF target = point.asPoint();
                target.x = transformCenterAlign(target.x, 1 / mResizeRatio);
                target.y = transformCenterAlign(target.y, 1 / mResizeRatio);
                mPointProgram.draw(target, Color.RED, DRAW_POINT_SIZE);
            }
        }
        if (faceInfo.getExtras() != null) {
            for (BefFaceInfo.ExtraInfo extraInfo : faceInfo.getExtras()) {
                for (BefFaceInfo.FacePoint point : extraInfo.getEye_left()) {
                    PointF target = point.asPoint();
                    target.x = transformCenterAlign(target.x, 1 / mResizeRatio);
                    target.y = transformCenterAlign(target.y, 1 / mResizeRatio);
                    mPointProgram.draw(target, Color.rgb(200, 0, 0), DRAW_POINT_SIZE - 1.0f);
                }
                for (BefFaceInfo.FacePoint point : extraInfo.getEye_right()) {
                    PointF target = point.asPoint();
                    target.x = transformCenterAlign(target.x, 1 / mResizeRatio);
                    target.y = transformCenterAlign(target.y, 1 / mResizeRatio);
                    mPointProgram.draw(target, Color.rgb(200, 0, 0), DRAW_POINT_SIZE - 1.0f);
                }
                for (BefFaceInfo.FacePoint point : extraInfo.getEyebrow_left()) {
                    PointF target = point.asPoint();
                    target.x = transformCenterAlign(target.x, 1 / mResizeRatio);
                    target.y = transformCenterAlign(target.y, 1 / mResizeRatio);
                    mPointProgram.draw(target, Color.rgb(220, 0, 0), DRAW_POINT_SIZE - 1.0f);
                }
                for (BefFaceInfo.FacePoint point : extraInfo.getEyebrow_right()) {
                    PointF target = point.asPoint();
                    target.x = transformCenterAlign(target.x, 1 / mResizeRatio);
                    target.y = transformCenterAlign(target.y, 1 / mResizeRatio);
                    mPointProgram.draw(target, Color.rgb(220, 0, 0), DRAW_POINT_SIZE - 1.0f);
                }
                for (BefFaceInfo.FacePoint point : extraInfo.getLeft_iris()) {
                    PointF target = point.asPoint();
                    target.x = transformCenterAlign(target.x, 1 / mResizeRatio);
                    target.y = transformCenterAlign(target.y, 1 / mResizeRatio);
                    mPointProgram.draw(target, Color.parseColor("#FFB400"), DRAW_POINT_SIZE - 1.0f);
                }
                if (extraInfo.getLeft_iris().length > 0) {
                    PointF target = extraInfo.getLeft_iris()[0].asPoint();
                    target.x = transformCenterAlign(target.x, 1 / mResizeRatio);
                    target.y = transformCenterAlign(target.y, 1 / mResizeRatio);
                    mPointProgram.draw(target, Color.GREEN, DRAW_POINT_SIZE - 1.0f);
                }
                for (BefFaceInfo.FacePoint point : extraInfo.getRight_iris()) {
                    PointF target = point.asPoint();
                    target.x = transformCenterAlign(target.x, 1 / mResizeRatio);
                    target.y = transformCenterAlign(target.y, 1 / mResizeRatio);
                    mPointProgram.draw(target, Color.parseColor("#FFB400"), DRAW_POINT_SIZE - 1.0f);
                }
                if (extraInfo.getRight_iris().length > 0) {
                    PointF target = extraInfo.getRight_iris()[0].asPoint();
                    target.x = transformCenterAlign(target.x, 1 / mResizeRatio);
                    target.y = transformCenterAlign(target.y, 1 / mResizeRatio);
                    mPointProgram.draw(target, Color.GREEN, DRAW_POINT_SIZE - 1.0f);
                }
                for (BefFaceInfo.FacePoint point : extraInfo.getLips()) {
                    PointF target = point.asPoint();
                    target.x = transformCenterAlign(target.x, 1 / mResizeRatio);
                    target.y = transformCenterAlign(target.y, 1 / mResizeRatio);
                    mPointProgram.draw(target, Color.rgb(200, 40, 40), DRAW_POINT_SIZE - 1.0f);
                }
            }
        }

//        if (faceInfo.getFaceMask() != null) {
//            drawFaceMask(faceInfo.getFaceMask(), texture,mFaceColor);
//
//        }
//        if (faceInfo.getMouthMask() != null) {
//            drawFaceMask(faceInfo.getMouthMask(), texture,mMouthColor);
//
//        }
//        if (faceInfo.getTeethMask() != null) {
//            drawFaceMask(faceInfo.getTeethMask(), texture,mTeethColor);
//
//        }

        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
    }

    public void drawSkeleton(BefSkeletonInfo skeletonInfo, int textureid) {
        if (mPointProgram == null) {
            mPointProgram = new PointProgram(mContext, mViewPortWidth, mViewPortHeight);
        }
        if (mLineProgram == null) {
            mLineProgram = new LineProgram(mContext, mViewPortWidth, mViewPortHeight);
        }

        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, mFrameBuffers[0]);
        GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0,
                GLES20.GL_TEXTURE_2D, textureid, 0);

        GLES20.glViewport(0, 0, mViewPortWidth, mViewPortHeight);

        for (BefSkeletonInfo.Skeleton skeleton : skeletonInfo.getSkeletons()) {
            for (BefSkeletonInfo.SkeletonPoint skeletonPoint : skeleton.getKeypoints()) {
                if (skeletonPoint != null && skeletonPoint.isDetect()) {
                    PointF target = skeletonPoint.asPoint();
                    target.x = transformCenterAlign(target.x, 1 / mResizeRatio);
                    target.y = transformCenterAlign(target.y, 1 / mResizeRatio);
                    mPointProgram.draw(target, Color.BLUE, DRAW_POINT_SIZE * 2);
                }
            }
            BefFaceInfo.FaceRect rect = skeleton.getSkeletonRect();
            float left = transformCenterAlign(rect.getLeft(), 1 / mResizeRatio);
            float right = transformCenterAlign(rect.getRight(), 1 / mResizeRatio);
            float top = transformCenterAlign(rect.getTop(), 1 / mResizeRatio);
            float bottom = transformCenterAlign(rect.getBottom(), 1 / mResizeRatio);

            mLineProgram.drawRect(new RectF(left, top, right, bottom), Color.RED, DRAW_POINT_SIZE);
        }
        mLineProgram.drawLines(getSkeletonLineDraws(skeletonInfo.getSkeletons()), Color.BLUE, DRAW_LINE_SIZE);
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
    }

    public void drawActionRecognition(BefActionRecognitionInfo info, int texture, boolean showWarning) {
        if (mDashLineProgram == null) {
            mDashLineProgram = new DashLineProgram(mContext, mViewPortWidth, mViewPortHeight);
        }
        if (mCircleProgram == null) {
            mCircleProgram = new CircleProgram(mContext, mViewPortWidth, mViewPortHeight);
        }

        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, mFrameBuffers[0]);
        GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0,
                GLES20.GL_TEXTURE_2D, texture, 0);
        GLES20.glViewport(0, 0, mViewPortWidth, mViewPortHeight);

        int rightLineColor = argbColor(1.f, 0.55f, 0.55f, 0.55f);
        int wrongLineColor = showWarning ? argbColor(1.f, 0.98f, 0.59f, 0.f) : rightLineColor;
        int rightBgColor = argbColor(1.f, 1.f, 1.f, 1.f);
        int rightDotColor = argbColor(1.f, 0.086f, 0.39f, 1.f);
        int wrongDotColor = showWarning ? argbColor(1.f, 0.98f, 0.59f, 0.f) : rightDotColor;
        int[] armsp = {4, 3, 3, 2, 2, 1, 1, 5, 5, 6, 6, 7,
                16, 14, 14, 0, 17, 15, 15, 0,
                1, 8, 8, 11, 11, 1, 1, 0,
                8, 9, 9, 10, 11, 12, 12, 13};

        //  {zh} 肢体线  {en} Body line
        for (int i = 0; i < armsp.length; i += 2) {
            BefPublicDefine.BefKeyPoint k1 = info.keyPoints[armsp[i]];
            BefPublicDefine.BefKeyPoint k2 = info.keyPoints[armsp[i + 1]];
            if (k1.isDetect() && k2.isDetect()) {
                mDashLineProgram.drawLine(new PointF[]{new PointF(k1.getX(), k1.getY()),
                        new PointF(k2.getX(), k2.getY())}, rightLineColor, DRAW_LINE_SIZE);
            }
        }
        for (int i = 0; i < info.feedbackKeyPoints.length; i += 2) {
            BefPublicDefine.BefKeyPoint k1 = info.feedbackKeyPoints[i];
            BefPublicDefine.BefKeyPoint k2 = info.feedbackKeyPoints[i + 1];
            if (k1.isDetect() && k2.isDetect()) {
                mDashLineProgram.drawLine(new PointF[]{new PointF(k1.getX(), k1.getY()),
                        new PointF(k2.getX(), k2.getY())}, wrongLineColor, DRAW_LINE_SIZE);
            }
        }

        //  {zh} 关键点  {en} Key points
        for (int i = 0; i < armsp.length; i += 2) {
            BefPublicDefine.BefKeyPoint k1 = info.keyPoints[armsp[i]];
            BefPublicDefine.BefKeyPoint k2 = info.keyPoints[armsp[i + 1]];
            if (k1.isDetect()) {
                mCircleProgram.draw(new PointF(k1.getX(), k1.getY()), 10.f * 3, rightBgColor);
                mCircleProgram.draw(new PointF(k1.getX(), k1.getY()), 8.f * 3, rightDotColor);
            }
            if (k2.isDetect()) {
                mCircleProgram.draw(new PointF(k2.getX(), k2.getY()), 10.f * 3, rightBgColor);
                mCircleProgram.draw(new PointF(k2.getX(), k2.getY()), 8.f * 3, rightDotColor);
            }
        }
        for (int i = 0; i < info.feedbackKeyPoints.length; i += 2) {
            BefPublicDefine.BefKeyPoint k1 = info.feedbackKeyPoints[i];
            BefPublicDefine.BefKeyPoint k2 = info.feedbackKeyPoints[i + 1];
            if (k1.isDetect()) {
                mCircleProgram.draw(new PointF(k1.getX(), k1.getY()), 8.f * 3, wrongDotColor);
            }
            if (k2.isDetect()) {
                mCircleProgram.draw(new PointF(k2.getX(), k2.getY()), 8.f * 3, wrongDotColor);
            }
        }
    }

    public void drawHumanDist(BefDistanceInfo humanDistanceResult, int texture) {
        if (null == humanDistanceResult) return;
        BefFaceInfo.FaceRect[] rects = humanDistanceResult.getFaceRects();
        if (null == rects || rects.length == 0) return;

        if (mLineProgram == null) {
            mLineProgram = new LineProgram(mContext, mViewPortWidth, mViewPortHeight);
        }

        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, mFrameBuffers[0]);
        GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0,
                GLES20.GL_TEXTURE_2D, texture, 0);

        GLES20.glViewport(0, 0, mViewPortWidth, mViewPortHeight);
        for (BefFaceInfo.FaceRect rect : rects) {
            float left = transformCenterAlign(rect.getLeft(), 1 / mResizeRatio);
            float right = transformCenterAlign(rect.getRight(), 1 / mResizeRatio);
            float top = transformCenterAlign(rect.getTop(), 1 / mResizeRatio);
            float bottom = transformCenterAlign(rect.getBottom(), 1 / mResizeRatio);

            mLineProgram.drawRect(new RectF(left, top, right, bottom), Color.RED, DRAW_POINT_SIZE);
        }
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);


    }

    public PointF[] getSkeletonLineDraws(BefSkeletonInfo.Skeleton[] skeletons) {
        ArrayList<PointF> skeleps = new ArrayList<PointF>();
        for (BefSkeletonInfo.Skeleton skeleton : skeletons) {
            int[] armsp = {4, 3, 3, 2, 2, 1, 1, 5, 5, 6, 6, 7,
                    16, 14, 14, 0, 17, 15, 15, 0,
                    1, 8, 8, 11, 11, 1, 1, 0,
                    8, 9, 9, 10, 11, 12, 12, 13};
            for (int armi = 0; armi < armsp.length; armi += 2) {
                BefSkeletonInfo.SkeletonPoint armp = skeleton.getKeypoints()[armsp[armi]];
                BefSkeletonInfo.SkeletonPoint armpn = skeleton.getKeypoints()[armsp[armi + 1]];
                if (armp.isDetect() && armpn.isDetect()) {
                    skeleps.add(new PointF(transformCenterAlign(armp.asPoint().x, 1 / mResizeRatio), transformCenterAlign(armp.asPoint().y, 1 / mResizeRatio)));
                    skeleps.add(new PointF(transformCenterAlign(armpn.asPoint().x, 1 / mResizeRatio), transformCenterAlign(armpn.asPoint().y, 1 / mResizeRatio)));
                }
            }
        }
        return skeleps.toArray(new PointF[0]);
    }

    public PointF[] getSkeletonLineDraws(BefPublicDefine.BefKeyPoint[] points) {
        int[] armsp = {4, 3, 3, 2, 2, 1, 1, 5, 5, 6, 6, 7,
                16, 14, 14, 0, 17, 15, 15, 0,
                1, 8, 8, 11, 11, 1, 1, 0,
                8, 9, 9, 10, 11, 12, 12, 13};
        ArrayList<PointF> sk = new ArrayList<>();
        for (int armi = 0; armi < armsp.length; armi += 2) {
            BefPublicDefine.BefKeyPoint armp = points[armsp[armi]];
            BefPublicDefine.BefKeyPoint armpn = points[armsp[armi + 1]];
            if (armp.isDetect() && armpn.isDetect()) {
                sk.add(new PointF(transformCenterAlign(armp.getX(), 1 / mResizeRatio), transformCenterAlign(armp.getY(), 1 / mResizeRatio)));
                sk.add(new PointF(transformCenterAlign(armpn.getX(), 1 / mResizeRatio), transformCenterAlign(armpn.getY(), 1 / mResizeRatio)));
            }
        }
        return sk.toArray(new PointF[0]);
    }

    public PointF[] getSkeletonLineDraws(BefDynamicActionInfo dynamicActionInfo) {
        List<PointF> list = new ArrayList<>();
        for (BefDynamicActionInfo.DynamicSkInfo info : dynamicActionInfo.getSkInfos()) {
            int[] armsp = {4, 3, 3, 2, 2, 1, 1, 5, 5, 6, 6, 7,
                    16, 14, 14, 0, 17, 15, 15, 0,
                    1, 8, 8, 11, 11, 1, 1, 0,
                    8, 9, 9, 10, 11, 12, 12, 13};
            for (int armi = 0; armi < armsp.length; armi += 2) {
                BefPublicDefine.BefKeyPoint armp = info.getKeyPoints()[armsp[armi]];
                BefPublicDefine.BefKeyPoint armpn = info.getKeyPoints()[armsp[armi + 1]];
                if (armp.isDetect() && armpn.isDetect()) {
                    list.add(new PointF(transformCenterAlign(armp.getX(), 1 / mResizeRatio), transformCenterAlign(armp.getY(), 1 / mResizeRatio)));
                    list.add(new PointF(transformCenterAlign(armpn.getX(), 1 / mResizeRatio), transformCenterAlign(armpn.getY(), 1 / mResizeRatio)));
                }
            }
        }
        return list.toArray(new PointF[0]);
    }

    public PointF[] getSkeletonLineDraws(BefBachSkeletonInfo.Skeleton[] skeletons) {
        ArrayList<PointF> skeleps = new ArrayList<PointF>();
        for (BefBachSkeletonInfo.Skeleton skeleton : skeletons) {
            int[] armsp = {0, 1, 1, 2, 2, 3, 3, 4, 4, 5, 5, 6, 6, 7, 7, 8, 8, 9, 9, 10, 10, 11, 11,
                           12, 12, 13, 13, 14, 14, 15, 15, 16, 16, 17, 17, 18, 18, 19, 19, 20, 20,
                           21, 21, 22, 22, 23, 23, 24, 23, 25, 25, 26, 26, 27, 27, 28, 28, 29, 29,
                           30, 30, 31, 31, 32, 32, 33, 33, 35, 35, 34, 35, 36, 36, 37, 37, 38, 38,
                           39, 39, 40, 40, 41, 41, 42, 42, 43, 43, 44, 44, 45, 45, 46, 46, 47, 47,
                           48, 48, 49, 49, 50, 50, 51, 51, 52, 52, 53, 53, 54, 54, 55, 55, 56, 56,
                           57, 57, 58, 58, 59, 59, 60, 60, 61, 61, 62, 62, 0
            };

            for (int armi = 0; armi < armsp.length; armi += 2) {
                BefBachSkeletonInfo.SkeletonPoint armp = skeleton.getKeypoints()[armsp[armi]];
                BefBachSkeletonInfo.SkeletonPoint armpn = skeleton.getKeypoints()[armsp[armi + 1]];
                if (armp.isDetect() && armpn.isDetect()) {
                    skeleps.add(new PointF(transformCenterAlign(armp.asPoint().x, 1 / mResizeRatio), transformCenterAlign(armp.asPoint().y, 1 / mResizeRatio)));
                    skeleps.add(new PointF(transformCenterAlign(armpn.asPoint().x, 1 / mResizeRatio), transformCenterAlign(armpn.asPoint().y, 1 / mResizeRatio)));
                }
            }
        }
        return skeleps.toArray(new PointF[0]);
    }

    /** {zh}
     * 绘制手势结果
     * draw gesstures result
     * @param handInfo
     * @param texture
     */
    /**
     * {en}
     * Draw gesture results
     * draw gesstures results
     *
     * @param handInfo
     * @param texture
     */

    public void drawHands(BefHandInfo handInfo, int texture) {

        if (mLineProgram == null) {
            mLineProgram = new LineProgram(mContext, mViewPortWidth, mViewPortHeight);
        }
        if (mPointProgram == null) {
            mPointProgram = new PointProgram(mContext, mViewPortWidth, mViewPortHeight);
        }

        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, mFrameBuffers[0]);
        GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0,
                GLES20.GL_TEXTURE_2D, texture, 0);

        GLES20.glViewport(0, 0, mViewPortWidth, mViewPortHeight);

        if (handInfo.getHandCount() > 0 && handInfo.getHands() != null) {
            for (BefHandInfo.BefHand hand : handInfo.getHands()) {
                //    {zh} 绘制手框        {en} Draw hand frame  
                // draw hand box
                Rect rect = hand.getRect();
                float left = transformCenterAlign(rect.left, 1 / mResizeRatio);
                float right = transformCenterAlign(rect.right, 1 / mResizeRatio);
                float top = transformCenterAlign(rect.top, 1 / mResizeRatio);
                float bottom = transformCenterAlign(rect.bottom, 1 / mResizeRatio);
                mLineProgram.drawRect(new RectF(left, top, right, bottom), Color.RED, 1f);

                if (hand.getKeyPoints() != null && hand.getKeyPoints().length == 22) {

                    //    {zh} 绘制手指线段        {en} Draw finger line segment  
                    // draw finger segment
                    PointF[] points = new PointF[5];
                    points[0] = hand.getKeyPoints()[0].asPoint();
                    points[0].x = transformCenterAlign(points[0].x, 1 / mResizeRatio);
                    points[0].y = transformCenterAlign(points[0].y, 1 / mResizeRatio);
                    for (int n = 0; n < 5; n++) {
                        int index = 4 * n + 1;
                        for (int k = 1; k < 5; k++) {
                            points[k] = hand.getKeyPoints()[index++].asPoint();
                            points[k].x = transformCenterAlign(points[k].x, 1 / mResizeRatio);
                            points[k].y = transformCenterAlign(points[k].y, 1 / mResizeRatio);
                        }
                        mLineProgram.drawLineStrip(points, Color.RED, DRAW_POINT_SIZE);
                    }

                    //    {zh} 绘制关键点        {en} Draw key points  
                    // draw key point
                    for (BefHandInfo.BefKeyPoint point : hand.getKeyPoints()) {
                        PointF target = point.asPoint();
                        target.x = transformCenterAlign(target.x, 1 / mResizeRatio);
                        target.y = transformCenterAlign(target.y, 1 / mResizeRatio);
                        mPointProgram.draw(target, Color.RED, DRAW_POINT_SIZE * 2);
                    }
                } else {
                    LogUtils.e("hand.getKeyPoints() == null");
                }

            }
        }
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
    }

    /** {zh}
     * 绘制手势结果
     * draw gestures
     * @param mattingMask
     * @param texture
     */
    /**
     * {en}
     * Draw gestures result
     * draw gestures
     *
     * @param mattingMask
     * @param texture
     */

    public void drawMattingMask(PortraitMatting.MattingMask mattingMask, int texture) {
        if (mPortraitMaskProgram == null)
            mPortraitMaskProgram = new HairMaskProgram(mContext, mViewPortWidth, mViewPortHeight, mPortraitColor);
//            mPortraitMaskProgram = new PortraitMaskProgram(mContext, mViewPortWidth, mViewPortHeight,
//                    GlUtil.createImageTexture(BitmapFactory.decodeResource(mContext.getResources(), R.drawable.bg_portrait)));

        if (mOriginVertexBuffer == null) {
            mOriginVertexBuffer = ByteBuffer.allocateDirect(TextureRotationUtil.CUBE.length * 4)
                    .order(ByteOrder.nativeOrder())
                    .asFloatBuffer();
            mOriginVertexBuffer.clear();
            mOriginVertexBuffer.put(TextureRotationUtil.CUBE).position(0);
        }
        GLES20.glViewport(0, 0, mViewPortWidth, mViewPortHeight);
        mPortraitMaskProgram.drawMask(mattingMask.getBuffer(), mattingMask.getWidth(), mattingMask.getHeight(),
                mOriginVertexBuffer, mGLTextureBuffer, mFrameBuffers[0], texture);
    }

    public void drawHeadSegment(BefHeadSegInfo info, int texture) {
        if (mHeadMaskProgram == null)
            mHeadMaskProgram = new HairMaskProgram(mContext, mViewPortWidth, mViewPortHeight, FRAGMENT_AFFINE, mHeadColor);
        if (mOriginVertexBuffer == null) {
            mOriginVertexBuffer = ByteBuffer.allocateDirect(TextureRotationUtil.CUBE.length * 4)
                    .order(ByteOrder.nativeOrder())
                    .asFloatBuffer();
            mOriginVertexBuffer.clear();
            mOriginVertexBuffer.put(TextureRotationUtil.CUBE).position(0);
        }
        GLES20.glViewport(0, 0, mViewPortWidth, mViewPortHeight);

        for (BefHeadSegInfo.HeadSeg data : info.data) {
            mHeadMaskProgram.setRatio(mResizeRatio);
            mHeadMaskProgram.setAffine(data.matrix, data.width, data.height);
            mHeadMaskProgram.drawMask(data.alpha, data.width, data.height,
                    mOriginVertexBuffer, mGLTextureBuffer, mFrameBuffers[0], texture);
        }
    }


    public void drawHairMask(HairParser.HairMask hairMask, int texture) {
        if (mHairMaskProgram == null)
            mHairMaskProgram = new HairMaskProgram(mContext, mViewPortWidth, mViewPortHeight, mHairColor);
        if (mOriginVertexBuffer == null) {
            mOriginVertexBuffer = ByteBuffer.allocateDirect(TextureRotationUtil.CUBE.length * 4)
                    .order(ByteOrder.nativeOrder())
                    .asFloatBuffer();
            mOriginVertexBuffer.clear();
            mOriginVertexBuffer.put(TextureRotationUtil.CUBE).position(0);
        }
        GLES20.glViewport(0, 0, mViewPortWidth, mViewPortHeight);

        mHairMaskProgram.drawMask(hairMask.getBuffer(), hairMask.getWidth(), hairMask.getHeight(),
                mOriginVertexBuffer, mGLTextureBuffer, mFrameBuffers[0], texture);
    }

    public void drawSkyMask(BefSkyInfo skyInfo, int texture) {
        SkySegment.SkyMask skyMask = skyInfo.getSkyMask();
        if (mSkyMaskProgram == null) {
            mSkyMaskProgram = new HairMaskProgram(mContext, mViewPortWidth, mViewPortHeight, mSkyColor);
        }
        if (mOriginVertexBuffer == null) {
            mOriginVertexBuffer = ByteBuffer.allocateDirect(TextureRotationUtil.CUBE.length * 4)
                    .order(ByteOrder.nativeOrder())
                    .asFloatBuffer();
            mOriginVertexBuffer.clear();
            mOriginVertexBuffer.put(TextureRotationUtil.CUBE).position(0);
        }
        GLES20.glViewport(0, 0, mViewPortWidth, mViewPortHeight);

        mSkyMaskProgram.drawMask(skyMask.getBuffer(), skyMask.getWidth(), skyMask.getHeight(),
                mOriginVertexBuffer, mGLTextureBuffer, mFrameBuffers[0], texture);
    }

    public void drawGazeEstimation(BefGazeEstimationInfo gazeEstimationInfo, int texture) {
        if (mLineProgram == null) {
            mLineProgram = new LineProgram(mContext, mViewPortWidth, mViewPortHeight);
        }
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, mFrameBuffers[0]);
        GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0,
                GLES20.GL_TEXTURE_2D, texture, 0);

        GLES20.glViewport(0, 0, mViewPortWidth, mViewPortHeight);

        if (gazeEstimationInfo != null && gazeEstimationInfo.getInfos() != null) {
            for (BefGazeEstimationInfo.BefGazeEstimation gazeEstimation : gazeEstimationInfo.getInfos()) {
                if (!gazeEstimation.isValid()) continue;
                PointF[] pointFS = new PointF[4];
                pointFS[0] = new PointF(transformCenterAlign(gazeEstimation.getLeye_pos2d()[0]), transformCenterAlign(gazeEstimation.getLeye_pos2d()[1]));
                pointFS[1] = new PointF(transformCenterAlign(gazeEstimation.getLeye_gaze_2d()[0]), transformCenterAlign(gazeEstimation.getLeye_gaze_2d()[1]));
                pointFS[2] = new PointF(transformCenterAlign(gazeEstimation.getReye_pos2d()[0]), transformCenterAlign(gazeEstimation.getReye_pos2d()[1]));
                pointFS[3] = new PointF(transformCenterAlign(gazeEstimation.getReye_gaze2d()[0]), transformCenterAlign(gazeEstimation.getReye_gaze2d()[1]));

                mLineProgram.drawLines(pointFS, Color.RED, 3);
            }
        }

        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
    }

    public void drawCarInfo(BefCarDetectInfo carDetectInfo, int texture) {
        if (mLineProgram == null) {
            mLineProgram = new LineProgram(mContext, mViewPortWidth, mViewPortHeight);
        }
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, mFrameBuffers[0]);
        GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0,
                GLES20.GL_TEXTURE_2D, texture, 0);

        GLES20.glViewport(0, 0, mViewPortWidth, mViewPortHeight);

        for (BefCarDetectInfo.BefCarRect carRect : carDetectInfo.getCarRects()) {
            float left = transformCenterAlign(carRect.getLeft(), 1 / mResizeRatio);
            float right = transformCenterAlign(carRect.getRight(), 1 / mResizeRatio);
            float top = transformCenterAlign(carRect.getTop(), 1 / mResizeRatio);
            float bottom = transformCenterAlign(carRect.getBottom(), 1 / mResizeRatio);
            mLineProgram.drawRect(new RectF(left, top, right, bottom), Color.RED, 3);
        }

        for (BefCarDetectInfo.BefBrandInfo brandInfo : carDetectInfo.getBrandInfos()) {
            float left = transformCenterAlign(brandInfo.getPoints()[1].getX(), 1 / mResizeRatio);
            float right = transformCenterAlign(brandInfo.getPoints()[3].getX(), 1 / mResizeRatio);
            float top = transformCenterAlign(brandInfo.getPoints()[1].getY(), 1 / mResizeRatio);
            float bottom = transformCenterAlign(brandInfo.getPoints()[3].getY(), 1 / mResizeRatio);
            mLineProgram.drawRect(new RectF(left, top, right, bottom), Color.RED, 3);
        }

        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
    }

    public void drawFaceSegment(BefFaceInfo faceInfo, int texture) {
        if (mFaceSegmentProgram == null)
            mFaceSegmentProgram = new FaceSegmentProgram(mContext, mViewPortWidth, mViewPortHeight, FaceSegmentProgram.DRAW_FACE_MASK_FRAGMENT_SHADER);
        if (mOriginVertexBuffer == null) {
            mOriginVertexBuffer = ByteBuffer.allocateDirect(TextureRotationUtil.CUBE.length * 4)
                    .order(ByteOrder.nativeOrder())
                    .asFloatBuffer();
            mOriginVertexBuffer.clear();
            mOriginVertexBuffer.put(TextureRotationUtil.CUBE).position(0);
        }
        GLES20.glViewport(0, 0, mViewPortWidth, mViewPortHeight);

        for (BefFaceInfo.FaceMaskInfo faceMask : faceInfo.getFaceMaskInfo()) {
            float[] maskColor = new float[]{0.0f, 0.0f, 1.0f};
            mFaceSegmentProgram.setRatio(mResizeRatio);
            mFaceSegmentProgram.setWrapMat(faceMask.warp_mat);
            mFaceSegmentProgram.drawSegment(faceMask.mask, maskColor, 256, 256,
                    mOriginVertexBuffer, mGLTextureBuffer, mFrameBuffers[0], texture);
        }

        for (BefFaceInfo.FaceMaskInfo mouthMask : faceInfo.getMouthMaskInfo()) {
            float[] maskColor = new float[]{1.0f, 0.0f, 0.0f};
            mFaceSegmentProgram.setRatio(mResizeRatio);
            mFaceSegmentProgram.setWrapMat(mouthMask.warp_mat);
            mFaceSegmentProgram.drawSegment(mouthMask.mask, maskColor, 256, 256,
                    mOriginVertexBuffer, mGLTextureBuffer, mFrameBuffers[0], texture);
        }

        for (BefFaceInfo.FaceMaskInfo teethMask : faceInfo.getTeethMaskInfo()) {
            float[] maskColor = new float[]{0.0f, 1.0f, 0.0f};
            mFaceSegmentProgram.setRatio(mResizeRatio);
            mFaceSegmentProgram.setWrapMat(teethMask.warp_mat);
            mFaceSegmentProgram.drawSegment(teethMask.mask, maskColor, 256, 256,
                    mOriginVertexBuffer, mGLTextureBuffer, mFrameBuffers[0], texture);
        }

    }

    public void drawSkinSegmentationMask(BefSkinSegInfo info, int texture) {
        if (mSkinSegMaskProgram == null) {
            mSkinSegMaskProgram = new HairMaskProgram(mContext, mViewPortWidth, mViewPortHeight, mSkinSegColor);
        }
        if (mOriginVertexBuffer == null) {
            mOriginVertexBuffer = ByteBuffer.allocateDirect(TextureRotationUtil.CUBE.length * 4)
                    .order(ByteOrder.nativeOrder())
                    .asFloatBuffer();
            mOriginVertexBuffer.clear();
            mOriginVertexBuffer.put(TextureRotationUtil.CUBE).position(0);
        }
        GLES20.glViewport(0, 0, mViewPortWidth, mViewPortHeight);

        mSkinSegMaskProgram.drawMask(info.getMask(), info.getWidth(), info.getHeight(),
                mOriginVertexBuffer, mGLTextureBuffer, mFrameBuffers[0], texture);
    }

    public void drawBachSkeleton(BefBachSkeletonInfo skeletonInfo, int textureid) {
        if (mPointProgram == null) {
            mPointProgram = new PointProgram(mContext, mViewPortWidth, mViewPortHeight);
        }
        if (mLineProgram == null) {
            mLineProgram = new LineProgram(mContext, mViewPortWidth, mViewPortHeight);
        }

        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, mFrameBuffers[0]);
        GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0,
                GLES20.GL_TEXTURE_2D, textureid, 0);

        GLES20.glViewport(0, 0, mViewPortWidth, mViewPortHeight);

        int[] armsp = {0, 1, 1, 2, 2, 3, 3, 4, 4, 5, 5, 6, 6, 7, 7, 8, 8, 9, 9, 10, 10, 11, 11,
                12, 12, 13, 13, 14, 14, 15, 15, 16, 16, 17, 17, 18, 18, 19, 19, 20, 20,
                21, 21, 22, 22, 23, 23, 24, 23, 25, 25, 26, 26, 27, 27, 28, 28, 29, 29,
                30, 30, 31, 31, 32, 32, 33, 33, 35, 35, 34, 35, 36, 36, 37, 37, 38, 38,
                39, 39, 40, 40, 41, 41, 42, 42, 43, 43, 44, 44, 45, 45, 46, 46, 47, 47,
                48, 48, 49, 49, 50, 50, 51, 51, 52, 52, 53, 53, 54, 54, 55, 55, 56, 56,
                57, 57, 58, 58, 59, 59, 60, 60, 61, 61, 62, 62, 0
        };

        final int contour_colors[] = {
                255, 128,   0, 255, 128,   0, 255, 128,   0, 255, 128,   0, 255, 128,   0, 255, 128,
                0, 255, 128,   0, 255,  51, 255, 255,  51, 255, 255,  51, 255, 255,  51, 255, 255,
                51, 255,  51, 153, 255,  51, 153, 255,  51, 153, 255,  51, 153, 255,  51, 153, 255,
                51, 153, 255,  51, 153, 255,  51, 153, 255,  51, 153, 255,  51, 153, 255,  51, 153, 255,
                51, 153, 255,   0, 255,   0,   0, 255,   0,   0, 255,   0,   0, 255,   0,   0, 255,
                0,   0, 255,   0,   0, 255,   0,   0, 255,   0,   0, 255,   0,   0, 255,   0,
                51, 153, 255,  51, 153, 255,  51, 153, 255,  51, 153, 255,  51, 153, 255,
                51, 153, 255,  51, 153, 255,  51, 153, 255,  51, 153, 255,  51, 153, 255,
                51, 153, 255,  51, 153, 255, 255,  51, 255, 255,  51, 255, 255,  51, 255, 255,
                51, 255, 255,  51, 255, 255, 128,   0, 255, 128,   0, 255, 128, 0, 255, 128,
                0, 255, 128,   0, 255, 128,   0, 255, 128,   0, 230, 230,   0, 230, 230,
                0, 230, 230,   0, 230, 230,   0, 230, 230,   0
        };

        for (BefBachSkeletonInfo.Skeleton skeleton : skeletonInfo.getSkeletons()) {
            for (BefBachSkeletonInfo.SkeletonPoint skeletonPoint : skeleton.getKeypoints()) {
                if (skeletonPoint != null && skeletonPoint.isDetect()) {
                    PointF target = skeletonPoint.asPoint();
                    target.x = transformCenterAlign(target.x, 1 / mResizeRatio);
                    target.y = transformCenterAlign(target.y, 1 / mResizeRatio);
                    mPointProgram.draw(target, Color.BLUE, DRAW_POINT_SIZE * 2);
                }
            }
            BefFaceInfo.FaceRect rect = skeleton.getSkeletonRect();
            float left = transformCenterAlign(rect.getLeft(), 1 / mResizeRatio);
            float right = transformCenterAlign(rect.getRight(), 1 / mResizeRatio);
            float top = transformCenterAlign(rect.getTop(), 1 / mResizeRatio);
            float bottom = transformCenterAlign(rect.getBottom(), 1 / mResizeRatio);

            mLineProgram.drawRect(new RectF(left, top, right, bottom), Color.RED, DRAW_POINT_SIZE);

            ArrayList<PointF> skeleps = new ArrayList<PointF>();
            ArrayList<Float> colors = new ArrayList<Float>();
            for (int armi = 0,j=0; armi < armsp.length; armi += 2) {
                BefBachSkeletonInfo.SkeletonPoint armp = skeleton.getKeypoints()[armsp[armi]];
                BefBachSkeletonInfo.SkeletonPoint armpn = skeleton.getKeypoints()[armsp[armi + 1]];
                if (armp.isDetect() && armpn.isDetect()) {
                    skeleps.add(new PointF(transformCenterAlign(armp.asPoint().x, 1 / mResizeRatio), transformCenterAlign(armp.asPoint().y, 1 / mResizeRatio)));
                    skeleps.add(new PointF(transformCenterAlign(armpn.asPoint().x, 1 / mResizeRatio), transformCenterAlign(armpn.asPoint().y, 1 / mResizeRatio)));
                    colors.add(new Float((float) contour_colors[j] / 255.0f));
                    colors.add(new Float((float) contour_colors[j+1] / 255.0f));
                    colors.add(new Float((float) contour_colors[j+2] / 255.0f));
                    colors.add(new Float((float) contour_colors[j] / 255.0f));
                    colors.add(new Float((float) contour_colors[j+1] / 255.0f));
                    colors.add(new Float((float) contour_colors[j+2] / 255.0f));
                    j += 3;
                }
            }
            // draw line color
            if (mLineColorProgram == null) {
                mLineColorProgram = new LineColorProgram(mContext, mViewPortWidth, mViewPortHeight);
            }

            PointF[] points = skeleps.toArray(new PointF[0]);

            final float[] cols = new float[colors.size()];
            int index = 0;
            for (final Float value: colors) {
                cols[index++] = value;
            }
            mLineColorProgram.drawLines(points, cols, DRAW_LINE_SIZE);
        }

        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
    }

    public void drawChromaKeyingResult(BefChromaKeyingInfo info, int texture) {
        if (mChromaKeyingMaskProgram == null) {
            mChromaKeyingMaskProgram = new HairMaskProgram(mContext, mViewPortWidth, mViewPortHeight, mChromaKeyingColor);
        }
        if (mOriginVertexBuffer == null) {
            mOriginVertexBuffer = ByteBuffer.allocateDirect(TextureRotationUtil.CUBE.length * 4)
                    .order(ByteOrder.nativeOrder())
                    .asFloatBuffer();
            mOriginVertexBuffer.clear();
            mOriginVertexBuffer.put(TextureRotationUtil.CUBE).position(0);
        }
        GLES20.glViewport(0, 0, mViewPortWidth, mViewPortHeight);

        byte[] mask = new byte[info.getWidth() * info.getHeight()];

        for (int i = 0; i < info.getWidth() * info.getHeight(); ++i) {
            mask[i] = (byte)(255 - info.getMask()[i]);
        }

        mChromaKeyingMaskProgram.drawMask(mask, info.getWidth(), info.getHeight(),
                mOriginVertexBuffer, mGLTextureBuffer, mFrameBuffers[0], texture);
    }

    private void destroyFrameBuffers() {
        if (mFrameBuffers != null) {
            GLES20.glDeleteFramebuffers(FRAME_BUFFER_NUM, mFrameBuffers, 0);
            mFrameBuffers = null;
        }
    }

    /** {zh}
     * frameBuffer和texture的初始化
     * init frame buffer and texture
     */
    /**
     * {en}
     * FrameBuffer and texture initialization
     * init frame buffer and texture
     */

    private void initFrameBuffers() {
        destroyFrameBuffers();

        if (mFrameBuffers == null) {
            mFrameBuffers = new int[FRAME_BUFFER_NUM];

            GLES20.glGenFramebuffers(FRAME_BUFFER_NUM, mFrameBuffers, 0);
        }
    }

    /** {zh}
     * 纹理参数设置+buffer绑定
     * set texture params
     * and bind buffer
     */
    /**
     * {en}
     * Texture parameter setting + buffer binding
     * set texture params
     * and binding buffer
     */

    private void bindFrameBuffer(int textureId, int frameBuffer, int width, int height) {
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);
        GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, width, height, 0,
                GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, null);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);

        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, frameBuffer);
        GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0,
                GLES20.GL_TEXTURE_2D, textureId, 0);

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
    }

    public final void destroy() {
        mIsInitialized = false;

        mViewPortWidth = 0;
        mViewPortHeight = 0;

        destroyFrameBuffers();
        GLES20.glDeleteProgram(mArrayPrograms.get(0).get(PROGRAM_ID));
        mArrayPrograms.get(0).put(PROGRAM_ID, 0);
        GLES20.glDeleteProgram(mArrayPrograms.get(1).get(PROGRAM_ID));
        mArrayPrograms.get(1).put(PROGRAM_ID, 0);

        if (mPointProgram != null) {
            mPointProgram.release();
            mPointProgram = null;
        }

        if (mLineProgram != null) {
            mLineProgram.release();
            mLineProgram = null;
        }

        if (mPortraitMaskProgram != null) {
            mPortraitMaskProgram.release();
            mPortraitMaskProgram = null;
        }

        if (mHairMaskProgram != null) {
            mHairMaskProgram.release();
            mHairMaskProgram = null;
        }

        if (mHeadMaskProgram != null) {
            mHeadMaskProgram.release();
            mHeadMaskProgram = null;
        }

        if (mSkyMaskProgram != null) {
            mSkyMaskProgram.release();
            mSkyMaskProgram = null;
        }

        if (mFaceSegmentProgram != null) {
            mFaceSegmentProgram.release();
            mFaceSegmentProgram = null;
        }

        if (mDashLineProgram != null) {
            mDashLineProgram.release();
            mDashLineProgram = null;
        }

        if (mCircleProgram != null) {
            mCircleProgram.release();
            mCircleProgram = null;
        }

        if (mSkinSegMaskProgram != null) {
            mSkinSegMaskProgram.release();
            mSkinSegMaskProgram = null;
        }

        if (mChromaKeyingMaskProgram != null) {
            mChromaKeyingMaskProgram.release();
            mChromaKeyingMaskProgram = null;
        }
    }

    public void drawPetFaces(BefPetFaceInfo petFaceInfo, int texture) {
        if (mLineProgram == null) {
            mLineProgram = new LineProgram(mContext, mViewPortWidth, mViewPortHeight);
        }
        if (mPointProgram == null) {
            mPointProgram = new PointProgram(mContext, mViewPortWidth, mViewPortHeight);
        }

        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, mFrameBuffers[0]);
        GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0,
                GLES20.GL_TEXTURE_2D, texture, 0);

        GLES20.glViewport(0, 0, mViewPortWidth, mViewPortHeight);

        //   {zh} 绘制脸框     {en} Draw face frame 
        // draw face box
        for (BefPetFaceInfo.PetFace face90 : petFaceInfo.getFace90()) {
            BefFaceInfo.FaceRect rect = face90.getRect();
            float left = transformCenterAlign(rect.getLeft(), 1 / mResizeRatio);
            float right = transformCenterAlign(rect.getRight(), 1 / mResizeRatio);
            float top = transformCenterAlign(rect.getTop(), 1 / mResizeRatio);
            float bottom = transformCenterAlign(rect.getBottom(), 1 / mResizeRatio);

            mLineProgram.drawRect(new RectF(left, top, right, bottom), Color.RED, DRAW_POINT_SIZE);

            for (BefFaceInfo.FacePoint point : face90.getPoints_array()) {
                PointF target = point.asPoint();
                target.x = transformCenterAlign(target.x, 1 / mResizeRatio);
                target.y = transformCenterAlign(target.y, 1 / mResizeRatio);

                mPointProgram.draw(target, Color.RED, DRAW_POINT_SIZE);
            }
        }

        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
    }

    public void drawGeneralObject(BefGeneralObjectInfo generalObjectInfo, int texture) {
        if (mLineProgram == null) {
            mLineProgram = new LineProgram(mContext, mViewPortWidth, mViewPortHeight);
        }

        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, mFrameBuffers[0]);
        GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0,
                GLES20.GL_TEXTURE_2D, texture, 0);

        GLES20.glViewport(0, 0, mViewPortWidth, mViewPortHeight);

        // draw rect
        for (BefGeneralObjectInfo.ObjectInfo objectInfo : generalObjectInfo.getInfos()) {
            BefGeneralObjectInfo.ObjectRect rect = objectInfo.getBox();
            float left = transformCenterAlign(rect.getLeft(), 1 / mResizeRatio);
            float right = transformCenterAlign(rect.getRight(), 1 / mResizeRatio);
            float top = transformCenterAlign(rect.getTop(), 1 / mResizeRatio);
            float bottom = transformCenterAlign(rect.getBottom(), 1 / mResizeRatio);

            mLineProgram.drawRect(new RectF(left, top, right, bottom), Color.RED, DRAW_POINT_SIZE);
        }

        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
    }

    //    {zh} 切换分辨率的时候，需要调用这个函数，program 内部缓存了宽高来进行进行点的切换        {en} When switching the resolution, you need to call this function, and the width and height are cached inside the program to switch points  
    public void setGlProgramWidthAndHeight(int width, int height) {
        if (mLineProgram != null) {
            mLineProgram.setHeight(height);
            mLineProgram.setWidth(width);
        }

        if (mPointProgram != null) {
            mPointProgram.setHeight(height);
            mPointProgram.setWidth(width);
        }

        if (mPortraitMaskProgram != null) {
            mPortraitMaskProgram.setWidth(width);
            mPortraitMaskProgram.setHeight(height);
        }

        if (mHairMaskProgram != null) {
            mHairMaskProgram.setWidth(width);
            mHairMaskProgram.setHeight(height);
        }

        if (mHairMaskProgram != null) {
            mHairMaskProgram.setWidth(width);
            mHairMaskProgram.setHeight(height);
        }

        if (mHeadMaskProgram != null) {
            mHeadMaskProgram.setWidth(width);
            mHeadMaskProgram.setHeight(height);
        }

        if (mSkyMaskProgram != null) {
            mSkyMaskProgram.setWidth(width);
            mSkyMaskProgram.setHeight(height);
        }

        if (mFaceSegmentProgram != null) {
            mFaceSegmentProgram.setWidth(width);
            mFaceSegmentProgram.setHeight(height);
        }
    }

    private int argbColor(float alpha, float red, float green, float blue) {
        return ((int) (alpha * 255.0f + 0.5f) << 24) |
                ((int) (red * 255.0f + 0.5f) << 16) |
                ((int) (green * 255.0f + 0.5f) << 8) |
                (int) (blue * 255.0f + 0.5f);
    }
}
