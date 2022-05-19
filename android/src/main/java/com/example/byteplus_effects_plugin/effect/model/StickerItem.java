package com.example.byteplus_effects_plugin.effect.model;

/**
 * Created on 2019-07-21 12:25
 */
public class StickerItem extends FilterItem {
    private String tip;
    private RenderCache renderCache;

    public StickerItem(int title, int icon, String resource) {
        super(title, icon, resource);
    }

    public StickerItem(int title, int icon, String resource, String tip) {
        super(title, icon, resource);
        this.tip = tip;
    }

    public StickerItem(int title, int icon, String resource, String tip, RenderCache renderCache) {
        super(title, icon, resource);
        this.tip = tip;
        this.renderCache = renderCache;
    }

    public String getTip() {
        return tip;
    }

    public void setTip(String tip) {
        this.tip = tip;
    }

    public boolean hasTip() {
        return tip != null;
    }

    public RenderCache getRenderCache() {
        return renderCache;
    }

    public static class RenderCache {
        private final String cacheTextureName;
        private final String defaultTexture;

        public RenderCache(String cacheTextureName, String defaultTexture) {
            this.cacheTextureName = cacheTextureName;
            this.defaultTexture = defaultTexture;
        }

        public String getCacheTextureName() {
            return cacheTextureName;
        }

        public String getDefaultTexture() {
            return defaultTexture;
        }
    }
}
