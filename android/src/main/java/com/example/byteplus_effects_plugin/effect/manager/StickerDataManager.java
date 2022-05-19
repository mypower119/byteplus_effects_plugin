package com.example.byteplus_effects_plugin.effect.manager;

import android.content.Context;

import com.example.byteplus_effects_plugin.R;
import com.example.byteplus_effects_plugin.effect.model.StickerItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created on 2019-07-21 14:09
 */
public class StickerDataManager {

    public enum StickerType {
        GENERAL_STCIKER("sticker"),
        STYLE_MAKEUP("style"),
        ANIMOJI("animoji"),
        AR_SCAN("ar_scan"),
        AMAZING_STICKER("amazing-sticker"),
        // AR 4 categories
        SLAM_STICKRE("ar-slam"),
        OBJECT_AR("ar-object"),
        LANDMARK_AR("ar-landmark"),
        SKY_LAND_AR("ar-sky-land"),
        // AR_TRY_ON 10 categories
        AR_TRY_PURSE("ar-try-purse"),
        AR_TRY_NAIL("ar-try-nail"),
        AR_TRY_SHOE("ar-try-shoe"),
        AR_TRY_HAT("ar-try-hat"),
        AR_TRY_NECKLACE("ar-try-necklace"),
        AR_TRY_GLASSES("ar-try-glasses"),
        AR_TRY_BRACELET("ar-try-bracelet"),
        AR_TRY_RING("ar-try-ring"),
        AR_TRY_EARRINGS("ar-try-earrings"),
        AR_TRY_WATCH("ar-try-watch");

        private String name;

