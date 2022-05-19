package com.example.byteplus_effects_plugin.core.lens;

import com.bytedance.labcv.effectsdk.BytedEffectConstants;

/**
 * Created on 5/8/21 11:58 AM
 */
public interface ImageQualityInterface {
    /** {zh} 
     * 初始SDK，确保在gl线程中执行
     * dir 确定要求可读可写权限
     */
    /** {en} 
     * Initial SDK, ensure that
     * dir is executed in the gl thread to determine the read and write permissions required
     */

    int init(String dir);

    int destroy();

    void selectImageQuality(BytedEffectConstants.ImageQualityType type, boolean on);

    int processTexture(int srcTextureId,
                       int srcTextureWidth, int srcTextureHeight, ImageQualityResult result);

    void setPause(boolean pause);

    void recoverStatus();

    class ImageQualityResult {
        int texture;
        int height;
        int width;

        public ImageQualityResult(){
            texture = -1;
            height = 0;
            width = 0;
        }

        public void setWidth(int width) {
            this.width = width;
        }

        public void setHeight(int height) {
            this.height = height;
        }

        public void setTexture(int texture) {
            this.texture = texture;
        }

        public int getTexture() {
            return texture;
        }

        public int getHeight() {
            return height;
        }

        public int getWidth() {
            return width;
        }
    };
}
