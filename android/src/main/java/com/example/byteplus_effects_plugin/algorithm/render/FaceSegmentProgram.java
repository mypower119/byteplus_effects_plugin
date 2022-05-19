package com.example.byteplus_effects_plugin.algorithm.render;

import android.content.Context;
import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

/** {zh} 
 * 人体分割的绘制句柄
 */

/** {en}
 * Draw handle for human body segmentation
 */


public class FaceSegmentProgram extends ShaderProgram
{

    private int mPositionLocation;
    private int mCoordinatLocation;
    private int mMaskTextureLocaltion;
    private int mMaskColorLocaltion;
    private int mInpurtTextureLocaltion;


    private double[] mWrapMat;
    private int mWrapMatLocation;

    private int[] mCachedTextureid = new int[]{ShaderHelper.NO_TEXTURE};

    private float mRatio = 1;


    public void setRatio(float ratio) {
        mRatio = ratio;
    }

    public void setWrapMat(double[] m) {
        mWrapMat = m;
    }

    // rgba
    public FaceSegmentProgram(Context context, int width, int height, String fragmentShader) {
        super(context, DRAW_FACE_MASK_VERTEX_SHADER, fragmentShader, width, height);

        mPositionLocation = GLES20.glGetAttribLocation(mProgram, "position");
        mCoordinatLocation = GLES20.glGetAttribLocation(mProgram, "inputTextureCoordinate");
        mMaskTextureLocaltion = GLES20.glGetUniformLocation(mProgram, "inputMask");
        mMaskColorLocaltion= GLES20.glGetUniformLocation(mProgram, "maskColor");
//        mInpurtTextureLocaltion = GLES20.glGetUniformLocation(mProgram, "inputImageTexture");
        mWrapMatLocation = GLES20.glGetUniformLocation(mProgram, "warpMat");
    }


    public void drawSegment(byte[] bytes, float[] color, int width, int height,
                         FloatBuffer vertexBuffer, FloatBuffer textureBuffer, int frameBuffer, int origintextureid) {
        useProgram();
        vertexBuffer.position(0);
        GLES20.glVertexAttribPointer(mPositionLocation, 2, GLES20.GL_FLOAT, false, 0, vertexBuffer);
        GLES20.glEnableVertexAttribArray(mPositionLocation);

        textureBuffer.position(0);
        GLES20.glVertexAttribPointer(mCoordinatLocation, 2, GLES20.GL_FLOAT, false, 0, textureBuffer);
        GLES20.glEnableVertexAttribArray(mCoordinatLocation);

        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
        ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
        byteBuffer.position(0);
        if (mCachedTextureid[0] == ShaderHelper.NO_TEXTURE) {
            GLES20.glGenTextures(1, mCachedTextureid, 0);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mCachedTextureid[0]);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                    GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                    GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                    GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                    GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
            GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_ALPHA, width, height, 0,
                    GLES20.GL_ALPHA, GLES20.GL_UNSIGNED_BYTE, byteBuffer);
        } else {
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mCachedTextureid[0]);
            GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_ALPHA, width, height, 0,
                    GLES20.GL_ALPHA, GLES20.GL_UNSIGNED_BYTE, byteBuffer);
        }

        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, frameBuffer);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,  mCachedTextureid[0]);
        GLES20.glUniform1i(mMaskTextureLocaltion, 0);
        onBindData();

        float scaleX = mWidth / (width * 1.0f);
        float scaleY = mHeight / (height* 1.0f);

        if (mWrapMat != null) {
            float[] m4 = new float[]{
                    (float) mWrapMat[0] * mRatio * scaleX, (float) mWrapMat[3] * mRatio * scaleX, 0.0f, 0.0f,
                    (float) mWrapMat[1] * mRatio * scaleY, (float) mWrapMat[4] * mRatio * scaleY, 0.0f, 0.0f,
                    0.0f, 0.0f, 1.0f, 0.0f,
                    (float) mWrapMat[2] / width, (float) (mWrapMat[5]/ height), 0.0f, 1.0f
            };

            GLES20.glUniformMatrix4fv(mWrapMatLocation, 1, false, m4, 0);
        } else if (mWrapMatLocation >= 0){
            float[] m4 = new float[]{
                    1.0f, 0.0f, 0.0f,0.0f,
                    0.0f, 1.0f, 0.0f, 0.0f,
                    0.0f, 0.0f, 1.0f, 0.0f,
                    0.0f, 0.0f, 0.0f, 1.0f
            };
            GLES20.glUniformMatrix4fv(mWrapMatLocation, 1, false, m4, 0);
        }

        GLES20.glUniform3f(mMaskColorLocaltion, color[0], color[1], color[2]);


        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);

        GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0,
                GLES20.GL_TEXTURE_2D, origintextureid, 0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
        GLES20.glDisable(GLES20.GL_BLEND);


        GLES20.glDisableVertexAttribArray(mPositionLocation);
        GLES20.glDisableVertexAttribArray(mCoordinatLocation);
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
        GLES20.glUseProgram(0);
    }


    public void release() {
        super.release();
        if (mCachedTextureid[0] != ShaderHelper.NO_TEXTURE) {
            GLES20.glDeleteTextures(1, mCachedTextureid, 0);
        }
    }

    protected void onBindData() {
    }

    private static final String DRAW_FACE_MASK_VERTEX_SHADER = "" +
            "attribute vec4 position;\n" +
            "attribute vec4 inputTextureCoordinate;\n" +
            "\n" +
            "varying vec2 textureCoordinate;\n" +
            "varying vec2 maskCoordinate;\n" +
            "uniform mat4 warpMat;\n" +
            "\n" +
            "void main()\n" +
            "{\n" +
            "	textureCoordinate = vec2(inputTextureCoordinate.x, 1.0 - inputTextureCoordinate.y);\n" +
            "	gl_Position = position;\n" +
            "	maskCoordinate = (warpMat * vec4(textureCoordinate, 0.0, 1.0)).xy;\n" +
            "}";

    public static final String DRAW_FACE_MASK_FRAGMENT_SHADER =
                    "precision mediump float;\n" +
                            "varying highp vec2 textureCoordinate;\n" +
                            "varying highp vec2 maskCoordinate;\n" +
                            "uniform sampler2D inputMask;\n" +
                            "uniform vec3 maskColor;\n" +
                            "void main()\n" +
                            "{\n" +
                            "   vec4 color = vec4(0.0, 0.0, 0.0, 0.0);\n" +
                            "\n" +
                            "   vec2 maskCoordinate1 = vec2(maskCoordinate.x, maskCoordinate.y);\n" +
//                            "   if(clamp(maskCoordinate1, 0.0, 1.0) != maskCoordinate1)\n" +
                            "   if(maskCoordinate1.x > 1.0 || maskCoordinate1.x < 0.0 || maskCoordinate1.y > 1.0 || maskCoordinate1.y < 0.0 )\n" +
                            "   {\n" +
                            "       gl_FragColor = color;\n" +
                            "   }\n" +
                            "   else\n" +
                            "   {\n" +
                            "       float mask = texture2D(inputMask, maskCoordinate1).a;\n" +
                            "       gl_FragColor = vec4(maskColor, mask*0.45);\n" +
                            "   }\n" +
                            "}";
}


