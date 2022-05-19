package com.example.byteplus_effects_plugin.algorithm.view;

import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;

import com.example.byteplus_effects_plugin.core.algorithm.base.AlgorithmTaskKey;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** {zh} 
 * 结果tip管理器
 */

/** {en}
 * Result tip manager
 */

public class TipManager {
    public interface ResultTipGenerator<T> {
        ResultTip<T> create(Context context);
    }

    public static class TipInfo {
        List<ResultTip> tips = new ArrayList<>();
        int added = 0;
    }

    private FrameLayout rootContainer;
    private Context mContext;

    private Map<AlgorithmTaskKey, ResultTipGenerator> mGenerators = new HashMap<>();
    private Map<AlgorithmTaskKey, TipInfo> mTips = new HashMap<>();

    public void init(Context context, FrameLayout frameLayout) {
        mContext = context;
        rootContainer = frameLayout;
    }

    public <T> void registerGenerator(AlgorithmTaskKey key, ResultTipGenerator<T> generator) {
        mGenerators.put(key, generator);
    }

    public void enableOrRemove(AlgorithmTaskKey key, boolean flag) {
        if (flag) {
            enableTip(key);
        } else {
            removeTip(key);
        }
    }

    public void enableTip(AlgorithmTaskKey key) {
        TipInfo info = mTips.get(key);
        if (info == null) return;
        info.added = 0;
    }

    public void removeTip(AlgorithmTaskKey key) {
        TipInfo info = mTips.get(key);
        if (info == null) return;
        for (int i = 0; i < info.added && i < info.tips.size(); i++) {
            rootContainer.removeView(info.tips.get(i));
        }
        info.added = -1;
    }

    public <T> void updateInfo(AlgorithmTaskKey key, T[] infos) {
        TipInfo tipInfo = mTips.get(key);
        if (tipInfo == null) {
            tipInfo = new TipInfo();
            mTips.put(key, tipInfo);
        }

        if (tipInfo.added < 0) return;

        if (infos == null) {
            for (int i = 0; i < tipInfo.added; i++) {
                tipInfo.tips.get(i).setVisibility(View.INVISIBLE);
            }
            return;
        }

        for (int i = 0; i < infos.length; i++) {
            ResultTipGenerator<T> generator = mGenerators.get(key);
            if (generator == null) {
                throw new IllegalStateException("generator must be registered before use");
            }
            while (tipInfo.tips.size() <= i) {
                tipInfo.tips.add(generator.create(mContext));
            }
            while (tipInfo.added <= i) {
                // fix  java.lang.IllegalStateException: The specified child already has a parent
                rootContainer.removeView(tipInfo.tips.get(tipInfo.added));
                rootContainer.addView(tipInfo.tips.get(tipInfo.added++));
            }

            ResultTip<T> tip = tipInfo.tips.get(i);
            tip.setVisibility(View.VISIBLE);
            tip.updateInfo(infos[i]);
        }

        for (int i = infos.length; i < tipInfo.added; i++) {
            tipInfo.tips.get(i).setVisibility(View.INVISIBLE);
        }
    }
}
