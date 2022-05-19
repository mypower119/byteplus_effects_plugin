package com.example.byteplus_effects_plugin.effect.manager;

import android.content.Context;

import com.example.byteplus_effects_plugin.core.effect.EffectResourceHelper;
import com.example.byteplus_effects_plugin.core.effect.EffectResourceProvider;
import com.example.byteplus_effects_plugin.R;
import com.example.byteplus_effects_plugin.effect.model.FilterItem;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created on 2019-07-21 13:58
 */
public class FilterDataManager {
    private Context context = null;

    public FilterDataManager(Context context) {
        this.context = context;
        mResourceProvider = new EffectResourceHelper(context);
    }

    private static final int[] IMAGES = new int[] {
            R.drawable.clear,
            R.drawable.roubai,
            R.drawable.naiyou,
            R.drawable.yangqi,
            R.drawable.jiegeng,
            R.drawable.luolita,
            R.drawable.mitao,
            R.drawable.makalong,
            R.drawable.paomo,
            R.drawable.yinghua,
            R.drawable.qiannuan,
            R.drawable.wuyu,
            R.drawable.beihaidao,
            R.drawable.riza,
            R.drawable.xiyatu,
            R.drawable.jingmi,
            R.drawable.jiaopian,
            R.drawable.nuanyang,
            R.drawable.jiuri,
            R.drawable.hongchun,
            R.drawable.julandiao,
            R.drawable.tuise,
            R.drawable.heibai,
            R.drawable.wenrou,
            R.drawable.lianaichaotian,
            R.drawable.chujian,
            R.drawable.andiao,
            R.drawable.naicha,
            R.drawable.soft,
            R.drawable.xiyang,
            R.drawable.lengyang,
            R.drawable.haibianrenxiang,
            R.drawable.gaojihui,
            R.drawable.haidao,
            R.drawable.qianxia,
            R.drawable.yese,
            R.drawable.hongzong,
            R.drawable.qingtou,
            R.drawable.ziran,
            R.drawable.suda,
            R.drawable.jiazhou,
            R.drawable.shise,
            R.drawable.chuanwei,
            R.drawable.meishijiaopian,
            R.drawable.hongsefugu,
            R.drawable.lvtu,
            R.drawable.nuanhuang,
            R.drawable.landiaojiaopian

    };
    private EffectResourceProvider mResourceProvider;

    private List<FilterItem> mItems;

