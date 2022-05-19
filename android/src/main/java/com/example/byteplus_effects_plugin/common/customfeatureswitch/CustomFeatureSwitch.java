package com.example.byteplus_effects_plugin.common.customfeatureswitch;

public class CustomFeatureSwitch {
    private static class SingletonClassInstance {
        private static final CustomFeatureSwitch instance = new CustomFeatureSwitch();
    }

    private CustomFeatureSwitch() {
    }

    public static CustomFeatureSwitch getInstance() {
        return SingletonClassInstance.instance;
    }

    public void resetAllSwitches()
    {
        feature_watch = false;
        feature_bracelet = false;
    }

    private boolean feature_watch;
    private boolean feature_bracelet;

    public void setFeature_watch(boolean feature_watch) {
        this.feature_watch = feature_watch;
    }

    public boolean getFeature_watch()
    {
        return this.feature_watch;
    }

    public void setFeature_bracelet(boolean feature_bracelet) {
        this.feature_bracelet = feature_bracelet;
    }

    public boolean getFeature_bracelet()
    {
        return this.feature_bracelet;
    }
}
