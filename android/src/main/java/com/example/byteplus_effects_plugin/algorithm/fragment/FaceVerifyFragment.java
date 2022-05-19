package com.example.byteplus_effects_plugin.algorithm.fragment;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
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
import com.example.byteplus_effects_plugin.core.util.LogUtils;
import com.example.byteplus_effects_plugin.R;
import com.dmcbig.mediapicker.PickerActivity;
import com.dmcbig.mediapicker.PickerConfig;
import com.dmcbig.mediapicker.entity.Media;

import java.util.ArrayList;

public class FaceVerifyFragment extends Fragment
        implements View.OnClickListener{
    private int REQUEST_ALBUM = 1001;

    private ButtonView bvUpload;
    private IFaceVerifyCallback mCallback;


    public interface IFaceVerifyCallback {
        void onPicChoose(Bitmap bitmap);

        void faceVerifyOn(boolean flag);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return LayoutInflater.from(getActivity()).inflate(R.layout.fragment_face_verify, container, false);
    }

    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        bvUpload = view.findViewById(R.id.bv_upload_face_verify);

        bvUpload.setOnClickListener(this);
        bvUpload.on();
        mCallback.faceVerifyOn(true);

    }

    public FaceVerifyFragment setCallback(IFaceVerifyCallback callback) {
        mCallback = callback;
        return this;
    }

    @Override
    public void onClick(View v) {
        if (CommonUtils.isFastClick()) {
            LogUtils.e("too fast click");
            return;
        }
        if (mCallback == null) return;
        int id = v.getId();
        if (id == R.id.bv_upload_face_verify) {
                chooseImg();

        }
    }

    private void chooseImg() {

        Intent intent =new Intent(getActivity(), PickerActivity.class);
        intent.putExtra(PickerConfig.SELECT_MODE,PickerConfig.PICKER_IMAGE);// {zh} 设置选择类型，默认是图片和视频可一起选择(非必填参数) {en} Set the selection type, the default is that pictures and videos can be selected together (non-required parameters)
        long maxSize=188743680L;// {zh} long long long long类型 {en} Long long long long type
        intent.putExtra(PickerConfig.MAX_SELECT_SIZE,maxSize); // {zh} 最大选择大小，默认180M（非必填参数） {en} Maximum selection size, default 180M (not required)
        intent.putExtra(PickerConfig.MAX_SELECT_COUNT,1);  // {zh} 最大选择数量，默认40（非必填参数） {en} Maximum number of selections, default 40 (not required)

        startActivityForResult(intent,REQUEST_ALBUM);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_ALBUM && resultCode == PickerConfig.RESULT_CODE) {
            ArrayList<Media> select = data.getParcelableArrayListExtra(PickerConfig.EXTRA_RESULT);// {zh} 选择完后返回的list {en} Returning list after selection
            if (null == select || select.size() == 0)return;
            String imagePath = select.get(0).path;
            displayImage(imagePath);
        }
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

    private void displayImage(String imagePath) {
        Bitmap bitmap = BitmapUtils.decodeBitmapFromFile(imagePath, 800, 800);
        if (bitmap != null && !bitmap.isRecycled()) {
            mCallback.onPicChoose(bitmap);
        } else {
            ToastUtils.show("failed to get image");
        }
    }
}
