package com.example.byteplus_effects_plugin.effect.qrscan;

/**
 * Created on 2021/1/6 11:41
 */
public class QRResourceInfo {
    public String secId;
    public String sdkVersion;
    public QRResourceType goodsType;
    public QRResourceType goodsSubType;

    public static class QRResourceType {
        public String key;
        public String name;
        boolean enableQrcode;
    }
}
