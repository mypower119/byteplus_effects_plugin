package com.example.byteplus_effects_plugin.effect.qrscan;

/**
 * Created on 2021/1/6 10:34
 */
public class EncryptResult {
    public BaseResponse base_response;
    public EncryptResultData data;

    public static class EncryptResultData {
        public String encryptUrl;
    }
}
