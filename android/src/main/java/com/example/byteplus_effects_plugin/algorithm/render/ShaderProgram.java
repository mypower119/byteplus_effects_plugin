// Copyright (C) 2018 Beijing Bytedance Network Technology Co., Ltd.
package com.example.byteplus_effects_plugin.algorithm.render;

import android.content.Context;
import android.opengl.GLES20;
import android.text.TextUtils;

public class ShaderProgram {

    protected int mProgram = -1;

    protected int mWidth, mHeight;

    protected Context mContext;

    protected ShaderProgram(Context context, String vertex, String fragment, int width, int height) {
        mContext = context;

        if (!TextUtils.isEmpty(vertex) && !TextUtils.isEmpty(fragment)) {
            mProgram = ShaderHelper.buildProgram(vertex, fragment);
        }

        mWidth = width;
        mHeight = height;
    }

    protected float transformX(float x) {
        return 2 * x / mWidth - 1;
    }

    protected float transformY(float y) {
        return 2 * y / mHeight - 1;
    }

    public void useProgram() {
        if (mProgram >= 0) {
            GLES20.glUseProgram(mProgram);
        }
    }

    public void release() {
        if (mProgram >= 0) {
            GLES20.glDeleteProgram(mProgram);
        }
        mProgram = -1;
    }

    public void setWidth(int width){
        mWidth = width;
    }

    public void setHeight(int height){
        mHeight = height;
    }
}
