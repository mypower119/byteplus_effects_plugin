package com.example.byteplus_effects_plugin.algorithm.ui;

import android.view.View;

import com.example.byteplus_effects_plugin.algorithm.model.AlgorithmItem;
import com.example.byteplus_effects_plugin.common.view.PropertyTextView;
import com.example.byteplus_effects_plugin.core.algorithm.VideoClsAlgorithmTask;
import com.example.byteplus_effects_plugin.core.algorithm.base.AlgorithmTaskKey;
import com.example.byteplus_effects_plugin.R;
import com.bytedance.labcv.effectsdk.BefVideoClsInfo;

/**
 * Created on 2020/8/19 17:51
 */
public class VideoClsUI extends BaseAlgorithmUI<BefVideoClsInfo> {
    public static final int SHOW_NUM = 1;

    private static String[] VIDEO_CLS_TYPES = {
            "human", "baby", "kid", "half-selfie", "full-selfie", "multi-person", "kid-parent", "kid-show",
            "crowd", "dinner", "wedding", "avatar", "homelife", "yard,balcony", "furniture,appliance",
            "urbanlife", "supermarket", "streets", "parks", "campus", "building", "bridges", "station",
            "transportation", "car", "train", "plane", "entertainment", "ktv,bar", "mahjomg", "amusement-park",
            "drama,cross-talk,cinema", "concert,music-festival", "museum", "food", "food-show", "snacks",
            "home-foods", "fruit", "drinks", "japanese-food", "hot-pot", "barbecue", "noodles", "cake",
            "cooking", "natural-scene", "mountain", "rivers,lakes,sea", "road", "grass", "waterfull",
            "desert", "forest", "snow", "fileworks", "sky", "cultural-scene", "animals", "pets", "livestock",
            "plants", "flowers", "pets-plants", "ball", "soccer", "basketball", "badminton", "tennis",
            "pingpong", "billiard", "keep-fit", "yoga", "track-and-field", "extreme-sports", "water-sports",
            "swim", "dance", "singing", "game", "cartoon", "cosplay", "texts", "papers", "cards", "sensitive",
            "sex", "synthesis"
    };

    private PropertyTextView ptv;

    @Override
    void initView() {
        super.initView();

        addLayout(R.layout.layout_c1_info, R.id.fl_algorithm_info);

        if (!checkAvailable(provider())) return;
        ptv = provider().findViewById(R.id.ptv_c1);
    }

    @Override
    public void onEvent(AlgorithmTaskKey key, boolean flag) {
        super.onEvent(key, flag);

        ptv.setVisibility(flag ? View.VISIBLE : View.INVISIBLE);
        if (!flag) {
            ptv.setTitle("");
            ptv.setValue("");
        }
    }

    @Override
    public void onReceiveResult(BefVideoClsInfo befVideoClsInfo) {
        runOnUIThread(new Runnable() {
            @Override
            public void run() {
                if (!checkAvailable(provider(),
                        provider().getContext(), tipManager()) || befVideoClsInfo == null) return;
                BefVideoClsInfo.BefVideoClsType[] topN = befVideoClsInfo.topN(SHOW_NUM);
                if (topN.length > 0) {
                    ptv.setTitle(VIDEO_CLS_TYPES[topN[0].getId()]);
                    ptv.setValue(String.format("%.2f", topN[0].getConfidence()));
                } else {
                    ptv.setTitle(provider().getString(R.string.tab_video_cls));
                    ptv.setValue(provider().getString(R.string.video_cls_no_results));
                }
            }
        });
    }

    @Override
    public AlgorithmItem getAlgorithmItem() {
        return (AlgorithmItem) new AlgorithmItem(VideoClsAlgorithmTask.VIDEO_CLS)
                .setIcon(R.drawable.ic_video_cls)
                .setTitle(R.string.tab_video_cls);
    }
}
