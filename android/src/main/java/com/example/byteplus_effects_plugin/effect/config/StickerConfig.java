package com.example.byteplus_effects_plugin.effect.config;

import com.example.byteplus_effects_plugin.effect.manager.StickerDataManager;

import java.util.Map;

/** {zh} 
 * Created  on 2021/5/23 8:01 下午
 */

/** {en}
 * Created on 2021/5/23 8:01 pm
 */

public class StickerConfig {
    public static final String StickerConfigKey = "sticker_config_key";
    private StickerDataManager.StickerType type;
    private String stickerPath;
    private String renderCacheKey;
    private String renderCachePath;
    private Map<String, Object> params;

    public StickerConfig setType(StickerDataManager.StickerType type) {
        this.type = type;
        return this;
    }

    public StickerConfig setStickerPath(String stickerPath) {
        this.stickerPath = stickerPath;
        return this;

    }

    public StickerConfig setRenderCacheKey(String renderCacheKey) {
        this.renderCacheKey = renderCacheKey;
        return this;


    }

    public StickerConfig setRenderCachePath(String renderCachePath) {
        this.renderCachePath = renderCachePath;
        return this;

    }

    public StickerDataManager.StickerType getType() {
        return type;
    }

    public String getStickerPath() {
        return stickerPath;
    }

    public String getRenderCacheKey() {
        return renderCacheKey;
    }

    public String getRenderCachePath() {
        return renderCachePath;
    }

    public Map<String, Object> getParams() {
        return params;
    }

    public StickerConfig setParams(Map<String, Object> params) {
        this.params = params;
        return this;
    }
}
