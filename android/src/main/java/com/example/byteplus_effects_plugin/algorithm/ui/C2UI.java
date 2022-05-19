package com.example.byteplus_effects_plugin.algorithm.ui;

import android.view.View;

import com.example.byteplus_effects_plugin.algorithm.model.AlgorithmItem;
import com.example.byteplus_effects_plugin.common.view.PropertyTextView;
import com.example.byteplus_effects_plugin.core.algorithm.C2AlgorithmTask;
import com.example.byteplus_effects_plugin.core.algorithm.base.AlgorithmTaskKey;
import com.example.byteplus_effects_plugin.core.util.LogUtils;
import com.example.byteplus_effects_plugin.R;
import com.bytedance.labcv.effectsdk.BefC2Info;

import java.util.Arrays;

/**
 * Created on 2020/8/19 17:50
 */
public class C2UI extends BaseAlgorithmUI<BefC2Info> {
    public static final int SHOW_NUM = 3;

    public static final String[] C2_TYPES = {
            "car_c1", "cat_c1", "bus_station", "Giraffe", "vegetables", "Cave", "jiehunzhao",
            "sr_camera", "yinger", "zhongcan", "lawn", "soccer_field", "selfie_c1", "GaoTie",
            "broadleaf_forest", "Americal_Fast_Food", "islet", "dining_hall", "formal_garden",
            "carrousel", "food_court", "Pc_game", "card_data", "Pig", "golf_course", "mianshi",
            "Peacock", "ertong", "supermarket", "sushi", "badminton_indoor", "Panda", "ruin", "TingYuan",
            "Graduation", "ZhanDao", "tennis_outdoor", "snacks", "DuoRouZhiWu", "ocean_liner", "Snooker",
            "hill_c1", "GongDian", "beach_c1", "QuanJiaFu", "dog_c1", "bankcard", "Swimming", "godfish",
            "grill", "LuYing", "fountain", "flower_c1", "rabbit", "Hamster", "desert", "banquet_hall",
            "farm", "Tortoise", "Ski", "downtown", "moutain_path", "street_c1", "HaiXian", "Bonfire",
            "movie_theater", "PaoBu", "park", "mobilephone", "Monument", "Elepant", "hotpot", "KingPenguin",
            "natural_river", "text_c1", "tv", "cartoon_c1", "swimming_pool_indoor", "lake_c1", "mountain_snowy",
            "fruit", "gymnasimum", "skyscraper", "amusement_arcade", "Soccerball", "big_neg", "statue_c1",
            "xiangcunjianzhu", "shineijiudianjucan", "group_c1", "cake", "creek", "ShaLa", "nightscape_c1",
            "bridge", "ocean", "DiTie", "shiwaiyoulechagn", "train_station", "tree_c1", "baseball", "Tiger",
            "temple", "gujianzhu", "Card_game", "throne_room", "xuejing", "tower", "bazaar_outdoor",
            "basketball_court_indoor", "QinZiSheYing", "drink", "ktv", "athletic_field", "screen",
            "note_labels", "pure_text", "puzzle", "qrcode", "scrawl_mark", "solidcolor_bg", "tv_screen"
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
    public void onReceiveResult(BefC2Info befC2Info) {
        runOnUIThread(new Runnable() {
            @Override
            public void run() {
                if (!checkAvailable(provider())) return;
                if (befC2Info == null) return;

                LogUtils.e(Arrays.toString(befC2Info.topN(SHOW_NUM)));
                BefC2Info.BefC2CategoryItem[] items = befC2Info.topN(SHOW_NUM);
                if (items.length > 0) {
                    BefC2Info.BefC2CategoryItem item = items[0];
                    ptv.setTitle(C2_TYPES[item.getId()]);
                    ptv.setValue(String.format("%.2f", item.getConfidence()));
                } else {
                    ptv.setTitle(provider().getString(R.string.tab_c2));
                    ptv.setValue(provider().getString(R.string.video_cls_no_results));
                }
            }
        });
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
    public AlgorithmItem getAlgorithmItem() {
        return (AlgorithmItem) new AlgorithmItem(C2AlgorithmTask.C2)
                .setTitle(R.string.tab_c2)
                .setDesc(R.string.c2_desc)
                .setIcon(R.drawable.ic_c2);
    }
}
