package com.example.byteplus_effects_plugin.algorithm.render;

import android.content.Context;
import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

/**
 * draw color outer mask
 */
public class MaskProgram extends ShaderProgram {


    private int mPositionLocation;
    private int mCoordinatLocation;
    private int mMaskTextureLocaltion;
    private int mMaskWidthLocation;
    private int mMaskHeightLocation;

    private int[] mCachedTextureid = new int[]{ShaderHelper.NO_TEXTURE};

    private float mRatio = 1;
    private double[] mAffine;
    private int mAffineLocation;

    private int mVp_h;
    private int mVp_w;

    private int mMaskWidth;
    private int mMaskHeight;

    public void setRatio(float ratio) {
        mRatio = ratio;
    }

    public void setAffine(double[] m,int width,int height) {
        mAffine = m;
        mMaskWidth = width;
        mMaskHeight = height;
    }

    public void setAffine(float[] m,int width, int height) {
        double[] output = new double[m.length];
        for (int i = 0; i < m.length; i++)
        {
            output[i] = m[i];
        }
        mAffine = output;
        mMaskWidth = width;
        mMaskHeight = height;
    }

    // rgba
    public MaskProgram(Context context, int width, int height, String fragmentShader) {
        super(context, CAMERA_INPUT_VERTEX_SHADER, fragmentShader, width, height);

        mPositionLocation = GLES20.glGetAttribLocation(mProgram, "position");
        mCoordinatLocation = GLES20.glGetAttribLocation(mProgram, "inputTextureCoordinate");
        mMaskTextureLocaltion = GLES20.glGetUniformLocation(mProgram, "inputMaskTexture");
        mAffineLocation = GLES20.glGetUniformLocation(mProgram, "affine");
        mVp_w = GLES20.glGetUniformLocation(mProgram, "viewport_width");
        mVp_h = GLES20.glGetUniformLocation(mProgram, "viewport_height");
        mMaskWidthLocation = GLES20.glGetUniformLocation(mProgram, "mask_width");
        mMaskHeightLocation = GLES20.glGetUniformLocation(mProgram, "mask_height");
    }


