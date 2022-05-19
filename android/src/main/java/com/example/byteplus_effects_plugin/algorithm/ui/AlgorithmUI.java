package com.example.byteplus_effects_plugin.algorithm.ui;

import android.content.Context;
import android.view.View;

import androidx.annotation.IdRes;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.byteplus_effects_plugin.algorithm.model.AlgorithmItem;
import com.example.byteplus_effects_plugin.algorithm.view.TipManager;
import com.example.byteplus_effects_plugin.core.algorithm.base.AlgorithmTaskKey;

;

/**
 * Created on 2020/8/18 17:15
 */
public interface AlgorithmUI<T> {
    void init(AlgorithmInfoProvider provider);

    void onEvent(AlgorithmTaskKey key, boolean flag);

    void onReceiveResult(T algorithmResult);

    AlgorithmItem getAlgorithmItem();

    IFragmentGenerator getFragmentGenerator();

    interface AlgorithmInfoProvider {
        TipManager getTipManager();
        Context getContext();
        String getString(@StringRes int id);
        int getPreviewWidth();
        int getPreviewHeight();
        boolean fitCenter();
        FragmentManager getFMManager();
        void runOnUiThread(Runnable runnable);
        <T extends View> T findViewById(@IdRes int id);
        void setBoardTarget(@IdRes int targetId);
    }

    public interface IFragmentGenerator {
        Fragment create();
        int title();
        AlgorithmTaskKey key();
    }
}
