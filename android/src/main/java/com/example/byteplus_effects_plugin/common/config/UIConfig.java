package com.example.byteplus_effects_plugin.common.config;

public class UIConfig {
    public static final String KEY = "uiConfig";
     private boolean enableRotate = true;
     private boolean enbaleAblum = true;

    public boolean isEnableRotate() {
        return enableRotate;
    }

    public UIConfig setEnableRotate(boolean enableRotate) {
        this.enableRotate = enableRotate;
        return this;

    }

    public boolean isEnbaleAblum() {
        return enbaleAblum;
    }

    public UIConfig setEnbaleAblum(boolean enbaleAblum) {
        this.enbaleAblum = enbaleAblum;
        return this;

    }
}
