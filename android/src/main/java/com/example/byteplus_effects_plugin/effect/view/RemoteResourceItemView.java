package com.example.byteplus_effects_plugin.effect.view;

import android.content.Context;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.example.byteplus_effects_plugin.R;

public class RemoteResourceItemView extends RelativeLayout {

    private final String TAG = "ResourceItemView";

    public ImageView iv_icon;
//    public RemoteResourceDownloadProgressView dpv;
    //    ImageView iv_undownload;

    public RemoteResourceItemView(Context context) {
        super(context);
        iv_icon = findViewById(R.id.iv_icon);
//        dpv = findViewById(R.id.dpv);
//        dpv.setVisibility(View.INVISIBLE);
    }

    public void setProgress(float progress){
//        dpv.setProgress(progress);
    }

}
