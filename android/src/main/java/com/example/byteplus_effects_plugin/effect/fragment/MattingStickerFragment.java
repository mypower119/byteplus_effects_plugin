package com.example.byteplus_effects_plugin.effect.fragment;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.byteplus_effects_plugin.common.fragment.TabBoardFragment;

import java.util.ArrayList;


public class MattingStickerFragment extends TabBoardFragment{
    private MattingStickerCallback mattingStickerCallback;
//    private boolean isEnabled = true;
//    private ButtonView mBvNone;
//    private ButtonView mBvUpload;
//    private int mLayoutRes = R.layout.fragment_matting;

    public interface MattingStickerCallback{
        void onClickEvent(View view);
    }

    public MattingStickerFragment() {

    }

    public MattingStickerFragment(ArrayList<Fragment> fragments, ArrayList<String> titles){
        setFragmentList(fragments);
        setTitleList(titles);
    }

//    public MattingStickerFragment(int layoutRes) {
//       mLayoutRes = layoutRes;
//    }

    public void setMattingStickerCallback(MattingStickerCallback mattingStickerCallback) {
        this.mattingStickerCallback = mattingStickerCallback;
    }

//    @Override
//    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable  ViewGroup container, @Nullable  Bundle savedInstanceState) {
//        return inflater.inflate(mLayoutRes, container, false);
//    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//        removeButtonImgDefault();
//        mBvNone = view.findViewById(R.id.bv_none_matting);
//        mBvNone.setOnClickListener(this);
//        mBvUpload = view.findViewById(R.id.bv_upload_matting);
//        mBvUpload.setOnClickListener(this);
//        view.findViewById(R.id.iv_close_board).setOnClickListener(this);
//        view.findViewById(R.id.iv_record_board).setOnClickListener(this);
//        view.findViewById(R.id.img_default).setVisibility(View.GONE);
    }
    //    @Override
//    public void onClick(View view) {
//
//        // UI 变化
//       if (view.getId() == R.id.bv_none_matting){
//           isEnabled = !isEnabled;
//       }
//       if (view.getId() == R.id.bv_upload_matting){
//           if (!isEnabled){
//               ToastUtils.show(getString(R.string.matting_open_first));
//               return;
//           }
//       }
//
//        if (null != mattingStickerCallback){
//            mattingStickerCallback.onClickEvent(view);
//        }
//    }

//    public void changeButtonIcon(int icon) {
//       mBvUpload.setIcon(icon);
//    }

    @Override
    public void onViewPagerSelected(int position) {

    }

    @Override
    public void onClickEvent(View view) {
        if (null != mattingStickerCallback){
            mattingStickerCallback.onClickEvent(view);
        }
    }

    @Override
    public void setData() {

    }

}
