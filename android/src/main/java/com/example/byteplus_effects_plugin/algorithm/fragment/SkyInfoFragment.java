package com.example.byteplus_effects_plugin.algorithm.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.example.byteplus_effects_plugin.R;
import com.bytedance.labcv.effectsdk.BefSkyInfo;

public class SkyInfoFragment extends AppCompatDialogFragment {

    private TextView tvSkyInfo;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return View.inflate(getContext(), R.layout.fragment_sky_info, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tvSkyInfo = view.findViewById(R.id.tv_sky_info);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public void resetProperty(boolean isAttrOn){
        tvSkyInfo.setText("无");
        tvSkyInfo.setVisibility(isAttrOn? View.VISIBLE:View.GONE);
        if (tvSkyInfo.getVisibility() == View.VISIBLE ) {
            tvSkyInfo.setText("");
        }
    }

    public void updateProperty(BefSkyInfo skyInfo, boolean isSkyAttrOn) {

        if (this.isVisible()) {
            // while  license error/ detect error will get a null faceinfo
            if ( skyInfo != null) {
                if (isSkyAttrOn) {
                    tvSkyInfo.setVisibility(View.VISIBLE);
                    tvSkyInfo.setText( skyInfo.getHasSky()? "有":"无" );
                } else {
                    tvSkyInfo.setVisibility(View.INVISIBLE);
                }
            }
        }

    }


    public void onClose() {
        resetProperty(false);
    }
}
