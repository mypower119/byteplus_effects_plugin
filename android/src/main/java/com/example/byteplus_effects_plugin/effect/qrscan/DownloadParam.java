package com.example.byteplus_effects_plugin.effect.qrscan;

/**
 * Created on 2021/1/6 18:01
 */
public class DownloadParam {
    public String encryptUrl;

    public static class Builder {
        private String encryptUrl;

        public Builder setEncryptUrl(String encryptUrl) {
            this.encryptUrl = encryptUrl;
            return this;
        }

        public DownloadParam build() {
            DownloadParam param = new DownloadParam();
            param.encryptUrl = encryptUrl;
            return param;
        }
    }
}
