package com.example.byteplus_effects_plugin.common.task;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.text.TextUtils;

import com.example.byteplus_effects_plugin.common.model.CaptureResult;
import com.example.byteplus_effects_plugin.common.utils.BitmapUtils;
import com.example.byteplus_effects_plugin.core.util.LogUtils;

import java.io.File;

/**
 * Created on 2020/8/4 15:52
 */

public class SavePicTask extends AsyncTask<CaptureResult, Void, String> {
    private final SavePicDelegate mDelegate;

    public SavePicTask(SavePicDelegate delegate) {
        mDelegate = (delegate);
    }

    @Override
    protected String doInBackground(CaptureResult... captureResults) {
        LogUtils.d("SavePicTask doInBackground enter");
        if (captureResults.length == 0) return "captureResult arrayLength is 0";
        Bitmap bitmap = Bitmap.createBitmap(captureResults[0].getWidth(), captureResults[0].getHeight(), Bitmap.Config.ARGB_8888);
        bitmap.copyPixelsFromBuffer(captureResults[0].getByteBuffer().position(0));
        File file = BitmapUtils.saveToLocal(bitmap);
        LogUtils.d("SavePicTask doInBackground finish");

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
            LogUtils.e("SavePicTask save picture fail");
            return;
        }
        if (TextUtils.isEmpty(path)) {
            mDelegate.onSavePicFinished(false, null);
            return;
        }
        try {
            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.DATA, path);
//            values.put(MediaStore.Images.Media.MIME_TYPE, "image/*");
            values.put(MediaStore.Images.Media.MIME_TYPE, "image/png");
            mDelegate.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        } catch (Exception e) {
            e.printStackTrace();
            mDelegate.onSavePicFinished(false, null);
            return;
        }
        mDelegate.onSavePicFinished(true, path);
    }

    public interface SavePicDelegate {
        ContentResolver getContentResolver();
        void onSavePicFinished(boolean success, String path);
    }
}