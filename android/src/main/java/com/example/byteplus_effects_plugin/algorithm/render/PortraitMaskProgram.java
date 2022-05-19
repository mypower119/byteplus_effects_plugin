package com.example.byteplus_effects_plugin.algorithm.render;

import android.content.Context;
import android.opengl.GLES20;

/** {zh} 
 * 人体分割的绘制句柄
 */

/** {en}
 * Draw handle for human body segmentation
 */

public class PortraitMaskProgram extends MaskProgram {
    private int mBackgroundTextureLocation;
    private int mBackgroundTexture;

    public PortraitMaskProgram(Context context, int width, int height, int backgroundTexture) {
        super(context, width, height, FRAGMENT_PORTRAIT);

        mBackgroundTextureLocation = GLES20.glGetUniformLocation(mProgram, "backgroundTexture");
        mBackgroundTexture = backgroundTexture;
    }


    public PortraitMaskProgram(Context context, int width, int height, String fragmentShader, int backgroundTexture) {
        super(context, width, height, fragmentShader);

        mBackgroundTextureLocation = GLES20.glGetUniformLocation(mProgram, "backgroundTexture");
        mBackgroundTexture = backgroundTexture;
    }

    @Override
    protected void onBindData() {
        GLES20.glActiveTexture(GLES20.GL_TEXTURE2);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mBackgroundTexture);
        GLES20.glUniform1i(mBackgroundTextureLocation, 2);
    }

    @Override
    public void release() {
        super.release();
        GLES20.glDeleteTextures(1, new int[]{mBackgroundTexture}, 0);
    }
}
