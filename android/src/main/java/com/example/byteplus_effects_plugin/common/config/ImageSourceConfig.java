package com.example.byteplus_effects_plugin.common.config;

/**
 * Created on 5/12/21 11:16 AM
 */
public class ImageSourceConfig {
    public static final String IMAGE_SOURCE_CONFIG_KEY = "image_source_config_key";
    public enum  ImageSourceType{
        TYPE_CAMERA, TYPE_VIDEO,TYPE_IMAGE;

    }


    private ImageSourceType type;
    private String media;
    private boolean recordable= false;
    private int requestWidth;
    private int requestHeight;


    public ImageSourceConfig() {
    }

    public ImageSourceConfig(ImageSourceType type, String media) {
        this.type = type;
        this.media = media;
    }

    public ImageSourceType getType() {
        return type;
    }

    public void setType(ImageSourceType type) {
        this.type = type;
    }

    public String getMedia() {
        return media;
    }

    /** {zh} 
     * 可以是视频或者图片路径 也可以是前后置摄像头，如果是前后置摄像头 传入标识前后的配置参数
     * android.hardware.Camera.CameraInfo.CAMERA_FACING_FRONT
     * android.hardware.Camera.CameraInfo.CAMERA_FACING_BACK
     * @param media
     */
    /** {en} 
     * It can be a video or picture path, or it can be a front and rear camera. If it is a front and rear camera, pass in the configuration parameters before and after the logo
     * android.hardware.Camera.CameraInfo.CAMERA_FACING_FRONT
     * android.hardware.Camera.CameraInfo.CAMERA_FACING_BACK
     * @param media
     */

    public void setMedia(String media) {
        this.media = media;

    }

    /** {zh} 
     * 是否录制视频
     * @return
     */
    /** {en} 
     * Whether to record video
     * @return
     */

    public boolean isRecordable() {
        return recordable;
    }

    public void setRecordable(boolean recordable) {
        this.recordable = recordable;
    }

    public int getRequestWidth() {
        return requestWidth;
    }

    public void setRequestWidth(int requestWidth) {
        this.requestWidth = requestWidth;
    }

    public int getRequestHeight() {
        return requestHeight;
    }

    public void setRequestHeight(int requestHeight) {
        this.requestHeight = requestHeight;
    }

    public ImageSourceConfig clone(){
        ImageSourceConfig clone = new ImageSourceConfig(this.getType(),this.getMedia());
        clone.setRecordable(this.isRecordable());
        clone.setRequestWidth(this.requestWidth);
        clone.setRequestHeight(this.requestHeight);
        return clone;
    }
}
