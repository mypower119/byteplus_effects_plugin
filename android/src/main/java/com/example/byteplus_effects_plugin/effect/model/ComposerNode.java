package com.example.byteplus_effects_plugin.effect.model;

/**
 * Created on 2019-07-26 10:53
 */
public class ComposerNode {
    private String path;
    private String[] keyArray;
    private String tag;
    private float[] intensityArray = new float[0];

    public ComposerNode(String path, String[] keyArray, float[] intensityArray) {
        this.path = path;
        this.keyArray = keyArray;
        this.intensityArray =  intensityArray;
    }

    public ComposerNode(String path, String key, float intensity) {
        this.path = path;
        this.keyArray = new String[]{key};
        this.intensityArray =  new float[]{intensity};
    }



    public ComposerNode(String path, String[] keyArray, String tag) {
        this.path = path;
        this.keyArray = keyArray;
        this.tag = tag;
    }

    public ComposerNode(String path) {
        this.path = path;
        this.keyArray = new String[]{"null"};
        this.intensityArray = new float[]{0.5f};
    }

    public ComposerNode(String path, String[] keyArray, float intensity) {
        this.path = path;
        this.keyArray = keyArray;
        this.intensityArray =  new float[]{intensity};
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String[] getKeyArray() {
        return keyArray;
    }

    public void setKeyArray(String[] keyArray) {
        this.keyArray = keyArray;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public float[] getIntensityArray() {
        return intensityArray;
    }

    public void setIntensityArray(float[] intensityArray) {
        this.intensityArray = intensityArray;
    }
}
