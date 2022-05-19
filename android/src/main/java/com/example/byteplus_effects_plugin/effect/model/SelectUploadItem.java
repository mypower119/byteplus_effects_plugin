package com.example.byteplus_effects_plugin.effect.model;

import androidx.annotation.DrawableRes;

import com.example.byteplus_effects_plugin.common.model.ButtonItem;

/**
 * Created on 2022-01-20 18:00
 */
public class SelectUploadItem extends ButtonItem {
    private String path;

    public SelectUploadItem(@DrawableRes int icon) {
        setIcon(icon);
    }
    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
