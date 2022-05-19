package com.example.byteplus_effects_plugin.effect.config;

import com.example.byteplus_effects_plugin.common.model.EffectType;
import com.example.byteplus_effects_plugin.effect.model.ComposerNode;
import com.example.byteplus_effects_plugin.effect.model.FilterItem;

/**
 * Created on 5/11/21 5:10 PM
 */
public class EffectConfig {
    public static final String EffectConfigKey = "effect_config_key";

    private String feature;
    private ComposerNode[] nodes;
    private FilterItem filter;
    private EffectType effectType = EffectType.LITE_ASIA;
    private boolean isAutoTest = false;

    public EffectType getEffectType() {
        return effectType;
    }

    public EffectConfig setEffectType(EffectType effectType) {
        this.effectType = effectType;
        return this;
    }

    public String[] getNodeArray(){
        if (null == nodes || nodes.length == 0){
            return null;
        }
        int length = nodes.length;
        String[] nodeArray = new String[length];
        for (int i  = 0; i < length;i++){
            nodeArray[i] = nodes[i].getPath();
        }
        return nodeArray;
    }

    public ComposerNode[] getNodes() {
        return nodes;
    }

    public void setNodes(ComposerNode[] nodes) {
        this.nodes = nodes;
    }

    public FilterItem getFilter() {
        return filter;
    }

    public void setFilter(FilterItem filter) {
        this.filter = filter;
    }

    public boolean isAutoTest() {
        return isAutoTest;
    }

    public void setAutoTest(boolean autoTest) {
        isAutoTest = autoTest;
    }

    public EffectConfig setFeature(String feature){
        this.feature = feature;
        return this;
    }

    public String getFeature(){
        return feature;
    }
}
