package com.example.byteplus_effects_plugin.algorithm.render;

import android.content.Context;
import android.opengl.GLES20;

public class SkyMaskProgram extends MaskProgram {


    private float[] mMaskColor;
    private int mMaskColorLocation;

    public SkyMaskProgram(Context context, int width, int height, float[] maskColor) {
        super(context, width, height, FRAGMENT_HAIR);

        mMaskColorLocation = GLES20.glGetUniformLocation(mProgram, "maskColor");
        mMaskColor = maskColor;
    }

    @Override
    protected void onBindData() {
        GLES20.glUniform4f(mMaskColorLocation, mMaskColor[0], mMaskColor[1], mMaskColor[2], mMaskColor[3]);
    }

}
