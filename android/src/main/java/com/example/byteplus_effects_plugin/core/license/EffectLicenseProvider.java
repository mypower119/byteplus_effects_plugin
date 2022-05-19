package com.example.byteplus_effects_plugin.core.license;
/**
 * Created on 5/8/21 10:30 AM
 */

public interface EffectLicenseProvider {
    enum LICENSE_MODE_ENUM{
        OFFLINE_LICENSE,
        ONLINE_LICENSE
    };

    String getLicensePath();
    String updateLicensePath();
    LICENSE_MODE_ENUM getLicenseMode();
    int getLastErrorCode();
    boolean checkLicenseResult(String msg);
}
