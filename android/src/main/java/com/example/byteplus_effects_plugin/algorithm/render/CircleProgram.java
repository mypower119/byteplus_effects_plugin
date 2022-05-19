package com.example.byteplus_effects_plugin.algorithm.render;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PointF;
import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * Created on 2021/7/28 17:49
 */
public class CircleProgram extends ShaderProgram {

    private static final int COORDS_PER_VERTEX = 2;
    private static final int STRIDE = COORDS_PER_VERTEX * 4;
    private static final int POSITION_COUNT = 2;
    private static final int VERTEX_COUNT = 50;

    private final int mPositionLocation;
    private final int mColorLocation;
    private final int mCircleOffsetLocation;
    private final float[] mVertexes;
    private final FloatBuffer mVertexBuffer;

    private float mOldRadius = -1;

    protected CircleProgram(Context context, int width, int height) {
        super(context, VERTEX, FRAGMENT, width, height);

        mPositionLocation = GLES20.glGetAttribLocation(mProgram, "aPosition");
        mCircleOffsetLocation = GLES20.glGetUniformLocation(mProgram, "aOffset");
        mColorLocation = GLES20.glGetUniformLocation(mProgram, "aColor");
        mVertexes = new float[VERTEX_COUNT * 2];
        mVertexBuffer = ByteBuffer.allocateDirect(STRIDE * VERTEX_COUNT)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
    }

    public void draw(PointF circlePosition, float radius, int color) {
        useProgram();

        calculateVertexes(radius);

        mVertexBuffer.position(0);
        GLES20.glVertexAttribPointer(mPositionLocation, 2, GLES20.GL_FLOAT, false, STRIDE, mVertexBuffer);
        GLES20.glEnableVertexAttribArray(mPositionLocation);

        GLES20.glUniform2f(mCircleOffsetLocation, transformX(circlePosition.x), transformY(circlePosition.y));

        float r = Color.red(color) / 255f;
        float g = Color.green(color) / 255f;
        float b = Color.blue(color) / 255f;
        float a = Color.alpha(color) / 255f;
        GLES20.glUniform4f(mColorLocation, r, g, b, a);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, VERTEX_COUNT);

        GLES20.glDisableVertexAttribArray(mPositionLocation);
        GLES20.glDisableVertexAttribArray(mCircleOffsetLocation);
    }

    private void calculateVertexes(float radius) {
        if (radius == mOldRadius) {
            return;
        }
        float radiusX = radius / mWidth;
        float radiusY = radius / mHeight;
        float delta = (float) (2.f * Math.PI / VERTEX_COUNT);

        for (int i = 0; i < VERTEX_COUNT; i++) {
            float x = (float) (radiusX * Math.cos(delta * i));
            float y = (float) (radiusY * Math.sin(delta * i));
            mVertexes[i * 2] = x;
            mVertexes[i * 2 + 1] = y;
        }

        mVertexBuffer.position(0);
        mVertexBuffer.put(mVertexes);
        mOldRadius = radius;
    }

    private static final String VERTEX = "attribute vec2 aPosition;\n" +
            "uniform vec2 aOffset;\n" +
            "void main() {\n" +
            "    gl_Position = vec4(aPosition + aOffset, 0.0, 1.0);\n" +
            "}";

    private static final String FRAGMENT = "" +
            "precision highp float;\n" +
            "uniform vec4 aColor; \n" +
            "void main() \n" +
            "{ \n" +
            "   gl_FragColor = aColor;" +
            "} \n";
}
