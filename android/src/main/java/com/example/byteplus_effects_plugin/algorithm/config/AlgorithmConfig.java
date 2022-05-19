package com.example.byteplus_effects_plugin.algorithm.config;

import java.util.Map;

/**
 * Created on 5/11/21 5:06 PM
 */
public class AlgorithmConfig {
    public static final String ALGORITHM_CONFIG_KEY = "algorithm_config_key";


    private String algorithmType;
    private Map<String, Object> params;
    private boolean showBoard;
    private boolean isAutoTest;

    public AlgorithmConfig(String algorithmType, Map<String, Object> params) {
        this.algorithmType = algorithmType;
        this.params = params;
        this.showBoard = true;
    }

    public AlgorithmConfig(String algorithmType, Map<String, Object> params, boolean showBoard) {
        this.algorithmType = algorithmType;
        this.params = params;
        this.showBoard = showBoard;
    }

    public String getType() {
        return algorithmType;
    }

    public void setType(String algorithmType) {
        this.algorithmType = algorithmType;
    }

    public Map<String, Object> getParams() {
        return params;
    }

    public void setParams(Map<String, Object> params) {
        this.params = params;
    }

    public boolean isShowBoard() {
        return showBoard;
    }

    public void setShowBoard(boolean showBoard) {
        this.showBoard = showBoard;
    }

    public boolean isAutoTest() {
        return isAutoTest;
    }

    public void setAutoTest(boolean autoTest) {
        isAutoTest = autoTest;
    }
}
