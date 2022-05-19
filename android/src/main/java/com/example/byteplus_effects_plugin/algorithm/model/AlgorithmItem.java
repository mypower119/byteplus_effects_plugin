package com.example.byteplus_effects_plugin.algorithm.model;


import com.example.byteplus_effects_plugin.common.model.ButtonItem;
import com.example.byteplus_effects_plugin.core.algorithm.base.AlgorithmTaskKey;

import java.util.List;

/**
 * Created on 5/11/21 6:58 PM
 */
public class AlgorithmItem extends ButtonItem {

    private List<AlgorithmTaskKey> dependency;
    private int dependencyToastId;

    private AlgorithmTaskKey key;

    public AlgorithmItem() {}

    public AlgorithmItem(AlgorithmTaskKey key) {
        this.key = key;
    }

    public AlgorithmItem(AlgorithmTaskKey key, List<AlgorithmTaskKey> dependency) {
        this.dependency = dependency;
        this.key = key;
    }

    public AlgorithmTaskKey getKey() {
        return key;
    }

    public void setKey(AlgorithmTaskKey key) {
        this.key = key;
    }

    public List<AlgorithmTaskKey> getDependency() {
        return dependency;
    }

    public void setDependency(List<AlgorithmTaskKey> dependency) {
        this.dependency = dependency;
    }

    public int getDependencyToastId() {
        return dependencyToastId;
    }

    public AlgorithmItem setDependencyToastId(int dependencyToastId) {
        this.dependencyToastId = dependencyToastId;
        return this;
    }
}
