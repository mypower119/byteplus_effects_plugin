package com.example.byteplus_effects_plugin.algorithm.render;

import android.content.Context;
import android.graphics.PointF;
import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class LineColorProgram extends ShaderProgram {

    private static final int SIZE_OF_FLOAT = 4;
    private static final int COORDS_PER_VERTEX = 2;
    private static final int STRIDE = COORDS_PER_VERTEX * SIZE_OF_FLOAT;
    private static final int POSITION_COUNT = 2;
    private static final int COLOR_COUNT = 3;

    private final int mPositionLocation;
    private final int mColorLocation;

    //   {zh} 存储矩形顶点数据，从 drawRect() 方法中分离出啦，减少重复的初始化       {en} Stores rectangular vertex data, separated from drawRect () method, reducing repeated initialization
    // Store rectangle vertex data
    // Separate from the drawRect() method, reduce repetitive initialization
    private FloatBuffer mRectFb;
    private float[] mRectPoints;

    public LineColorProgram(Context context, int width, int height) {
        super(context, VERTEX, FRAGMENT, width, height);

        mPositionLocation = GLES20.glGetAttribLocation(mProgram, "a_Position");
        mColorLocation = GLES20.glGetAttribLocation(mProgram, "a_Color");

        mRectFb = ByteBuffer.allocateDirect(STRIDE * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        mRectPoints = new float[STRIDE];
    }

    public void drawLineStrip(PointF points[], float[] color, float linewidth) {
        drawLine(points, color, linewidth, true);
    }

    public void drawLines(PointF points[], float[] color, float linewidth) {
        drawLine(points, color, linewidth, false);
    }

    //   {zh} drawLinesTrip() 和 drawLines() 两个方法仅绘制类型不同，可以将逻辑统一到本方法里       {en} The drawLinesTrip () and drawLines () methods only have different drawing types, and the logic can be unified into this method
    private void drawLine(PointF[] points, float[] colors, float lineWidth, boolean strip) {
        useProgram();

        float[] pointpPosis = new float[points.length * POSITION_COUNT];
        FloatBuffer pointsBuffer = ByteBuffer
                .allocateDirect(STRIDE * points.length)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();

        for(int i = 0; i < points.length; i ++)
        {
            pointpPosis[i*2] = transformX(points[i].x);
            pointpPosis[i*2+1] = transformY(points[i].y);
        }

        pointsBuffer.position(0);
        pointsBuffer.put(pointpPosis);
        pointsBuffer.position(0);

        GLES20.glVertexAttribPointer(mPositionLocation, POSITION_COUNT, GLES20.GL_FLOAT,
                false, STRIDE, pointsBuffer);
        GLES20.glEnableVertexAttribArray(mPositionLocation);

        FloatBuffer colorsBuffer = ByteBuffer
                .allocateDirect(colors.length * SIZE_OF_FLOAT)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();

        colorsBuffer.position(0);
        colorsBuffer.put(colors);
        colorsBuffer.position(0);

        GLES20.glVertexAttribPointer(mColorLocation, COLOR_COUNT, GLES20.GL_FLOAT, false, 3 * SIZE_OF_FLOAT, colorsBuffer);
        GLES20.glEnableVertexAttribArray(mColorLocation);

        GLES20.glLineWidth(lineWidth);
        GLES20.glDrawArrays(strip ? GLES20.GL_LINE_STRIP : GLES20.GL_LINES, 0, points.length);

        GLES20.glDisableVertexAttribArray(mPositionLocation);
        GLES20.glDisableVertexAttribArray(mColorLocation);
    }

    private static final String VERTEX =
            "attribute vec4 a_Position;\n" +
            "attribute vec3 a_Color;\n" +
            "varying vec4 lineColor;\n" +
            "\n" +
            "void main() {\n" +
            "    gl_Position = a_Position;\n" +
            "    lineColor = vec4(a_Color, 1.0);\n" +
            "}";

    private static final String FRAGMENT = "precision mediump float;\n" +
            "\n" +
            "varying vec4 lineColor;\n" +
            "\n" +
            "void main() {\n" +
            "    gl_FragColor = lineColor;\n" +
            "}";

}
