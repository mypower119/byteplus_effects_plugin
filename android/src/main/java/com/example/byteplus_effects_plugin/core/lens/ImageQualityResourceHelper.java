package com.example.byteplus_effects_plugin.core.lens;

import android.content.Context;

import com.example.byteplus_effects_plugin.core.Config;

import java.io.File;

/**
 * Created on 2021/5/19 10:21
 */
public class ImageQualityResourceHelper implements ImageQualityResourceProvider {
    public static final String RESOURCE = "resource";

    private Context mContext;

    public ImageQualityResourceHelper(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public String getLicensePath() {
        return new File(new File(getResourcePath(), "LicenseBag.bundle"), Config.LICENSE_NAME).getAbsolutePath();
    }

    private String getResourcePath() {
        return mContext.getExternalFilesDir("assets").getAbsolutePath() + File.separator + RESOURCE;
    }
}
