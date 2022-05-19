package com.example.byteplus_effects_plugin.lens.manager;

import com.example.byteplus_effects_plugin.common.model.ButtonItem;
import com.bytedance.labcv.effectsdk.BytedEffectConstants;
import com.example.byteplus_effects_plugin.R;
import com.example.byteplus_effects_plugin.lens.config.ImageQualityConfig;

import java.util.HashMap;
import java.util.Map;

/**
 * Created on 2021/5/19 10:35
 */
public class ImageQualityDataManager {
    private static Map<String, ImageQualityItem> sMap;

    public ImageQualityItem getItem(String key) {
        if (sMap != null) {
            return sMap.get(key);
        }

        sMap = new HashMap<>();
        sMap.put(ImageQualityConfig.KEY_VIDEO_SR, new ImageQualityItem(R.string.tab_video_sr,
                R.drawable.ic_video_sr, BytedEffectConstants.ImageQualityType.IMAGE_QUALITY_TYPE_VIDEO_SR));
        sMap.put(ImageQualityConfig.KEY_NIGHT_SCENE, new ImageQualityItem(R.string.tab_night_scene,
                R.drawable.ic_video_sr, BytedEffectConstants.ImageQualityType.IMAGE_QUALITY_TYPE_NIGHT_SCENE));
        sMap.put(ImageQualityConfig.KEY_ADAPTIVE_SHARPEN, new ImageQualityItem(R.string.tab_adaptive_sharpen,
                R.drawable.ic_adaptive_sharpen, BytedEffectConstants.ImageQualityType.IMAGE_QUALITY_TYPE_ADAPTIVE_SHARPEN));

        return sMap.get(key);
    }

    public static class ImageQualityItem  extends ButtonItem {
        private BytedEffectConstants.ImageQualityType type;

        public ImageQualityItem(int title, int icon, BytedEffectConstants.ImageQualityType type) {
            super(title, icon, 0);
            this.type = type;
        }

        public BytedEffectConstants.ImageQualityType getType() {
            return type;
        }

        public void setType(BytedEffectConstants.ImageQualityType type) {
            this.type = type;
        }
    }
}
