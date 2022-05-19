package com.example.byteplus_effects_plugin.core.algorithm.factory;

import com.example.byteplus_effects_plugin.core.algorithm.base.AlgorithmTaskKey;

/**
 * Created on 5/13/21 3:01 PM
 */
public class AlgorithmTaskKeyFactory {
    public static AlgorithmTaskKey create(String key, boolean isTask) {
        return new AlgorithmTaskKey(key, isTask);
    }

    public static AlgorithmTaskKey create(String key) {
        return new AlgorithmTaskKey(key, false);
    }
}