    public List<FilterItem> getItems() {
        if (mItems != null) {
            return mItems;
        }
        mItems = new ArrayList<>();
//        String[] FILTER_TITLE = new String[]{
//                context.getString(R.string.filter_normal),
//                context.getString(R.string.filter_chalk),
//                context.getString(R.string.filter_cream),
//                context.getString(R.string.filter_oxgen),
//                context.getString(R.string.filter_campan),
//                context.getString(R.string.filter_lolita),
//                context.getString(R.string.filter_mitao),
//                context.getString(R.string.filter_makalong),
//                context.getString(R.string.filter_paomo),
//                context.getString(R.string.filter_yinhua),
//                context.getString(R.string.filter_musi),
//                context.getString(R.string.filter_wuyu),
//                context.getString(R.string.filter_beihaidao),
//                context.getString(R.string.filter_riza),
//                context.getString(R.string.filter_xiyatu),
//                context.getString(R.string.filter_jingmi),
//                context.getString(R.string.filter_jiaopian),
//                context.getString(R.string.filter_nuanyang),
//                context.getString(R.string.filter_jiuri),
//                context.getString(R.string.filter_hongchun),
//                context.getString(R.string.filter_julandiao),
//                context.getString(R.string.filter_tuise),
//                context.getString(R.string.filter_heibai),
//                context.getString(R.string.filter_wenrou),
//                context.getString(R.string.filter_lianaichaotian),
//                context.getString(R.string.filter_chujian),
//                context.getString(R.string.filter_andiao),
//                context.getString(R.string.filter_naicha),
//                context.getString(R.string.filter_soft),
//                context.getString(R.string.filter_xiyang),
//                context.getString(R.string.filter_lengyang),
//                context.getString(R.string.filter_haibianrenxiang),
//                context.getString(R.string.filter_gaojihui),
//                context.getString(R.string.filter_haidao),
//                context.getString(R.string.filter_qianxia),
//                context.getString(R.string.filter_yese),
//                context.getString(R.string.filter_hongzong),
//                context.getString(R.string.filter_qingtou),
//                context.getString(R.string.filter_ziran),
//                context.getString(R.string.filter_suda),
//                context.getString(R.string.filter_jiazhou),
//                context.getString(R.string.filter_shise),
//                context.getString(R.string.filter_chuanwei),
//                context.getString(R.string.filter_meishijiaopian),
//                context.getString(R.string.filter_hongsefugu),
//                context.getString(R.string.filter_lvtu),
//                context.getString(R.string.filter_nuanhuang),
//                context.getString(R.string.filter_landiaojiaopian),
//
//        };
        int[] FILTER_TITLE = new int[]{
                R.string.filter_normal,
                R.string.filter_chalk,
                R.string.filter_cream,
                R.string.filter_oxgen,
                R.string.filter_campan,
                R.string.filter_lolita,
                R.string.filter_mitao,
                R.string.filter_makalong,
                R.string.filter_paomo,
                R.string.filter_yinhua,
                R.string.filter_musi,
                R.string.filter_wuyu,
                R.string.filter_beihaidao,
                R.string.filter_riza,
                R.string.filter_xiyatu,
                R.string.filter_jingmi,
                R.string.filter_jiaopian,
                R.string.filter_nuanyang,
                R.string.filter_jiuri,
                R.string.filter_hongchun,
                R.string.filter_julandiao,
                R.string.filter_tuise,
                R.string.filter_heibai,
                R.string.filter_wenrou,
                R.string.filter_lianaichaotian,
                R.string.filter_chujian,
                R.string.filter_andiao,
                R.string.filter_naicha,
                R.string.filter_soft,
                R.string.filter_xiyang,
                R.string.filter_lengyang,
                R.string.filter_haibianrenxiang,
                R.string.filter_gaojihui,
                R.string.filter_haidao,
                R.string.filter_qianxia,
                R.string.filter_yese,
                R.string.filter_hongzong,
                R.string.filter_qingtou,
                R.string.filter_ziran,
                R.string.filter_suda,
                R.string.filter_jiazhou,
                R.string.filter_shise,
                R.string.filter_chuanwei,
                R.string.filter_meishijiaopian,
                R.string.filter_hongsefugu,
                R.string.filter_lvtu,
                R.string.filter_nuanhuang,
                R.string.filter_landiaojiaopian,

        };
        File dir = new File(mResourceProvider.getFilterPath());
        List<File> mFileList = new ArrayList<>();
        mFileList.add(null);  //   {zh} normal 正常       {en} Normal normal  
        if (dir.exists() && dir.isDirectory()) {
            mFileList.addAll(Arrays.asList(dir.listFiles()));
        }
        Collections.sort(mFileList, new Comparator<File>() {
            @Override
            public int compare(File file, File t1) {
                if (file == null)
                    return -1;
                if (t1 == null)
                    return 1;

                String s = "";
                String s1 = "";

                String[] arrays =  file.getName().split("/");
                String[] arrays1 =  t1.getName().split("/");

                arrays = arrays[arrays.length - 1 ].split("_");
                arrays1 = arrays1[arrays1.length - 1 ].split("_");

                s =  arrays[1];
                s1 =  arrays1[1];


                return Integer.valueOf(s) - Integer.valueOf(s1);
            }
        });

        for (int i = 0; i < mFileList.size(); i++) {
            int fileNum = mFileList.get(i) == null ? 0 : Integer.valueOf(mFileList.get(i).getName().split("_")[1]);
            mItems.add(new FilterItem(FILTER_TITLE[fileNum], IMAGES[fileNum], mFileList.get(i) == null ? null : mFileList.get(i).getName(),fileNum == 0?0f:EffectDataManager.getDefaultIntensity(EffectDataManager.TYPE_FILTER,null)[0]));
        }
        return mItems;
    }
}