    public void drawMask(byte[] bytes, int width, int height,
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
//            GLES20.glTexSubImage2D(GLES20.GL_TEXTURE_2D, 0, 0, 0, width, height,
//                GLES20.GL_SRC_ALPHA, GLES20.GL_UNSIGNED_BYTE, byteBuffer);
        }

        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, frameBuffer);
        GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0,
                GLES20.GL_TEXTURE_2D, origintextureid, 0);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mCachedTextureid[0]);
        GLES20.glUniform1i(mMaskTextureLocaltion, 0);
        onBindData();

        if (mAffine != null) {
            float[] m3 = new float[]{
                    (float) mAffine[0] * mRatio, (float) mAffine[3] * mRatio, 0.0f,
                    (float) mAffine[1] * mRatio, (float) mAffine[4] * mRatio, 0.0f,
                    (float) mAffine[2], (float) mAffine[5], 0.0f
            };

            GLES20.glUniformMatrix3fv(mAffineLocation, 1, false, m3, 0);
        } else if (mAffineLocation >= 0){
            float[] m3 = new float[]{
                    1.0f, 0.0f, 0.0f,
                    0.0f, 1.0f, 0.0f,
                    0.0f, 0.0f, 1.0f
            };
            GLES20.glUniformMatrix3fv(mAffineLocation, 1, false, m3, 0);
        }


        if (mVp_h >= 0 && mVp_w >= 0) {
            GLES20.glUniform1f(mVp_w, (float)mWidth);
            GLES20.glUniform1f(mVp_h, (float)mHeight);
        }

        if (mMaskWidth >= 0 && mMaskHeight >= 0) {
            GLES20.glUniform1f(mMaskWidthLocation, (float)mMaskWidth);
            GLES20.glUniform1f(mMaskHeightLocation, (float)mMaskHeight);
        }

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);

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

    private static final String CAMERA_INPUT_VERTEX_SHADER = "" +
            "attribute vec4 position;\n" +
            "attribute vec4 inputTextureCoordinate;\n" +
            "\n" +
            "varying vec2 textureCoordinate;\n" +
            "\n" +
            "void main()\n" +
            "{\n" +
            "	textureCoordinate = vec2(inputTextureCoordinate.x, 1.0 -inputTextureCoordinate.y);\n" +
            "	gl_Position = position;\n" +
            "}";


    protected static final String FRAGMENT_PORTRAIT =
            "precision mediump float;\n" +
                    "varying highp vec2 textureCoordinate;\n" +
                    " \n" +
                    "uniform sampler2D inputMaskTexture;\n" +
                    "uniform sampler2D backgroundTexture;\n" +
                    " \n" +
                    "void main()\n" +
                    "{\n" +
                    "     float maska = texture2D(inputMaskTexture, textureCoordinate).a;\n" +
                    "     vec4 backgroundColor = texture2D(backgroundTexture, textureCoordinate);\n" +
                    "     gl_FragColor = vec4(backgroundColor.rgb , 1.0-maska);\n" +
                    "}";
    protected static final String FRAGMENT_HAIR =
            "precision mediump float;\n" +
                    "varying highp vec2 textureCoordinate;\n" +
                    " \n" +
                    "uniform sampler2D inputMaskTexture;\n" +
                    "uniform vec4 maskColor;\n" +
                    " \n" +
                    "void main()\n" +
                    "{\n" +
                    "     float maska = texture2D(inputMaskTexture, textureCoordinate).a;\n" +
                    "     gl_FragColor = vec4(maskColor.rgb ,  maska * maskColor.a);\n" +
                    "}";

    public static final String FRAGMENT_AFFINE =
            "precision mediump float;\n" +
                    "varying highp vec2 textureCoordinate;\n" +
                    "uniform mat3 affine;\n" +
                    "uniform float viewport_width;\n" +
                    "uniform float viewport_height;\n" +
                    "uniform float mask_width;\n" +
                    "uniform float mask_height;\n" +
                    " \n" +
                    "uniform sampler2D inputMaskTexture;\n" +
                    "uniform sampler2D backgroundTexture;\n" +
                    "uniform vec4 maskColor;\n" +
                    " \n" +
                    "void main()\n" +
                    "{\n" +
                    "     vec2 image_coord = vec2(textureCoordinate.x * viewport_width, textureCoordinate.y * viewport_height);\n" +
                    "     vec3 affine_coord = affine * vec3(image_coord, 1.0);\n" +
                    "     vec2 tex_coord = vec2(affine_coord.x / mask_width, affine_coord.y / mask_height);\n" +
                    "     if (tex_coord.x > 1.0 || tex_coord.x < 0.0 || tex_coord.y > 1.0 || tex_coord.y < 0.0) {gl_FragColor = vec4(0.0, 0.0, 0.0, 0.0); return;}\n" +
//                    "     if (distance(tex_coord, vec2(0.5, 0.5)) > 0.5) {gl_FragColor = vec4(0.0, 0.0, 0.0, 0.0); return;}\n" +
                    "     float maska = texture2D(inputMaskTexture, tex_coord).a;\n" +
                    "     vec4 backgroundColor = texture2D(backgroundTexture, textureCoordinate);\n" +
                    "     gl_FragColor = vec4(maskColor.rgb , maska * maskColor.a);\n" +
                    "}";
    public static final String FRAGMENT_PORTRAIT_FOREGROUND =
            "precision mediump float;\n" +
                    "varying highp vec2 textureCoordinate;\n" +
                    " \n" +
                    "uniform sampler2D inputMaskTexture;\n" +
                    "uniform sampler2D backgroundTexture;\n" +
                    " \n" +
                    "void main()\n" +
                    "{\n" +
                    "     float maska = texture2D(inputMaskTexture, textureCoordinate).a;\n" +
                    "     vec4 backgroundColor = texture2D(backgroundTexture, textureCoordinate);\n" +
                    "     gl_FragColor = vec4(backgroundColor.rgb , maska);\n" +
                    "}";
}
