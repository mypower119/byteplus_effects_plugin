package com.example.byteplus_effects_plugin.effect.qrscan;


import static com.example.byteplus_effects_plugin.effect.task.DownloadResourceTask.SDK_VERSION;

import com.example.byteplus_effects_plugin.effect.task.DownloadResourceTask;

/**
 * Created on 2021/1/6 10:33
 */
public class EncryptParam {
    public String secId;
    public String sdkVersion;
    public String authFile;

    public static class Builder {
        private String secId;
        private String sdkVersion = SDK_VERSION;
        private String authFile = DownloadResourceTask.AUTH_FILE;

        public Builder setSecId(String secId) {
            this.secId = secId;
            return this;
        }

        public Builder setSdkVersion(String sdkVersion) {
            this.sdkVersion = sdkVersion;
            return this;
        }

        public Builder setAuthFile(String authFile) {
            this.authFile = authFile;
            return this;
        }

        public EncryptParam build() {
            EncryptParam param = new EncryptParam();
            param.sdkVersion = sdkVersion;
            param.secId = secId;
            param.authFile = authFile;
            return param;
        }
    }
}
