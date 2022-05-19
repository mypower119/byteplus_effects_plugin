package com.example.byteplus_effects_plugin.sports.task;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.text.TextUtils;

import com.example.byteplus_effects_plugin.common.utils.BitmapUtils;
import com.example.byteplus_effects_plugin.core.util.LogUtils;

import java.io.File;

/**
 * Created on 2021/7/21 11:18
 */
public class BitmapSaveTask extends AsyncTask<Bitmap, Void, String> {
    private final BitmapSaveDelegate mDelegate;

    public BitmapSaveTask(BitmapSaveDelegate delegate) {
        this.mDelegate = delegate;
    }

    @Override
    protected String doInBackground(Bitmap... bitmaps) {
        if (bitmaps.length == 0) {
            return "";
        }

        File file = BitmapUtils.saveToLocal(bitmaps[0]);

        if (file != null && file.exists()) {
            return file.getAbsolutePath();
        } else {
            return "";
        }
    }

    @Override
    protected void onPostExecute(String path) {
        super.onPostExecute(path);
        if (mDelegate == null) {
            try {
                new File(path).delete();
            } catch (Exception ignored) {
            }
            LogUtils.e("SavePicTask save bitmap fail");
            return;
        }
        if (TextUtils.isEmpty(path) || !new File(path).exists()) {
            mDelegate.onSavePicFinished(false, null);
            return;
        }
        try {
            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.DATA, path);
            values.put(MediaStore.Images.Media.MIME_TYPE, "image/*");
            mDelegate.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        } catch (Exception e) {
            e.printStackTrace();
            mDelegate.onSavePicFinished(false, null);
            return;
        }
        mDelegate.onSavePicFinished(true, path);
    }

    public interface BitmapSaveDelegate {
        ContentResolver getContentResolver();
        void onSavePicFinished(boolean success, String path);
    }
}