        StickerType(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

//    public static final int TYPE_STICKER = 1001 ;
//    public static final int TYPE_ANIMOJI = 1002;
//    public static final int TYPE_STICKER_2D = 1003;
//    public static final int TYPE_STICKER_COMPLEX = 1004;
//    public static final int TYPE_STICKER_AR_SHOES = 1005;
//    public static final int TYPE_STICKER_ADVANCED = 1006;
//    public static final int TYPE_STICKER_INTERACTIVE = 1007;
//    public static final int TYPE_AR_SCAN = 1008;
//    public static final int TYPE_STICKER_STYLE_MAKEUP = 1009;
//    public static final int TYPE_STICKER_AMAZING = 1010;
//    public static final int TYPE_STICKER_AR_HAT = 1011;
//
//    public static final int TYPE_STICKER_PLACEMENT = 1010;
//    public static final int TYPE_STICKER_AR_PEN = 1011;
//    public static final int TYPE_STICKER_AR_DOOR = 1012;
//
//    public static final int TYPE_STICKER_AR_2D = 1013;
//    public static final int TYPE_STICKER_AR_ZHUTI = 1014;
//
//    public static final int TYPE_STICKER_AR_LANDMARK = 1015;
//
//    public static final int TYPE_STICKER_AR_SKY = 1016;
//    public static final int TYPE_STICKER_AR_LAND = 1017;
//    public static final int TYPE_STICKER_AR_RING = 1018;
//    public static final int TYPE_STICKER_AR_GLASS = 1019;
//    public static final int TYPE_STICKER_AR_NAIL = 1020;
//
//    public static final int TYPE_STICKER_3D_WATCH = 1021;
//    public static final int TYPE_STICKER_3D_BRACELET = 1022;



//    public static class StickerTabItem {
//        public int id;
//        public int title;
//        public List<StickerItem> stickerItems;
//
//        public StickerTabItem(int id, int title, List<StickerItem> items) {
//            this.id = id;
//            this.title = title;
//            this.stickerItems = items;
//        }
//    }
//    public static List<StickerTabItem> getTabItems(Context mContext, StickerType stickerType) {
//        switch (stickerType){
//            case AR_TRY_NAIL:
//                return Arrays.asList(
//                        new StickerTabItem(TYPE_STICKER_AR_NAIL, R.string.sticker_nail, getARNail(mContext))
//                );
//            case AR_TRY_GLASS:
//                return Arrays.asList(
//                        new StickerTabItem(TYPE_STICKER_AR_GLASS, R.string.sticker_glass, getARGlasss(mContext))
//                );
//            case AR_TRY_RING:
//                return Arrays.asList(
//                        new StickerTabItem(TYPE_STICKER_AR_RING, R.string.sticker_ring, getARRing(mContext))
//                );
//            case AR_TRY_SHOES:
//               return Arrays.asList(
//                        new StickerTabItem(TYPE_STICKER_AR_SHOES, R.string.sticker_shoes, getARShoes(mContext))
//                );
//             case AR_TRY_HAT:
//               return Arrays.asList(
//                        new StickerTabItem(TYPE_STICKER_AR_HAT, R.string.sticker_hat, getARHat(mContext))
//                );
//            case AR_TRY_WATCH:
//                return Arrays.asList(
//                        new StickerTabItem(TYPE_STICKER_3D_WATCH, R.string.sticker_watch, get3DWatch(mContext))
//                );
//            case AR_TRY_BRACELET:
//                return Arrays.asList(
//                        new StickerTabItem(TYPE_STICKER_3D_BRACELET, R.string.sticker_bracelet, get3DBracelet(mContext))
//                );
//            case STYLE_MAKEUP:
//               return Arrays.asList(
//                        new StickerTabItem(TYPE_STICKER_STYLE_MAKEUP, R.string.sticker_makeup, getStyleMakeupItems(mContext))
//                );
//            case ANIMOJI:
//               return Arrays.asList(
//                        new StickerTabItem(TYPE_ANIMOJI, R.string.sticker_animoji, getAnimojiItems(mContext))
//                );
//            case AMAZING_STICKER:
//               return Arrays.asList(
//                        new StickerTabItem(TYPE_STICKER_AMAZING, R.string.sticker_amazing,  getStickerAmazingItems(mContext))
//                );
//
//            case AR_SCAN:
//
//                return
//                        Arrays.asList(
//                                new StickerTabItem(TYPE_AR_SCAN, R.string.sticker_ar_scan, getARScanItems(mContext))
//                        );
//            case GENERAL_STCIKER:
//                return
//                        Arrays.asList(
//                                new StickerTabItem(TYPE_STICKER_2D, R.string.sticker_2d, getSticker2DItems(mContext)),
//                                new StickerTabItem(TYPE_STICKER_COMPLEX, R.string.sticker_complex, getStickerComplexItems(mContext)),
//                                new StickerTabItem(TYPE_STICKER_AR_SHOES, R.string.sticker_3d, getSticker3DItems(mContext)),
//                                new StickerTabItem(TYPE_STICKER_STYLE_MAKEUP, R.string.sticker_makeup, getStyleMakeupItems(mContext))
//                        );
//            case SLAM_STICKRE:
//                return
//                        Arrays.asList(
//                                new StickerTabItem(TYPE_STICKER_PLACEMENT, R.string.sticker_placement, getStickerPlacementItems(mContext)),
//                                new StickerTabItem(TYPE_STICKER_AR_PEN, R.string.sticker_ar_pen, getStickerArPenItems(mContext)),
//                                new StickerTabItem(TYPE_STICKER_AR_DOOR, R.string.sticker_ar_door, getStickerArDoorItems(mContext))
//                        );
//
//            case OBJECT_AR:
//                return
//                        Arrays.asList(
//                                new StickerTabItem(TYPE_STICKER_AR_2D, R.string.sticker_ar_2D, getSticker2DARItems(mContext)),
//                                new StickerTabItem(TYPE_STICKER_AR_ZHUTI, R.string.sticker_ar_zhuti, getStickerZhuTiARItems(mContext))
//                        );
//
//            case LANDMARK_AR:
//                return
//                        Arrays.asList(
//                                new StickerTabItem(TYPE_STICKER_AR_LANDMARK, R.string.sticker_ar_landmark, getStickerLandmarkARItems(mContext))
//                        );
//
//            case SKY_LAND_AR:
//                return
//                        Arrays.asList(
//                                new StickerTabItem(TYPE_STICKER_AR_SKY, R.string.sticker_ar_sky, getStickerSkyARItems(mContext)),
//                                new StickerTabItem(TYPE_STICKER_AR_LAND, R.string.sticker_ar_land, getStickerLandARItems(mContext))
//                        );
//        }
//        return null;
//
//    }

    private static List<StickerItem> getStickerArDoorItems(Context mContext) {
        List<StickerItem> mSticker2DItems = new ArrayList<>();

        mSticker2DItems.add(new StickerItem(R.string.filter_normal, R.drawable.clear, null));
        mSticker2DItems.add(new StickerItem(R.string.sticker_ar_bowuguan, R.drawable.bowuguan, "ar/bowuguan"));
        return mSticker2DItems;
    }

    private static List<StickerItem> getStickerArPenItems(Context mContext) {
        List<StickerItem> mSticker2DItems = new ArrayList<>();

        mSticker2DItems.add(new StickerItem(R.string.filter_normal, R.drawable.clear, null));
        mSticker2DItems.add(new StickerItem(R.string.sticker_ar_wulong, R.drawable.wulong, "ar/slam-brush"));
        return mSticker2DItems;
    }

    private static List<StickerItem> getStickerPlacementItems(Context mContext) {
        List<StickerItem> mSticker2DItems = new ArrayList<>();

        mSticker2DItems.add(new StickerItem(R.string.filter_normal, R.drawable.clear, null));
        mSticker2DItems.add(new StickerItem(R.string.sticker_ar_jumao, R.drawable.jumao, "ar/jumao"));
        mSticker2DItems.add(new StickerItem(R.string.sticker_ar_lianhua, R.drawable.lianhua, "ar/lianhua"));
        return mSticker2DItems;
    }

    private static List<StickerItem> getSticker2DARItems(Context mContext) {
        List<StickerItem> mSticker2DItems = new ArrayList<>();

        mSticker2DItems.add(new StickerItem(R.string.filter_normal, R.drawable.clear, null));
        mSticker2DItems.add(new StickerItem(R.string.sticker_ar_hongbao, R.drawable.icon_ar_red, "ar/ar_red"));
        return mSticker2DItems;
    }

    private static List<StickerItem> getStickerZhuTiARItems(Context mContext) {
        List<StickerItem> mSticker2DItems = new ArrayList<>();

        mSticker2DItems.add(new StickerItem(R.string.filter_normal, R.drawable.clear, null));
        mSticker2DItems.add(new StickerItem(R.string.sticker_ar_wanglaoji, R.drawable.icon_wanglaoji, "ar/wanglaoji"));
        return mSticker2DItems;
    }

    private static List<StickerItem> getStickerLandmarkARItems(Context mContext) {
        List<StickerItem> mSticker2DItems = new ArrayList<>();

        mSticker2DItems.add(new StickerItem(R.string.filter_normal, R.drawable.clear, null));
        mSticker2DItems.add(new StickerItem(R.string.sticker_ar_baozang, R.drawable.dasanba, "ar/baozang"));
        return mSticker2DItems;
    }

    private static List<StickerItem> getStickerLandARItems(Context mContext) {
        List<StickerItem> mSticker2DItems = new ArrayList<>();

        mSticker2DItems.add(new StickerItem(R.string.filter_normal, R.drawable.clear, null));
        mSticker2DItems.add(new StickerItem(R.string.sticker_ar_land, R.drawable.icon_gound_ar, "ar/landar"));
        return mSticker2DItems;
    }

    private static List<StickerItem> getStickerSkyARItems(Context mContext) {
        List<StickerItem> mSticker2DItems = new ArrayList<>();

        mSticker2DItems.add(new StickerItem(R.string.filter_normal, R.drawable.clear, null));
        mSticker2DItems.add(new StickerItem(R.string.sticker_ar_meilihao, R.drawable.meilihao, "ar/meilihao"));
        return mSticker2DItems;
    }



    private static List<StickerItem> getSticker2DItems(Context mContext) {
        List<StickerItem> mSticker2DItems = new ArrayList<>();

        mSticker2DItems.add(new StickerItem(R.string.filter_normal, R.drawable.clear, null));
        mSticker2DItems.add(new StickerItem(R.string.sticker_landiaoxueying, R.drawable.icon_landiaoxueying, "stickers/landiaoxueying", mContext.getString(R.string.sticker_tip_landiaoxueying)));
        mSticker2DItems.add(new StickerItem(R.string.sticker_weilandongrizhuang, R.drawable.icon_weilandongrizhuang, "stickers/weilandongrizhuang", mContext.getString(R.string.sticker_tip_weilandongrizhuang)));
        mSticker2DItems.add(new StickerItem(R.string.sticker_tiaowuhuoji, R.drawable.icon_tiaowuhuoji, "stickers/tiaowuhuoji", mContext.getString(R.string.sticker_tip_tiaowuhuoji)));
        mSticker2DItems.add(new StickerItem(R.string.sticker_lizishengdan, R.drawable.icon_lizishengdan, "stickers/lizishengdan", mContext.getString(R.string.sticker_tip_lizishengdan)));
        mSticker2DItems.add(new StickerItem(R.string.sticker_heimaoyanjing, R.drawable.icon_heimaoyanjing, "stickers/heimaoyanjing"));
        mSticker2DItems.add(new StickerItem(R.string.sticker_chitushaonv, R.drawable.icon_chitushaonv, "stickers/chitushaonv"));
        mSticker2DItems.add(new StickerItem(R.string.sticker_huahua, R.drawable.icon_huahua, "stickers/huahua", mContext.getString(R.string.sticker_tip_huahua)));
        mSticker2DItems.add(new StickerItem(R.string.sticker_zhaocaimao, R.drawable.icon_zhaocaimao, "stickers/zhaocaimao"));
        mSticker2DItems.add(new StickerItem(R.string.sticker_wochaotian, R.drawable.icon_wochaotian, "stickers/wochaotian"));
        mSticker2DItems.add(new StickerItem(R.string.sticker_xiatiandefeng, R.drawable.icon_xiatiandefeng, "stickers/xiatiandefeng", mContext.getString(R.string.sticker_tip_xiatiandefeng)));
        mSticker2DItems.add(new StickerItem(R.string.sticker_shengrikuaile, R.drawable.icon_shengrikuaile, "stickers/shengrikuaile"));
        mSticker2DItems.add(new StickerItem(R.string.sticker_zhutouzhuer, R.drawable.icon_zhutouzhuer, "stickers/zhutouzhuer"));
        mSticker2DItems.add(new StickerItem(R.string.sticker_huanletuchiluobo, R.drawable.icon_huanletuchiluobo, "stickers/huanletuchiluobo"));
        return mSticker2DItems;
    }

    private static List<StickerItem> getSticker3DItems(Context mContext) {
        List<StickerItem> mSticker3DItems = new ArrayList<>();

        mSticker3DItems.add(new StickerItem(R.string.filter_normal, R.drawable.clear, null));
        mSticker3DItems.add(new StickerItem(R.string.sticker_zhuluojimaoxian, R.drawable.icon_zhuluojimaoxian, "stickers/zhuluojimaoxian", mContext.getString(R.string.sticker_tip_zhuluojimaoxian)));
        mSticker3DItems.add(new StickerItem(R.string.sticker_nuannuandoupeng, R.drawable.icon_nuannuandoupeng, "stickers/nuannuandoupeng", mContext.getString(R.string.sticker_tip_nuannuandoupeng)));
        mSticker3DItems.add(new StickerItem(R.string.sticker_haoqilongbao, R.drawable.icon_haoqilongbao, "stickers/haoqilongbao", mContext.getString(R.string.sticker_tip_haoqilongbao)));
        mSticker3DItems.add(new StickerItem(R.string.sticker_konglongshiguangji, R.drawable.icon_konglongshiguangji, "stickers/konglongshiguangji", mContext.getString(R.string.sticker_tip_konglongshiguangji)));
        mSticker3DItems.add(new StickerItem(R.string.sticker_konglongceshi, R.drawable.icon_konglongceshi, "stickers/konglongceshi", mContext.getString(R.string.sticker_tip_konglongceshi)));

        return mSticker3DItems;
    }

    private static List<StickerItem> getARShoes(Context mContext){
        List<StickerItem> mSticker3DItems = new ArrayList<>();
        mSticker3DItems.add(new StickerItem(R.string.filter_normal, R.drawable.clear, null));
        mSticker3DItems.add(new StickerItem(R.string.sticker_tryshoe, R.drawable.icon_shoe1, "ar/shoe1", mContext.getString(R.string.sticker_tip_tryshoe)));
        mSticker3DItems.add(new StickerItem(R.string.sticker_tryshoe, R.drawable.icon_shoe2, "ar/shoe2", mContext.getString(R.string.sticker_tip_tryshoe)));
        mSticker3DItems.add(new StickerItem(R.string.sticker_tryshoe, R.drawable.icon_shoe3, "ar/shoe3", mContext.getString(R.string.sticker_tip_tryshoe)));
        mSticker3DItems.add(new StickerItem(R.string.sticker_tryshoe, R.drawable.icon_gucciace_bee, "ar/shoe4", mContext.getString(R.string.sticker_tip_tryshoe)));
        mSticker3DItems.add(new StickerItem(R.string.sticker_tryshoe, R.drawable.icon_gucci_duck, "ar/shoe5", mContext.getString(R.string.sticker_tip_tryshoe)));
        mSticker3DItems.add(new StickerItem(R.string.sticker_tryshoe, R.drawable.icon_gucci_3duck, "ar/shoe6", mContext.getString(R.string.sticker_tip_tryshoe)));
        mSticker3DItems.add(new StickerItem(R.string.sticker_tryshoe, R.drawable.icon_gucci_gg, "ar/shoe7", mContext.getString(R.string.sticker_tip_tryshoe)));
        mSticker3DItems.add(new StickerItem(R.string.sticker_tryshoe, R.drawable.icon_lv_black, "ar/shoe8", mContext.getString(R.string.sticker_tip_tryshoe)));
        return mSticker3DItems;


    }
    private static List<StickerItem> getARHat(Context mContext){
        List<StickerItem> mSticker3DItems = new ArrayList<>();
        mSticker3DItems.add(new StickerItem(R.string.filter_normal, R.drawable.clear, null));
        mSticker3DItems.add(new StickerItem(R.string.sticker_beilei, R.drawable.icon_beilei, "ar/hat_beilei"));
        return mSticker3DItems;


    }

    private static List<StickerItem> getARNail(Context mContext){
        List<StickerItem> mSticker3DItems = new ArrayList<>();
        mSticker3DItems.add(new StickerItem(R.string.filter_normal, R.drawable.clear, null));
        mSticker3DItems.add(new StickerItem(R.string.sticker_meijia, R.drawable.icon_meijia, "ar/nail_xiarimeijia"));
        return mSticker3DItems;
    }

    private static List<StickerItem> getARRing(Context mContext){
        List<StickerItem> mSticker3DItems = new ArrayList<>();
        mSticker3DItems.add(new StickerItem(R.string.filter_normal, R.drawable.clear, null));
        mSticker3DItems.add(new StickerItem(R.string.sticker_ring_zuanjie, R.drawable.icon_zuanjie, "ar/ring_zuanjie"));
        mSticker3DItems.add(new StickerItem(R.string.sticker_ring_banbi, R.drawable.icon_banbi, "ar/ring_ido"));
        return mSticker3DItems;


    }

    private static List<StickerItem> getARGlasss(Context mContext){
        List<StickerItem> mSticker3DItems = new ArrayList<>();
        mSticker3DItems.add(new StickerItem(R.string.filter_normal, R.drawable.clear, null));
        mSticker3DItems.add(new StickerItem(R.string.sticker_glass, R.drawable.icon_anker_glasses, "ar/glasses_0"));
        mSticker3DItems.add(new StickerItem(R.string.sticker_glass, R.drawable.icon_dane_blue, "ar/glasses_1"));
        mSticker3DItems.add(new StickerItem(R.string.sticker_glass, R.drawable.icon_starship, "ar/glasses_2"));
        mSticker3DItems.add(new StickerItem(R.string.sticker_glass, R.drawable.icon_artiste, "ar/glasses_3"));
        mSticker3DItems.add(new StickerItem(R.string.sticker_glass, R.drawable.icon_norman, "ar/glasses_4"));
        mSticker3DItems.add(new StickerItem(R.string.sticker_glass, R.drawable.icon_maverick, "ar/glasses_5"));
        return mSticker3DItems;


    }

    private static List<StickerItem> get3DWatch(Context mContext){
        List<StickerItem> mSticker3DItems = new ArrayList<>();
        mSticker3DItems.add(new StickerItem(R.string.filter_normal, R.drawable.clear, null));
        mSticker3DItems.add(new StickerItem(R.string.sticker_tryWatch01, R.drawable.icon_watch1, "stickers/watch1"));
        mSticker3DItems.add(new StickerItem(R.string.sticker_tryWatch02, R.drawable.icon_watch2, "stickers/watch2"));
        return mSticker3DItems;


    }
    private static List<StickerItem> get3DBracelet(Context mContext){
        List<StickerItem> mSticker3DItems = new ArrayList<>();
        mSticker3DItems.add(new StickerItem(R.string.filter_normal, R.drawable.clear, null));
        mSticker3DItems.add(new StickerItem(R.string.sticker_tryBracelet01, R.drawable.icon_bracelet1, "stickers/bracelet1"));
        mSticker3DItems.add(new StickerItem(R.string.sticker_tryBracelet02, R.drawable.icon_bracelet2, "stickers/bracelet2"));
        return mSticker3DItems;


    }


    private static List<StickerItem> getStickerAmazingItems(Context mContext) {
        List<StickerItem> mStickerAdvancedItems = new ArrayList<>();

        mStickerAdvancedItems.add(new StickerItem(R.string.filter_normal, R.drawable.clear, null));
        mStickerAdvancedItems.add(new StickerItem(R.string.sticker_shahua, R.drawable.icon_shahua, "stickers/shahua", mContext.getString(R.string.sticker_tip_shahua)));
        mStickerAdvancedItems.add(new StickerItem(R.string.sticker_only_gan, R.drawable.icon_only_gan, "stickers/only_gan", mContext.getString(R.string.sticker_only_gan_tips)));
        mStickerAdvancedItems.add(new StickerItem(R.string.sticker_sd_gan, R.drawable.icon_sd_gan, "stickers/sd_gan", mContext.getString(R.string.sticker_sd_gan_tips)));
        mStickerAdvancedItems.add(new StickerItem(R.string.sticker_baby_gan, R.drawable.icon_baby_gan, "stickers/baby_gan"));
        mStickerAdvancedItems.add(new StickerItem(R.string.sticker_mofabianshen, R.drawable.icon_mofabianshen, "stickers/stickers_mofabianshen", mContext.getString(R.string.sticker_tip_mofabianshen)));
        mStickerAdvancedItems.add(new StickerItem(R.string.sticker_chaoqiangbianshenshu, R.drawable.icon_chaoqiangbianshenshu, "stickers/stickers_chaoqiangbianshenshu", mContext.getString(R.string.sticker_tip_chaoqiangbianshenshu)));
        mStickerAdvancedItems.add(new StickerItem(R.string.sticker_bianshenqinglv, R.drawable.icon_bianshenqinglv, "stickers/stickers_bianshenqinglv"));
        return mStickerAdvancedItems;
    }

    private static List<StickerItem> getStickerComplexItems(Context mContext) {
        List<StickerItem> mStickerComplexItems = new ArrayList<>();

        mStickerComplexItems.add(new StickerItem(R.string.filter_normal, R.drawable.clear, null));
        mStickerComplexItems.add(new StickerItem(R.string.sticker_maobing, R.drawable.icon_maobing, "stickers/maobing", mContext.getString(R.string.sticker_tip_snap_with_cats)));
        mStickerComplexItems.add(new StickerItem(R.string.sticker_kongquegongzhu, R.drawable.icon_kongquegongzhu, "stickers/kongquegongzhu", mContext.getString(R.string.sticker_tip_kongquegongzhu)));
        mStickerComplexItems.add(new StickerItem(R.string.sticker_eldermakup, R.drawable.icon_eldermakup, "stickers/eldermakup"));
        mStickerComplexItems.add(new StickerItem(R.string.sticker_kidmakup, R.drawable.icon_kidmakup, "stickers/kidmakup"));
        mStickerComplexItems.add(new StickerItem(R.string.sticker_zisemeihuo, R.drawable.icon_zisemeihuo, "stickers/zisemeihuo", mContext.getString(R.string.sticker_tip_zisemeihuo)));
        mStickerComplexItems.add(new StickerItem(R.string.sticker_yanlidoushini, R.drawable.icon_yanlidoushini, "stickers/yanlidoushini", mContext.getString(R.string.sticker_tip_yanlidoushini)));
        mStickerComplexItems.add(new StickerItem(R.string.sticker_xiaribingshuang, R.drawable.icon_xiaribingshuang, "stickers/xiaribingshuang", mContext.getString(R.string.sticker_tip_xiaribingshuang)));
        mStickerComplexItems.add(new StickerItem(R.string.sticker_biaobaqixi, R.drawable.icon_biaobaiqixi, "stickers/biaobaiqixi", mContext.getString(R.string.sticker_tip_biaobaqixi)));
        mStickerComplexItems.add(new StickerItem(R.string.sticker_cinamiheti, R.drawable.icon_cinamiheti, "stickers/cinamiheti", mContext.getString(R.string.sticker_tip_cinamiheti)));
        mStickerComplexItems.add(new StickerItem(R.string.sticker_shuiliandong, R.drawable.icon_shuiliandong, "stickers/shuiliandong", mContext.getString(R.string.sticker_tip_shuiliandong)));
        mStickerComplexItems.add(new StickerItem(R.string.sticker_mofabaoshi, R.drawable.icon_mofabaoshi, "stickers/mofabaoshi", mContext.getString(R.string.sticker_tip_mofabaoshi)));
        mStickerComplexItems.add(new StickerItem(R.string.sticker_shangke, R.drawable.icon_shangke, "stickers/shangke", mContext.getString(R.string.sticker_tip_shangke)));
        mStickerComplexItems.add(new StickerItem(R.string.sticker_baibianfaxing, R.drawable.icon_baibianfaxing, "stickers/baibianfaxing", mContext.getString(R.string.sticker_tip_baibianfaxing)));
        mStickerComplexItems.add(new StickerItem(R.string.sticker_qianduoduo, R.drawable.icon_qianduoduo, "stickers/qianduoduo", mContext.getString(R.string.sticker_tip_qianduoduo)));
        mStickerComplexItems.add(new StickerItem(R.string.sticker_meihaoxinqing, R.drawable.icon_meihaoxinqing, "stickers/meihaoxinqing", mContext.getString(R.string.sticker_tip_meihaoxinqing)));
        mStickerComplexItems.add(new StickerItem(R.string.sticker_jiancedanshenyinyuan, R.drawable.icon_jiancedanshenyinyuan, "stickers/jiancedanshenyinyuan", mContext.getString(R.string.sticker_tip_jiancedanshenyinyuan)));
        mStickerComplexItems.add(new StickerItem(R.string.sticker_shuihaimeigeqiutian, R.drawable.icon_shuihaimeigeqiutian, "stickers/shuihaimeigeqiutian", mContext.getString(R.string.sticker_tip_shuihaimeigeqiutian)));
        mStickerComplexItems.add(new StickerItem(R.string.sticker_kejiganqueaixiong, R.drawable.icon_kejiganqueaixiong, "stickers/kejiganqueaixiong", mContext.getString(R.string.sticker_tip_kejiganqueaixiong)));
        mStickerComplexItems.add(new StickerItem(R.string.sticker_mengguiyaotang, R.drawable.icon_mengguiyaotang, "stickers/mengguiyaotang", mContext.getString(R.string.sticker_tip_mengguiyaotang)));
        mStickerComplexItems.add(new StickerItem(R.string.sticker_dianjita, R.drawable.icon_dianjita, "stickers/dianjita", mContext.getString(R.string.sticker_tip_dianjita)));
        mStickerComplexItems.add(new StickerItem(R.string.sticker_katongnan, R.drawable.icon_katongnan, "stickers/katongnan"));
        mStickerComplexItems.add(new StickerItem(R.string.sticker_katongnv, R.drawable.icon_katongnv, "stickers/katongnv"));
        mStickerComplexItems.add(new StickerItem(R.string.sticker_jiamian, R.drawable.icon_jiamian, "stickers/jiamian", mContext.getString(R.string.sticker_tip_jiamian)));
        mStickerComplexItems.add(new StickerItem(R.string.sticker_huanlongshu, R.drawable.icon_huanlongshu, "stickers/huanlongshu", mContext.getString(R.string.sticker_tip_huanlonghsu)));
        mStickerComplexItems.add(new StickerItem(R.string.sticker_gongzhumianju, R.drawable.icon_gongzhumianju, "stickers/gongzhumianju"));
        mStickerComplexItems.add(new StickerItem(R.string.sticker_shenshi, R.drawable.icon_shenshi, "stickers/shenshi"));
        mStickerComplexItems.add(new StickerItem(R.string.sticker_luzhihuakuang, R.drawable.icon_luzhihuakuang, "stickers/luzhihuakuang", mContext.getString(R.string.sticker_tip_luzhihuakuang)));
//        mStickerComplexItems.add(new StickerItem(mContext.getString(R.string.sticker_duobibingdu), R.drawable.icon_duobibingdu, ResourceHelper.getGamePath(mContext, "duobibingdu")));
        mStickerComplexItems.add(new StickerItem(R.string.sticker_qianshuiting, R.drawable.icon_qianshuiting, "game/qianshuiting", mContext.getString(R.string.sticker_tip_qianshuiting)));
//        mStickerComplexItems.add(new StickerItem(mContext.getString(R.string.sticker_weixiaoyaotou), R.drawable.icon_weixiaoyaotou, "stickers/weixiaoyaotou"));
        mStickerComplexItems.add(new StickerItem(R.string.sticker_zhangshangyouxiji, R.drawable.icon_zhangshangyouxiji, "stickers/zhangshangyouxiji", mContext.getString(R.string.sticker_tip_zhangshangyouxiji)));
        mStickerComplexItems.add(new StickerItem(R.string.sticker_kuailexiaopingzi, R.drawable.icon_kuailexiaopingzi, "stickers/kuailexiaopingzi", mContext.getString(R.string.sticker_tip_kuailexiaopingzi)));
        return mStickerComplexItems;
    }

    private static List<StickerItem> getAnimojiItems(Context mContext) {
        List<StickerItem> mAnimojiItems = new ArrayList<>();
        mAnimojiItems.add(new StickerItem(R.string.filter_normal, R.drawable.clear, null));
        mAnimojiItems.add(new StickerItem(R.string.animoji_boy, R.drawable.icon_moji_boy, "animoji/animoji_boy"));
        mAnimojiItems.add(new StickerItem(R.string.animoji_girl, R.drawable.icon_moji_girl, "animoji/animoji_girl"));
//        mAnimojiItems.add(new StickerItem(mContext.getString(R.string.avatar), R.drawable.icon_change_face, "animoji/Tob_avatar"));
        mAnimojiItems.add(new StickerItem(R.string.animoji_caihongzhu, R.drawable.icon_caihongzhuxiaodi, "animoji/caihongzhu"));
        mAnimojiItems.add(new StickerItem(R.string.animoji_fensebianbian, R.drawable.icon_fensebianbian, "animoji/fensebianbian"));
        mAnimojiItems.add(new StickerItem(R.string.animoji_keaizhu, R.drawable.icon_keaizhu, "animoji/keaizhu"));
        mAnimojiItems.add(new StickerItem(R.string.animoji_maoernvhai, R.drawable.icon_maoernvhai, "animoji/maoernvhai"));
        mAnimojiItems.add(new StickerItem(R.string.animoji_bagequan, R.drawable.icon_bagequan, "animoji/bagequan"));
        mAnimojiItems.add(new StickerItem(R.string.animoji_gewanggugu, R.drawable.icon_gewanggugu, "animoji/gewanggugu"));
        mAnimojiItems.add(new StickerItem(R.string.animoji_duoshanbingqilin, R.drawable.icon_duoshanbingqilin, "animoji/duoshanbingqilin"));
        return mAnimojiItems;
    }

    private static List<StickerItem> getARScanItems(Context mContext) {
        List<StickerItem> mARScanItems = new ArrayList<>();
        mARScanItems.add(new StickerItem(R.string.filter_normal, R.drawable.clear, null));

        mARScanItems.add(new StickerItem(R.string.sticker_merry_christamas, R.drawable.icon_yanjlimdaidongxi, "stickers/merry_chrismas", mContext.getString(R.string.sticker_merry_christamas_tip)));
        return mARScanItems;
    }

    private static List<StickerItem> getStyleMakeupItems(Context mContext) {
        List<StickerItem> items = new ArrayList<>();

        items.add(new StickerItem(R.string.filter_normal, R.drawable.clear, null));
        items.add(new StickerItem(R.string.style_makeup_baicha, R.drawable.icon_baicha, "style_makeup/baicha"));
        items.add(new StickerItem(R.string.style_makeup_bingchuan, R.drawable.icon_lantong, "style_makeup/binlanyantong"));
        items.add(new StickerItem(R.string.style_makeup_xiaoxiong, R.drawable.icon_xiaoxiong, "style_makeup/meiyouxiaoxiong"));
        items.add(new StickerItem(R.string.style_makeup_qise, R.drawable.icon_qise, "style_makeup/qise"));
        items.add(new StickerItem(R.string.style_makeup_aidou, R.drawable.icon_aidou, "style_makeup/aidou"));
        items.add(new StickerItem(R.string.style_makeup_youya, R.drawable.icon_youya, "style_makeup/youya"));
        items.add(new StickerItem(R.string.style_makeup_cwei, R.drawable.icon_cwei, "style_makeup/cwei"));
        items.add(new StickerItem(R.string.style_makeup_nuannan, R.drawable.icon_nuannan, "style_makeup/nuannan"));
        items.add(new StickerItem(R.string.style_makeup_baixi, R.drawable.icon_baixi, "style_makeup/baixi"));
        items.add(new StickerItem(R.string.style_makeup_wennuan, R.drawable.icon_wennuan, "style_makeup/wennuan"));
        items.add(new StickerItem(R.string.style_makeup_shensui, R.drawable.icon_shensui, "style_makeup/shensui"));
        items.add(new StickerItem(R.string.style_makeup_tianmei, R.drawable.icon_tianmei, "style_makeup/tianmei"));
        items.add(new StickerItem(R.string.style_makeup_duanmei, R.drawable.icon_duanmei, "style_makeup/duanmei"));
        items.add(new StickerItem(R.string.style_makeup_oumei, R.drawable.icon_oumei, "style_makeup/oumei"));
        items.add(new StickerItem(R.string.style_makeup_zhigan, R.drawable.icon_zhigan, "style_makeup/zhigan"));
        items.add(new StickerItem(R.string.style_makeup_hanxi, R.drawable.icon_hanxi, "style_makeup/hanxi"));
        items.add(new StickerItem(R.string.style_makeup_yuanqi, R.drawable.icon_yuanqi, "style_makeup/yuanqi"));

        return items;
    }


}
