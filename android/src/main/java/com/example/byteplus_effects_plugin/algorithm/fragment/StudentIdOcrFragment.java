package com.example.byteplus_effects_plugin.algorithm.fragment;

import static android.app.Activity.RESULT_OK;

import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.byteplus_effects_plugin.common.utils.BitmapUtils;
import com.example.byteplus_effects_plugin.common.utils.CommonUtils;
import com.example.byteplus_effects_plugin.common.utils.ToastUtils;
import com.example.byteplus_effects_plugin.common.view.ButtonView;
import com.example.byteplus_effects_plugin.R;

public class StudentIdOcrFragment
        extends Fragment
        implements  View.OnClickListener{

    private int REQUEST_ALBUM = 1001;
    private ButtonView bvUpload;
    private IStudentIdOcrCallback mCallback;

    public interface IStudentIdOcrCallback{
        void onProcessImage(Bitmap bitmap);
    }

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return LayoutInflater.from(getActivity()).inflate(R.layout.fragment_student_id_ocr, container, false);
    }

    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bvUpload = view.findViewById(R.id.bv_student_id_ocr);
        bvUpload.setOnClickListener(this);
    }

    public StudentIdOcrFragment setCallback(IStudentIdOcrCallback callback) {
        mCallback = callback;
        return this;
    }

    @Override
    public void onClick(View v){
        if (CommonUtils.isFastClick()) {
            ToastUtils.show("too fast click");
            return;
        }
        if (v.getId() == R.id.bv_student_id_ocr) {
            chooseImg();
        }
    }

    private void chooseImg() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        if (intent.resolveActivity(getActivity().getPackageManager())!= null){
            startActivityForResult(intent, REQUEST_ALBUM);
        } else {
            ToastUtils.show(getString(R.string.ablum_not_supported));
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ALBUM && resultCode == RESULT_OK && null != data) {
            if (Build.VERSION.SDK_INT >= 19) {
                handleImageOnKitkat(data);
            } else {
                handleImageBeforeKitkat(data);
            }
        }
    }

    @TargetApi(19)
    private void handleImageOnKitkat(Intent data) {
        String imagePath = null;
        Uri uri = data.getData();
        String scheme = uri.getScheme();
        if (DocumentsContract.isDocumentUri(getActivity(), uri)) {
            String docId = DocumentsContract.getDocumentId(uri);
            String authority = uri.getAuthority();
            if ("com.android.providers.media.documents".equals(authority)) {
                String id = docId.split(":")[1];
                String selection = MediaStore.Images.Media._ID + "=" + id;
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
            } else if ("com.android.providers.downloads.documents".equals(authority)) {
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content:" +
                        "//downloads/public_downloads"), Long.valueOf(docId));
                imagePath = getImagePath(contentUri, null);
            }
        } else if ("content".equalsIgnoreCase(scheme)) {
            imagePath = getImagePath(uri, null);
        } else if ("file".equalsIgnoreCase(scheme)) {
            imagePath = uri.getPath();
        }
        processImage(imagePath);
    }

    private void handleImageBeforeKitkat(Intent data) {
        Uri uri = data.getData();
        String imagePath = getImagePath(uri, null);
        processImage(imagePath);

    }

    private String getImagePath(Uri uri, String selection) {
        String path = null;
        Cursor cursor = getActivity().getContentResolver().query(uri, null, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }

    private void processImage(String imagePath) {
        Bitmap bitmap = BitmapUtils.decodeBitmapFromFile(imagePath, 800, 800);
        if (bitmap != null && !bitmap.isRecycled()) {
            mCallback.onProcessImage(bitmap);
        } else {
            ToastUtils.show("failed to get image");
        }
    }
}
