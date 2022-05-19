package com.example.byteplus_effects_plugin.core.algorithm.base;

/**
 * Created on 5/7/21 3:06 PM
 */
public class AlgorithmTaskKey {
    private String key;
    private boolean isTask;


    public static AlgorithmTaskKey createKey(String key) {
        return new AlgorithmTaskKey(key);
    }

    public static AlgorithmTaskKey createKey(String key, boolean isTask) {
        return new AlgorithmTaskKey(key, isTask);
    }

    public AlgorithmTaskKey(String key) {
        this.key = key;
    }

    public AlgorithmTaskKey(String key, boolean isTask) {
        this.key = key;
        this.isTask = isTask;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public boolean isTask() {
        return isTask;
    }

    public void setTask(boolean task) {
        isTask = task;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AlgorithmTaskKey that = (AlgorithmTaskKey) o;
        return key.equals(that.key);
    }

    @Override
    public int hashCode() {
        return key.hashCode();
    }
}
