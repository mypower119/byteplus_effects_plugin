package com.example.byteplus_effects_plugin.common.imgsrc.bitmap;

import android.graphics.Bitmap;
import android.graphics.SurfaceTexture;
import android.opengl.GLES20;
import android.widget.ImageView;

import com.example.byteplus_effects_plugin.common.imgsrc.ImageSourceProvider;
import com.example.byteplus_effects_plugin.core.opengl.GlUtil;
import com.bytedance.labcv.effectsdk.BytedEffectConstants;

/**
 * Created  on 2021/5/14 2:02 PM
 */
public class BitmapSourceImpl implements ImageSourceProvider<Bitmap> {
    private int mMaxTexureSize;
    private Bitmap mBitmap;
    private int srcTextureId = GlUtil.NO_TEXTURE;




    @Override
    public void update() {

    }


    @Override
    public void close() {
        GlUtil.deleteTextureId(srcTextureId);
        if (null != mBitmap){
            mBitmap.recycle();
            mBitmap = null;
        }

    }

    @Override
    public void open(Bitmap bitmap, SurfaceTexture.OnFrameAvailableListener onFrameAvailableListener) {

        mBitmap = bitmap;
        srcTextureId = GlUtil.createImageTexture(mBitmap);

    }

    @Override
    public BytedEffectConstants.TextureFormat getTextureFormat() {
        return BytedEffectConstants.TextureFormat.Texure2D;
    }

    @Override
    public int getTexture() {
        GlUtil.deleteTextureId(srcTextureId);
        srcTextureId = GlUtil.createImageTexture(mBitmap);
        return srcTextureId;
    }

    @Override
    public int getWidth() {
        return mBitmap==null?0:mBitmap.getWidth();
    }

    @Override
    public int getHeight() {
        return mBitmap==null?0:mBitmap.getHeight();
    }

    @Override
    public int getOrientation() {
        return 0;
    }

    @Override
    public boolean isFront() {
        return false;
    }

    @Override
    public float[] getFov() {
        return new float[0];
    }

    @Override
    public boolean isReady() {
        return (mBitmap != null && GLES20.glIsTexture(srcTextureId));
    }

    @Override
    public long getTimestamp() {
        return System.nanoTime();
    }

    @Override
    public ImageView.ScaleType getScaleType() {
        return ImageView.ScaleType.CENTER_INSIDE;
    }
}
