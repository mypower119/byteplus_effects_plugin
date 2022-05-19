package com.example.byteplus_effects_plugin.algorithm.render;

import android.content.Context;
import android.opengl.GLES20;

public class HairMaskProgram extends MaskProgram {
    private float[] mMaskColor;
    private int mMaskColorLocation;

    public HairMaskProgram(Context context, int width, int height, float[] maskColor) {
        super(context, width, height, FRAGMENT_HAIR);

        mMaskColorLocation = GLES20.glGetUniformLocation(mProgram, "maskColor");
        mMaskColor = maskColor;
    }

    public HairMaskProgram(Context context, int width, int height,String shader,  float[] maskColor){
        super(context, width, height, FRAGMENT_AFFINE);

        mMaskColorLocation = GLES20.glGetUniformLocation(mProgram, "maskColor");
        mMaskColor = maskColor;
    }

    public void setMaskColor(float[] mMaskColor) {
        this.mMaskColor = mMaskColor;
    }

    @Override
    protected void onBindData() {
        GLES20.glUniform4f(mMaskColorLocation, mMaskColor[0], mMaskColor[1], mMaskColor[2], mMaskColor[3]);
    }
}
