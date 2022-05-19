package com.example.byteplus_effects_plugin.effect.model;

import com.example.byteplus_effects_plugin.common.model.ButtonItem;

/**
 * Created on 2019-07-21 12:23
 */
public class FilterItem extends ButtonItem {
    private String resource;
    private float intensity;

    public FilterItem(int title, int icon, String resource) {
        setTitle(title);
        setIcon(icon);
        this.resource = resource;
    }

    public FilterItem(int title, int icon, String resource, float intensity) {
        setTitle(title);
        setIcon(icon);
        this.resource = resource;
        this.intensity = intensity;
    }

    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    public float getIntensity() {
        return intensity;
    }

    public void setIntensity(float intensity) {
        this.intensity = intensity;
    }
}
