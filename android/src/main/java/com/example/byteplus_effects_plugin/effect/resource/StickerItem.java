package com.example.byteplus_effects_plugin.effect.resource;

import android.content.Context;
import androidx.annotation.IdRes;

import com.example.byteplus_effects_plugin.common.model.ButtonItem;
import com.example.byteplus_effects_plugin.common.utils.LocaleUtils;
import com.example.byteplus_effects_plugin.resource.BaseResource;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Map;

/**
 * Created on 2021/11/15 20:13
 */
public class StickerItem extends ButtonItem {
    @Expose
    private Map<String, String> titles;
    @Expose
    private Map<String, String> tips;
    @Expose
    @SerializedName("icon")
    private String iconUrl;
    @IdRes
    private int iconId;
    @Expose
    private BaseResource resource;
    @Expose
    private String key;
    @Expose
    private String type;

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public void setIconId(int iconId) {
        this.iconId = iconId;
    }

    public void setResource(BaseResource resource) {
        this.resource = resource;
    }

    public void setTitles(Map<String, String> titles) {
        this.titles = titles;
    }

    public Map<String, String> getTitles() {
        return titles;
    }

    public Map<String, String> getTips() {
        return tips;
    }

    public int getIconId() {
        return iconId;
    }

    public void setTips(Map<String, String> tips) {
        this.tips = tips;
    }

    public String getTitle(Context context) {
        String title = titles.get(LocaleUtils.getLanguage(context));
        if (title == null && titles.size() > 0) {
            return titles.entrySet().iterator().next().getValue();
        }
        return title;
    }

    public String getTip(Context context) {
        if (tips == null) {
            return null;
        }
        String tip = tips.get(LocaleUtils.getLanguage(context));
        if (tip == null && tips.size() > 0) {
            return tips.entrySet().iterator().next().getValue();
        }
        return tip;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public BaseResource getResource() {
        return resource;
    }

    public String getKey() {
        return key;
    }

    public String getType() {
        return type;
    }
}
