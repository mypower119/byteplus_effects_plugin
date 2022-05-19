package com.example.byteplus_effects_plugin.lens.config;

/**
 * Created on 2021/5/19 10:36
 */
public class ImageQualityConfig {
    public static final String IMAGE_QUALITY_KEY = "image_quality_key";

    public static final String KEY_VIDEO_SR = "video_sr";
    public static final String KEY_NIGHT_SCENE = "night_scene";
    public static final String KEY_ADAPTIVE_SHARPEN = "apdative_sharpen";

    public ImageQualityConfig(String key) {
        this.key = key;
    }

    private String key;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
