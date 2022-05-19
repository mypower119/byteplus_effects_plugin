package com.example.byteplus_effects_plugin.algorithm.render;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PointF;
import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * Created on 2021/7/22 14:42
 */
public class DashLineProgram extends ShaderProgram {

    private static final int COORDS_PER_VERTEX = 2;
    private static final int STRIDE = COORDS_PER_VERTEX * 4;
    private static final int POSITION_COUNT = 2;

    private final int mPositionLocation;
    private final int mColorLocation;
    private final int mInULocation;
    private final int mStripTextureLocation;
    private int mStripTexture;

    protected DashLineProgram(Context context, int width, int height) {
        super(context, VERTEX, FRAGMENT, width, height);

        mPositionLocation = GLES20.glGetAttribLocation(mProgram, "a_Position");
        mColorLocation = GLES20.glGetUniformLocation(mProgram, "color");
        mInULocation = GLES20.glGetAttribLocation(mProgram, "inU");
        mStripTextureLocation = GLES20.glGetUniformLocation(mProgram, "u_stippleTexture");
    }

    public void drawLine(PointF[] points, int color, float lineWidth) {
        useProgram();

        float[] newPoints = new float[points.length * POSITION_COUNT];
        FloatBuffer fb = ByteBuffer
                .allocateDirect(STRIDE * points.length)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();

        for (int i = 0; i < points.length; i++) {
            newPoints[i * 2] = transformX(points[i].x);
            newPoints[i * 2 + 1] = transformY(points[i].y);
        }

        fb.position(0);
        fb.put(newPoints);
        fb.position(0);
        GLES20.glVertexAttribPointer(mPositionLocation, POSITION_COUNT,
                GLES20.GL_FLOAT, false, STRIDE, fb);
        GLES20.glEnableVertexAttribArray(mPositionLocation);

        float start = 0.f;
        float end = 5.f;
        float[] ins = new float[points.length * 2];
        for (int i = 0; i < points.length; i++) {
            ins[i * 2] = start;
            ins[i * 2 + 1] = end;
        }
        FloatBuffer inFb = ByteBuffer
                .allocateDirect(STRIDE * points.length)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        inFb.position(0);
        inFb.put(ins);
        inFb.position(0);
        GLES20.glVertexAttribPointer(mInULocation, 1, GLES20.GL_FLOAT,
                false, 0, inFb);
        GLES20.glEnableVertexAttribArray(mInULocation);

        if (!GLES20.glIsTexture(mStripTexture)) {
            byte[] bytes = new byte[] { 125, 0, 0, 125 };
            ByteBuffer byteBuffer = ByteBuffer
                    .allocateDirect(bytes.length)
                    .order(ByteOrder.nativeOrder());
            byteBuffer.position(0);
            byteBuffer.put(bytes);
            byteBuffer.position(0);
            int[] textures = new int[1];
            GLES20.glGenTextures(1, textures, 0);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textures[0]);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                    GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                    GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                    GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_REPEAT);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                    GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_REPEAT);
            GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_ALPHA, 4, 1, 0,
                    GLES20.GL_ALPHA, GLES20.GL_UNSIGNED_BYTE, byteBuffer);

            mStripTexture = textures[0];
        }

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mStripTexture);
        GLES20.glUniform1i(mStripTextureLocation, 0);

        float r = Color.red(color) / 255f;
        float g = Color.green(color) / 255f;
        float b = Color.blue(color) / 255f;
        float a = Color.alpha(color) / 255f;
        GLES20.glUniform4f(mColorLocation, r, g, b, a);

        GLES20.glLineWidth(5);
        GLES20.glDrawArrays(GLES20.GL_LINE_STRIP, 0, points.length);

        GLES20.glDisableVertexAttribArray(mPositionLocation);
        GLES20.glDisableVertexAttribArray(mInULocation);
    }

    private static final String VERTEX = "precision mediump float;\n" +
            " attribute vec4 a_Position;\n" +
            " attribute float inU;\n" +
            " varying   float outU;\n" +
            "\n" +
            " void main()\n" +
            " {\n" +
            "     outU        = inU;\n" +
            "     gl_Position = a_Position;\n" +
            " }\n";
    private static final String FRAGMENT = "precision mediump float;\n" +
            " varying float     outU;\n" +
            " uniform sampler2D u_stippleTexture;\n" +
            " uniform vec4 color;\n" +
            "\n" +
            " void main()\n" +
            " {\n" +
            "     float stipple = texture2D(u_stippleTexture, vec2(outU, 0.0)).a;\n" +
            "     if (stipple < 0.1)\n" +
            "         discard;\n" +
            "     gl_FragColor = color;\n" +
            " }\n";
}
