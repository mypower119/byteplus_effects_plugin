package com.example.byteplus_effects_plugin.effect.resource;

import com.google.gson.annotations.Expose;

import java.util.Map;

/**
 * Created on 2021/11/15 20:13
 */
public class StickerPage {
    @Expose private int version;
    @Expose private Map<String, String> titles;
    @Expose private StickerGroup[] tabs;

    public int getVersion() {
        return version;
    }

    public Map<String, String> getTitles() {
        return titles;
    }

    public StickerGroup[] getTabs() {
        return tabs;
    }

    public void setTitles(Map<String, String> titles) {
        this.titles = titles;
    }

    public void setTabs(StickerGroup[] tabs) {
        this.tabs = tabs;
    }
}
