package com.example.byteplus_effects_plugin.app.model;

import static com.example.byteplus_effects_plugin.common.config.ImageSourceConfig.ImageSourceType.TYPE_CAMERA;

import android.content.Context;
import android.hardware.Camera;
import android.os.Build;
import android.text.TextUtils;

import com.example.byteplus_effects_plugin.ByteplusEffectsPlugin;
import com.example.byteplus_effects_plugin.algorithm.activity.AlgorithmActivity;
import com.example.byteplus_effects_plugin.algorithm.config.AlgorithmConfig;
import com.example.byteplus_effects_plugin.common.config.ImageSourceConfig;
import com.example.byteplus_effects_plugin.common.config.UIConfig;
import com.example.byteplus_effects_plugin.common.model.EffectType;
import com.example.byteplus_effects_plugin.common.utils.LocaleUtils;
import com.example.byteplus_effects_plugin.core.algorithm.BachSkeletonAlgorithmTask;
import com.example.byteplus_effects_plugin.core.algorithm.C1AlgorithmTask;
import com.example.byteplus_effects_plugin.core.algorithm.C2AlgorithmTask;
import com.example.byteplus_effects_plugin.core.algorithm.CarAlgorithmTask;
import com.example.byteplus_effects_plugin.core.algorithm.ChromaKeyingAlgorithmTask;
import com.example.byteplus_effects_plugin.core.algorithm.ConcentrateAlgorithmTask;
import com.example.byteplus_effects_plugin.core.algorithm.DynamicGestureAlgorithmTask;
import com.example.byteplus_effects_plugin.core.algorithm.FaceAlgorithmTask;
import com.example.byteplus_effects_plugin.core.algorithm.FaceClusterAlgorithmTask;
import com.example.byteplus_effects_plugin.core.algorithm.FaceVerifyAlgorithmTask;
import com.example.byteplus_effects_plugin.core.algorithm.GazeEstimationAlgorithmTask;
import com.example.byteplus_effects_plugin.core.algorithm.HairParserAlgorithmTask;
import com.example.byteplus_effects_plugin.core.algorithm.HandAlgorithmTask;
import com.example.byteplus_effects_plugin.core.algorithm.HeadSegAlgorithmTask;
import com.example.byteplus_effects_plugin.core.algorithm.HumanDistanceAlgorithmTask;
import com.example.byteplus_effects_plugin.core.algorithm.LightClsAlgorithmTask;
import com.example.byteplus_effects_plugin.core.algorithm.PetFaceAlgorithmTask;
import com.example.byteplus_effects_plugin.core.algorithm.PortraitMattingAlgorithmTask;
import com.example.byteplus_effects_plugin.core.algorithm.SkeletonAlgorithmTask;
import com.example.byteplus_effects_plugin.core.algorithm.SkinSegmentationAlgorithmTask;
import com.example.byteplus_effects_plugin.core.algorithm.SkySegAlgorithmTask;
import com.example.byteplus_effects_plugin.core.algorithm.VideoClsAlgorithmTask;
import com.example.byteplus_effects_plugin.app.DemoApplication;
import com.example.byteplus_effects_plugin.R;
import com.example.byteplus_effects_plugin.app.utils.StreamUtils;
import com.example.byteplus_effects_plugin.effect.activity.BackgroundBlurActivity;
import com.example.byteplus_effects_plugin.effect.activity.BeautyActivity;
import com.example.byteplus_effects_plugin.effect.activity.MattingStickerActivity;
import com.example.byteplus_effects_plugin.effect.activity.QRScanActivity;
import com.example.byteplus_effects_plugin.effect.activity.StickerActivity;
import com.example.byteplus_effects_plugin.effect.activity.StickerTestActivity;
import com.example.byteplus_effects_plugin.effect.activity.StyleMakeUpActivity;
import com.example.byteplus_effects_plugin.effect.config.EffectConfig;
import com.example.byteplus_effects_plugin.effect.config.StickerConfig;
import com.example.byteplus_effects_plugin.effect.manager.StickerDataManager;
import com.example.byteplus_effects_plugin.effect.utils.SLAMBlackList;
import com.example.byteplus_effects_plugin.lens.activity.ImageQualityActivity;
import com.example.byteplus_effects_plugin.lens.config.ImageQualityConfig;
import com.example.byteplus_effects_plugin.sports.SportsHomeActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created on 2021/5/18 20:50
 */
public class MainDataManager {
    /**
     * GROUP
     */
    public static final String GROUP_HOT="group_hot";
    public static final String GROUP_ALGORITHM="group_algorithm";
    public static final String GROUP_EFFECT="group_effect";
    public static final String GROUP_SPORTS="group_sports";
    public static final String GROUP_LENS="group_lens";
    public static final String GROUP_AR="group_ar";
    public static final String GROUP_AR_TRY_ON="group_ar_try_on";
    public static final String GROUP_CREATION_KIT = "group_creation_kit";
    public static final String GROUP_TEST = "group_test";

    private static HashMap<String, Integer> groupTitleMap = new HashMap<>();
    static {
        groupTitleMap.put(GROUP_HOT,  R.string.hot_feature);
        groupTitleMap.put(GROUP_ALGORITHM,  R.string.feature_algorithm);
        groupTitleMap.put(GROUP_EFFECT,  R.string.feature_effect);
        groupTitleMap.put(GROUP_SPORTS,  R.string.feature_sport);
        groupTitleMap.put(GROUP_LENS,  R.string.feature_image_quality);
        groupTitleMap.put(GROUP_AR,  R.string.feature_ar);
        groupTitleMap.put(GROUP_AR_TRY_ON,  R.string.feature_ar_try_on);
        groupTitleMap.put(GROUP_CREATION_KIT, R.string.feature_creation_kit);
        groupTitleMap.put(GROUP_TEST, R.string.feature_test);
    }


    /**
     * Feature Items
     */
    public static final String FEATURE_BEAUTY_LITE = "feature_beauty_lite";
    public static final String FEATURE_BEAUTY_STANDARD = "feature_beauty_standard";
    public static final String FEATURE_STICKER = "feature_sticker";
    public static final String FEATURE_STYLE_MAKEUP = "feature_style_makeup";
    public static final String FEATURE_ANIMOJI = "feature_animoji";
    public static final String FEATURE_MATTING_STIKCER = "feature_matting_sticker";
    public static final String FEATURE_BACKGROUND_BLUR = "feature_background_blur";
    public static final String FEATURE_AR_SCAN = "feature_ar_scan";
    public static final String FEATURE_QR_SCAN = "feature_qr_scan";
    public static final String FEATURE_AMAZING_STICKER = "feature_amazing_sticker";

    public static final String FEATURE_FACE = "feature_face";
    public static final String FEATURE_HAND = "feature_hand";
    public static final String FEATURE_SKELETON = "feature_skeleton";
    public static final String FEATURE_PET_FACE = "feature_pet_face";
    public static final String FEATURE_HEAD_SEG = "feature_head_seg";
    public static final String FEATURE_HAIR_PARSE = "feature_hair_parse";
    public static final String FEATURE_PORTRAIT_MATTING = "feature_portrait";
    public static final String FEATURE_SKY_SEG = "feature_sky_seg";
    public static final String FEATURE_LIGHT = "feature_light";
    public static final String FEATURE_HUMAN_DISTANCE = "feature_human_distance";
    public static final String FEATURE_CONCENTRATE = "feature_concentrate";
    public static final String FEATURE_GAZE = "feature_gaze_estimation";
    public static final String FEATURE_C1 = "feature_c1";
    public static final String FEATURE_C2 = "feature_c2";
    public static final String FEATURE_VIDEO_CLS = "feature_video_cls";
    public static final String FEATURE_CAR = "feature_car";
    public static final String FEATURE_FACE_VREIFY = "feature_face_verify";
    public static final String FEATURE_FACE_CLUSTER = "feature_face_cluster";
    public static final String FEATURE_DYNAMIC_GESTURE = "feature_dynamic_gesture";
    public static final String FEATURE_SKIN_SEGMENTATION = "feature_skin_segmentation";
    public static final String FEATURE_BACH_SKELETON = "feature_bach_skeleton";
    public static final String FEATURE_CHROMA_KEYING = "feature_chroma_keying";
    public static final String FEATURE_SPORT_ASSISTANCE = "feature_sport_assistance";
    public static final String FEATURE_VIDEO_SR = "feature_video_sr";
    public static final String FEATURE_NIGHT_SCENE = "feature_night_scene";
    public static final String FEATURE_ADAPTIVE_SHARPEN = "feature_adaptive_sharpen";
    public static final String FEATURE_CREATION_KIT = "feature_creation_kit";

    public static final String FEATURE_AR_SLAM = "feature_ar_slam";
    public static final String FEATURE_AR_OBJECT = "feature_ar_object";
    public static final String FEATURE_AR_LANDMARK = "feature_ar_landmark";
    public static final String FEATURE_AR_SKY_LAND = "feature_sky_land";

    public static final String FEATURE_AR_HAIR_DYE = "feature_ar_hair_dye";
    public static final String FEATURE_AR_PURSE = "feature_ar_purse";
    public static final String FEATURE_AR_NAIL = "feature_ar_nail";
    public static final String FEATURE_AR_SHOE = "feature_ar_shoe";
    public static final String FEATURE_AR_LIPSTICK = "feature_ar_lipstick";
    public static final String FEATURE_AR_HAT = "feature_ar_hat";
    public static final String FEATURE_AR_NECKLACE = "feature_ar_necklace";
    public static final String FEATURE_AR_GLASSES = "feature_ar_glasses";
    public static final String FEATURE_AR_BRACELET = "feature_ar_bracelet";
    public static final String FEATURE_AR_RING = "feature_ar_ring";
    public static final String FEATURE_AR_EARRINGS = "feature_ar_earrings";
    public static final String FEATURE_AR_WATCH = "feature_ar_watch";

    public static final String FEATURE_TEST_STICKER = "feature_sticker_test";


    private static List<FeatureTab> sFeatureTabs;

    public static HashMap<String, FeatureTabItem> featureItemMap = new HashMap<>();
    static {

        featureItemMap.put(FEATURE_BEAUTY_LITE, new FeatureTabItem(
                R.string.feature_beauty_lite,
                R.drawable.feature_beauty,
                new FeatureConfig().setActivityClassName(BeautyActivity.class.getName()).setEffectConfig(new EffectConfig().setEffectType(LocaleUtils.isAsia(ByteplusEffectsPlugin.context())? EffectType.LITE_ASIA:EffectType.LITE_NOT_ASIA))));
        featureItemMap.put(FEATURE_BEAUTY_STANDARD, new FeatureTabItem(
                R.string.feature_beauty_standard,
                R.drawable.feature_beauty,
                new FeatureConfig().setActivityClassName(BeautyActivity.class.getName()).setEffectConfig(new EffectConfig().setEffectType(LocaleUtils.isAsia(ByteplusEffectsPlugin.context())? EffectType.STANDARD_ASIA:EffectType.STANDARD_NOT_ASIA))));
        featureItemMap.put(FEATURE_STICKER, new FeatureTabItem(
                R.string.feature_sticker,
                R.drawable.feature_sticker,
                new FeatureConfig().setStickerConfig(new StickerConfig().setType(StickerDataManager.StickerType.GENERAL_STCIKER)).setActivityClassName(StickerActivity.class.getName())
        ));

        featureItemMap.put(FEATURE_QR_SCAN, new FeatureTabItem(
                R.string.feature_qr_scan,
                R.drawable.feature_qr_scan,
                new FeatureConfig().setImageSourceConfig(new ImageSourceConfig(TYPE_CAMERA, String.valueOf(Camera.CameraInfo.CAMERA_FACING_BACK))).setActivityClassName(QRScanActivity.class.getName())
        ));

        featureItemMap.put(FEATURE_STYLE_MAKEUP,  new FeatureTabItem(
                R.string.feature_style_makeup,
                R.drawable.feature_style_makeup,
                new FeatureConfig().setActivityClassName(StyleMakeUpActivity.class.getName())
        ));
        featureItemMap.put(FEATURE_AMAZING_STICKER, new FeatureTabItem(
                R.string.feature_amazing_sticker,
                R.drawable.feature_sticker,
                new FeatureConfig().setStickerConfig(new StickerConfig().setType(StickerDataManager.StickerType.AMAZING_STICKER)).setActivityClassName(StickerActivity.class.getName())
        ));
        featureItemMap.put(FEATURE_ANIMOJI, new FeatureTabItem(
                R.string.feature_animoji,
                R.drawable.feature_animoji,
                new FeatureConfig().setStickerConfig(new StickerConfig().setType(StickerDataManager.StickerType.ANIMOJI)).setActivityClassName(StickerActivity.class.getName())
        ));
        featureItemMap.put(FEATURE_MATTING_STIKCER, new FeatureTabItem(
                R.string.feature_matting_sticker,
                R.drawable.feature_matting_sticker,
                new FeatureConfig().setActivityClassName(MattingStickerActivity.class.getName())));
        featureItemMap.put(FEATURE_BACKGROUND_BLUR, new FeatureTabItem(
                R.string.feature_background_blur,
                R.drawable.feature_background_blur,
                new FeatureConfig().setActivityClassName(BackgroundBlurActivity.class.getName())));
        featureItemMap.put(FEATURE_AR_SCAN, new FeatureTabItem(
                R.string.feature_ar_scan,
                R.drawable.feature_ar_scan,
                new FeatureConfig().setStickerConfig(new StickerConfig().setType(StickerDataManager.StickerType.AR_SCAN)).setActivityClassName(StickerActivity.class.getName()).
                        setImageSourceConfig(new ImageSourceConfig(TYPE_CAMERA, String.valueOf(Camera.CameraInfo.CAMERA_FACING_BACK)))

        ));

        // AR try on start

        featureItemMap.put(FEATURE_AR_HAIR_DYE, new FeatureTabItem(
                R.string.feature_ar_hair_dye,
                R.drawable.feature_ar_hair_dye,
                new FeatureConfig().setActivityClassName(BeautyActivity.class.getName()).setEffectConfig(new EffectConfig().setEffectType(LocaleUtils.isAsia(ByteplusEffectsPlugin.context())? EffectType.STANDARD_ASIA:EffectType.STANDARD_NOT_ASIA).setFeature(FEATURE_AR_HAIR_DYE))
        ));

        featureItemMap.put(FEATURE_AR_PURSE, new FeatureTabItem(
                R.string.feature_ar_purse,
                R.drawable.feature_ar_purse,
                new FeatureConfig().setStickerConfig(new StickerConfig().setType(StickerDataManager.StickerType.AR_TRY_PURSE)).setActivityClassName(StickerActivity.class.getName())
        ));

        featureItemMap.put(FEATURE_AR_NAIL, new FeatureTabItem(
                R.string.feature_ar_nail,
                R.drawable.feature_ar_nail,
                new FeatureConfig().setStickerConfig(new StickerConfig().setType(StickerDataManager.StickerType.AR_TRY_NAIL)).setActivityClassName(StickerActivity.class.getName()).
                        setImageSourceConfig(new ImageSourceConfig(TYPE_CAMERA, String.valueOf(Camera.CameraInfo.CAMERA_FACING_BACK)))
        ));

        featureItemMap.put(FEATURE_AR_SHOE, new FeatureTabItem(
                R.string.feature_ar_shoe,
                R.drawable.feature_ar_shoe,
                new FeatureConfig().setStickerConfig(new StickerConfig().setType(StickerDataManager.StickerType.AR_TRY_SHOE)).setActivityClassName(StickerActivity.class.getName()).
                        setImageSourceConfig(new ImageSourceConfig(TYPE_CAMERA, String.valueOf(Camera.CameraInfo.CAMERA_FACING_BACK)))
        ));

        featureItemMap.put(FEATURE_AR_LIPSTICK, new FeatureTabItem(
                R.string.feature_ar_lipstick,
                R.drawable.feature_ar_lipstick,
                new FeatureConfig().setActivityClassName(BeautyActivity.class.getName()).setEffectConfig(new EffectConfig().setEffectType(LocaleUtils.isAsia(ByteplusEffectsPlugin.context())? EffectType.STANDARD_ASIA:EffectType.STANDARD_NOT_ASIA).setFeature(FEATURE_AR_LIPSTICK))
        ));

        featureItemMap.put(FEATURE_AR_HAT, new FeatureTabItem(
                R.string.feature_ar_hat,
                R.drawable.feature_ar_hat,
                new FeatureConfig().setStickerConfig(new StickerConfig().setType(StickerDataManager.StickerType.AR_TRY_HAT)).setActivityClassName(StickerActivity.class.getName()).
                        setImageSourceConfig(new ImageSourceConfig(TYPE_CAMERA, String.valueOf(Camera.CameraInfo.CAMERA_FACING_FRONT)))
        ));

        featureItemMap.put(FEATURE_AR_NECKLACE, new FeatureTabItem(
                R.string.feature_ar_necklace,
                R.drawable.feature_ar_necklace,
                new FeatureConfig().setStickerConfig(new StickerConfig().setType(StickerDataManager.StickerType.AR_TRY_NECKLACE)).setActivityClassName(StickerActivity.class.getName()).
                        setImageSourceConfig(new ImageSourceConfig(TYPE_CAMERA, String.valueOf(Camera.CameraInfo.CAMERA_FACING_FRONT)))
        ));

        featureItemMap.put(FEATURE_AR_GLASSES, new FeatureTabItem(
                R.string.feature_ar_glasses,
                R.drawable.feature_ar_glasses,
                new FeatureConfig().setStickerConfig(new StickerConfig().setType(StickerDataManager.StickerType.AR_TRY_GLASSES)).setActivityClassName(StickerActivity.class.getName()).
                        setImageSourceConfig(new ImageSourceConfig(TYPE_CAMERA, String.valueOf(Camera.CameraInfo.CAMERA_FACING_FRONT)))
        ));

        featureItemMap.put(FEATURE_AR_BRACELET, new FeatureTabItem(
                R.string.feature_ar_bracelet,
                R.drawable.feature_ar_bracelet,
                new FeatureConfig().setStickerConfig(new StickerConfig().setType(StickerDataManager.StickerType.AR_TRY_BRACELET)).setActivityClassName(StickerActivity.class.getName()).
                        setImageSourceConfig(new ImageSourceConfig(TYPE_CAMERA, String.valueOf(Camera.CameraInfo.CAMERA_FACING_BACK)))
        ));

        featureItemMap.put(FEATURE_AR_RING, new FeatureTabItem(
                R.string.feature_ar_ring,
                R.drawable.feature_ar_ring,
                new FeatureConfig().setStickerConfig(new StickerConfig().setType(StickerDataManager.StickerType.AR_TRY_RING)).setActivityClassName(StickerActivity.class.getName()).
                        setImageSourceConfig(new ImageSourceConfig(TYPE_CAMERA, String.valueOf(Camera.CameraInfo.CAMERA_FACING_BACK)))
        ));

        featureItemMap.put(FEATURE_AR_EARRINGS, new FeatureTabItem(
                R.string.feature_ar_earrings,
                R.drawable.feature_ar_earrings,
                new FeatureConfig().setStickerConfig(new StickerConfig().setType(StickerDataManager.StickerType.AR_TRY_EARRINGS)).setActivityClassName(StickerActivity.class.getName()).
                        setImageSourceConfig(new ImageSourceConfig(TYPE_CAMERA, String.valueOf(Camera.CameraInfo.CAMERA_FACING_FRONT)))
        ));

        featureItemMap.put(FEATURE_AR_WATCH, new FeatureTabItem(
                R.string.feature_ar_watch,
                R.drawable.feature_ar_watch,
                new FeatureConfig().setStickerConfig(new StickerConfig().setType(StickerDataManager.StickerType.AR_TRY_WATCH)).setActivityClassName(StickerActivity.class.getName()).
                        setImageSourceConfig(new ImageSourceConfig(TYPE_CAMERA, String.valueOf(Camera.CameraInfo.CAMERA_FACING_BACK)))
        ));


        // Algorithm start

        featureItemMap.put(FEATURE_FACE, new FeatureTabItem(
                R.string.feature_face,
                R.drawable.feature_face,
                new FeatureConfig().setAlgorithmConfig(new AlgorithmConfig(FaceAlgorithmTask.FACE.getKey(), mapOf(FaceAlgorithmTask.FACE.getKey(),true)))
                        .setActivityClassName(AlgorithmActivity.class.getName())
        ));
        featureItemMap.put(FEATURE_HAND, new FeatureTabItem(
                R.string.feature_hand,
                R.drawable.feature_hand,
                new FeatureConfig().setAlgorithmConfig(new AlgorithmConfig(HandAlgorithmTask.HAND.getKey(), mapOf(HandAlgorithmTask.HAND.getKey(),true)))
                        .setActivityClassName(AlgorithmActivity.class.getName())
        ));
        featureItemMap.put(FEATURE_SKELETON, new FeatureTabItem(
                R.string.feature_skeleton,
                R.drawable.feature_skeleton,
                new FeatureConfig().setAlgorithmConfig(new AlgorithmConfig(SkeletonAlgorithmTask.SKELETON.getKey(), mapOf(SkeletonAlgorithmTask.SKELETON.getKey(), true)))
                        .setActivityClassName(AlgorithmActivity.class.getName())
        ));
        featureItemMap.put(FEATURE_PET_FACE,new FeatureTabItem(
                R.string.feature_pet_face,
                R.drawable.feature_pet_face,
                new FeatureConfig().setAlgorithmConfig(new AlgorithmConfig(PetFaceAlgorithmTask.PET_FACE.getKey(), mapOf(PetFaceAlgorithmTask.PET_FACE.getKey(), true)))
                        .setActivityClassName(AlgorithmActivity.class.getName()).setImageSourceConfig(new ImageSourceConfig(TYPE_CAMERA, String.valueOf(Camera.CameraInfo.CAMERA_FACING_BACK)))
        ));
        featureItemMap.put(FEATURE_HEAD_SEG, new FeatureTabItem(
                R.string.feature_head_seg,
                R.drawable.feature_head_seg,
                new FeatureConfig().setAlgorithmConfig(new AlgorithmConfig(HeadSegAlgorithmTask.HEAD_SEGMENT.getKey(), mapOf(HeadSegAlgorithmTask.HEAD_SEGMENT.getKey(), true)))
                        .setActivityClassName(AlgorithmActivity.class.getName())
        ));
        featureItemMap.put(FEATURE_PORTRAIT_MATTING,new FeatureTabItem(
                R.string.feature_portrait,
                R.drawable.feature_portrait,
                new FeatureConfig().setAlgorithmConfig(new AlgorithmConfig(PortraitMattingAlgorithmTask.PORTRAIT_MATTING.getKey(), mapOf(PortraitMattingAlgorithmTask.PORTRAIT_MATTING.getKey(),true)))
                        .setActivityClassName(AlgorithmActivity.class.getName())
        ));
        featureItemMap.put(FEATURE_HAIR_PARSE, new FeatureTabItem(
                R.string.feature_hair_parse,
                R.drawable.feature_hair_parse,
                new FeatureConfig().setAlgorithmConfig(new AlgorithmConfig(HairParserAlgorithmTask.HAIR_PARSER.getKey(), mapOf(HairParserAlgorithmTask.HAIR_PARSER.getKey(), true)))
                        .setActivityClassName(AlgorithmActivity.class.getName())
        ));
        featureItemMap.put(FEATURE_SKY_SEG,
                new FeatureTabItem(
                        R.string.feature_sky_seg,
                        R.drawable.feature_sky_seg,
                        new FeatureConfig().setAlgorithmConfig(new AlgorithmConfig(SkySegAlgorithmTask.SKY_SEGMENT.getKey(), mapOf(SkySegAlgorithmTask.SKY_SEGMENT.getKey(), true)))
                                .setActivityClassName(AlgorithmActivity.class.getName()).setImageSourceConfig(new ImageSourceConfig(TYPE_CAMERA, String.valueOf(Camera.CameraInfo.CAMERA_FACING_BACK)))
                ));
        featureItemMap.put(FEATURE_LIGHT,
                new FeatureTabItem(
                        R.string.feature_light,
                        R.drawable.feature_light,
                        new FeatureConfig().setAlgorithmConfig(new AlgorithmConfig(LightClsAlgorithmTask.LIGHT_CLS.getKey(), mapOf(LightClsAlgorithmTask.LIGHT_CLS.getKey(),true)))
                                .setActivityClassName(AlgorithmActivity.class.getName())
                ));
        featureItemMap.put(FEATURE_HUMAN_DISTANCE,
                new FeatureTabItem(
                        R.string.feature_human_distance,
                        R.drawable.feature_human_distance,
                        new FeatureConfig().setAlgorithmConfig(new AlgorithmConfig(HumanDistanceAlgorithmTask.HUMAN_DISTANCE.getKey(), mapOf(HumanDistanceAlgorithmTask.HUMAN_DISTANCE.getKey(), true)))
                                .setActivityClassName(AlgorithmActivity.class.getName())
                ));
        featureItemMap.put(FEATURE_CONCENTRATE,
                new FeatureTabItem(
                        R.string.feature_concentrate,
                        R.drawable.feature_concentrate,
                        new FeatureConfig().setAlgorithmConfig(new AlgorithmConfig(ConcentrateAlgorithmTask.CONCENTRATION.getKey(), mapOf(ConcentrateAlgorithmTask.CONCENTRATION.getKey(), true)))
                                .setActivityClassName(AlgorithmActivity.class.getName())
                ));
        featureItemMap.put(FEATURE_GAZE,
                new FeatureTabItem(
                        R.string.feature_gaze_estimation,
                        R.drawable.feature_gaze_estimation,
                        new FeatureConfig().setAlgorithmConfig(new AlgorithmConfig(GazeEstimationAlgorithmTask.GAZE_ESTIMATION.getKey(), mapOf(GazeEstimationAlgorithmTask.GAZE_ESTIMATION.getKey(), true)))
                                .setActivityClassName(AlgorithmActivity.class.getName())
                ));
        featureItemMap.put(FEATURE_C1,
                new FeatureTabItem(
                        R.string.feature_c1,
                        R.drawable.feature_c1,
                        new FeatureConfig().setAlgorithmConfig(new AlgorithmConfig(C1AlgorithmTask.C1.getKey(), mapOf(C1AlgorithmTask.C1.getKey(), true)))
                                .setActivityClassName(AlgorithmActivity.class.getName()).setImageSourceConfig(new ImageSourceConfig(TYPE_CAMERA, String.valueOf(Camera.CameraInfo.CAMERA_FACING_BACK)))
                ));
        featureItemMap.put(FEATURE_C2,
                new FeatureTabItem(
                        R.string.feature_c2,
                        R.drawable.feature_c2,
                        new FeatureConfig().setAlgorithmConfig(new AlgorithmConfig(C2AlgorithmTask.C2.getKey(), mapOf(C2AlgorithmTask.C2.getKey(), true)))
                                .setActivityClassName(AlgorithmActivity.class.getName()).setImageSourceConfig(new ImageSourceConfig(TYPE_CAMERA, String.valueOf(Camera.CameraInfo.CAMERA_FACING_BACK)))
                ));
        featureItemMap.put(FEATURE_CAR,
                new FeatureTabItem(
                        R.string.feature_car,
                        R.drawable.feature_car,
                        new FeatureConfig().setAlgorithmConfig(new AlgorithmConfig(CarAlgorithmTask.CAR_ALGO.getKey(), mapOf( CarAlgorithmTask.CAR_ALGO.getKey(), true,CarAlgorithmTask.CAR_RECOG.getKey(), true)))
                                .setActivityClassName(AlgorithmActivity.class.getName()).setImageSourceConfig(new ImageSourceConfig(TYPE_CAMERA, String.valueOf(Camera.CameraInfo.CAMERA_FACING_BACK)))
                ));
        featureItemMap.put(FEATURE_VIDEO_CLS,
                new FeatureTabItem(
                        R.string.feature_video_cls,
                        R.drawable.feature_video_cls,
                        new FeatureConfig().setAlgorithmConfig(new AlgorithmConfig(VideoClsAlgorithmTask.VIDEO_CLS.getKey(), mapOf(VideoClsAlgorithmTask.VIDEO_CLS.getKey(), true)))
                                .setActivityClassName(AlgorithmActivity.class.getName()).setImageSourceConfig(new ImageSourceConfig(TYPE_CAMERA, String.valueOf(Camera.CameraInfo.CAMERA_FACING_BACK)))
                ));
        featureItemMap.put(FEATURE_FACE_VREIFY,
                new FeatureTabItem(
                        R.string.feature_face_verify,
                        R.drawable.feature_face_verify,
                        new FeatureConfig().setAlgorithmConfig(new AlgorithmConfig(FaceVerifyAlgorithmTask.FACE_VERIFY.getKey(), mapOf(FaceVerifyAlgorithmTask.FACE_VERIFY.getKey(), true)))
                                .setActivityClassName(AlgorithmActivity.class.getName())
                ));
        featureItemMap.put(FEATURE_FACE_CLUSTER,
                new FeatureTabItem(
                        R.string.feature_face_cluster,
                        R.drawable.feature_face_cluster,
                        new FeatureConfig().setAlgorithmConfig(new AlgorithmConfig(FaceClusterAlgorithmTask.FACE_CLUSTER.getKey(),  mapOf(FaceClusterAlgorithmTask.FACE_CLUSTER.getKey(), true)))
                                .setActivityClassName(AlgorithmActivity.class.getName())
                        .setUiConfig(new UIConfig().setEnbaleAblum(false))
                ));
        featureItemMap.put(FEATURE_DYNAMIC_GESTURE,
                new FeatureTabItem(
                        R.string.feature_dynamic_gesture,
                        R.drawable.feature_dynamic_gesture,
                        new FeatureConfig().setAlgorithmConfig(new AlgorithmConfig(DynamicGestureAlgorithmTask.DYNAMIC_GESTURE.getKey(),  mapOf(DynamicGestureAlgorithmTask.DYNAMIC_GESTURE.getKey(), true)))
                                .setActivityClassName(AlgorithmActivity.class.getName())
                ));
        featureItemMap.put(FEATURE_SKIN_SEGMENTATION,
                new FeatureTabItem(
                        R.string.feature_skin_segmentation,
                        R.drawable.feature_skin_segmentation,
                        new FeatureConfig().setAlgorithmConfig(new AlgorithmConfig(SkinSegmentationAlgorithmTask.SKIN_SEGMENTATION.getKey(),  mapOf(SkinSegmentationAlgorithmTask.SKIN_SEGMENTATION.getKey(), true)))
                                .setActivityClassName(AlgorithmActivity.class.getName())
                ));
        featureItemMap.put(FEATURE_BACH_SKELETON,
                new FeatureTabItem(
                        R.string.feature_bach_skeleton,
                        R.drawable.feature_bach_skeleton,
                        new FeatureConfig().setAlgorithmConfig(new AlgorithmConfig(BachSkeletonAlgorithmTask.BACH_SKELETON.getKey(),  mapOf(BachSkeletonAlgorithmTask.BACH_SKELETON.getKey(), true)))
                                .setActivityClassName(AlgorithmActivity.class.getName())
                ));
        featureItemMap.put(FEATURE_CHROMA_KEYING,
                new FeatureTabItem(
                        R.string.feature_chroma_keying,
                        R.drawable.feature_chroma_keying,
                        new FeatureConfig().setAlgorithmConfig(new AlgorithmConfig(ChromaKeyingAlgorithmTask.CHROMA_KEYING.getKey(),  mapOf(ChromaKeyingAlgorithmTask.CHROMA_KEYING.getKey(), true)))
                                .setActivityClassName(AlgorithmActivity.class.getName())
                ));
        featureItemMap.put(FEATURE_VIDEO_SR,
                new FeatureTabItem(
                        R.string.feature_video_sr,
                        R.drawable.feature_video_sr,
                        new FeatureConfig().setImageQualityConfig(new ImageQualityConfig(ImageQualityConfig.KEY_VIDEO_SR))
                                .setActivityClassName(ImageQualityActivity.class.getName())
                ));
        featureItemMap.put(FEATURE_NIGHT_SCENE,
                new FeatureTabItem(
                        R.string.feature_night_scene,
                        R.drawable.feature_night_scene,
                        new FeatureConfig().setImageQualityConfig(new ImageQualityConfig(ImageQualityConfig.KEY_NIGHT_SCENE))
                                .setActivityClassName(ImageQualityActivity.class.getName())
                ));
        featureItemMap.put(FEATURE_ADAPTIVE_SHARPEN,
                new FeatureTabItem(
                        R.string.feature_adaptive_sharpen,
                        R.drawable.feature_adaptive_sharpen,
                        new FeatureConfig().setImageQualityConfig(new ImageQualityConfig(ImageQualityConfig.KEY_ADAPTIVE_SHARPEN))
                                .setActivityClassName(ImageQualityActivity.class.getName())
                ));
        featureItemMap.put(FEATURE_SPORT_ASSISTANCE,
                new FeatureTabItem(
                        R.string.feature_sport_assistance,
                        R.drawable.feature_sports,
                        new FeatureConfig().setActivityClassName(SportsHomeActivity.class.getName())
                ));
        featureItemMap.put(FEATURE_CREATION_KIT,
                new FeatureTabItem(
                        R.string.feature_creation_kit,
                        R.drawable.feature_adaptive_sharpen,
                        new FeatureConfig()
                                .setActivityClassName("com.bytedance.ck.ckentrance.WelcomeActivity")
                ));
        featureItemMap.put(FEATURE_AR_SLAM,
                new FeatureTabItem(
                        R.string.feature_ar_slam,
                        R.drawable.feature_ar_slam,
                        new FeatureConfig().setStickerConfig(new StickerConfig().setType(StickerDataManager.StickerType.SLAM_STICKRE)).setActivityClassName(StickerActivity.class.getName())
                                .setImageSourceConfig(new ImageSourceConfig(TYPE_CAMERA, String.valueOf(Camera.CameraInfo.CAMERA_FACING_BACK)))
                                .setUiConfig(new UIConfig().setEnbaleAblum(false).setEnableRotate(false))


                ));
        featureItemMap.put(FEATURE_AR_OBJECT,
                new FeatureTabItem(
                        R.string.feature_ar_object,
                        R.drawable.feature_ar_obj,
                        new FeatureConfig().setStickerConfig(new StickerConfig().setType(StickerDataManager.StickerType.OBJECT_AR)).setActivityClassName(StickerActivity.class.getName())
                                .setImageSourceConfig(new ImageSourceConfig(TYPE_CAMERA, String.valueOf(Camera.CameraInfo.CAMERA_FACING_BACK)))
                                .setUiConfig(new UIConfig().setEnbaleAblum(false).setEnableRotate(false))


                ));

        featureItemMap.put(FEATURE_AR_LANDMARK,
                new FeatureTabItem(
                        R.string.feature_ar_landmark,
                        R.drawable.feature_ar_landmark,
                        new FeatureConfig().setStickerConfig(new StickerConfig().setType(StickerDataManager.StickerType.LANDMARK_AR)).setActivityClassName(StickerActivity.class.getName())
                                .setImageSourceConfig(new ImageSourceConfig(TYPE_CAMERA, String.valueOf(Camera.CameraInfo.CAMERA_FACING_BACK)))
                                .setUiConfig(new UIConfig().setEnbaleAblum(false).setEnableRotate(false))


                ));
        featureItemMap.put(FEATURE_AR_SKY_LAND,
                new FeatureTabItem(
                        R.string.feature_ar_sky_land,
                        R.drawable.feature_ar_sky_land,
                        new FeatureConfig().setStickerConfig(new StickerConfig().setType(StickerDataManager.StickerType.SKY_LAND_AR)).setActivityClassName(StickerActivity.class.getName())
                                .setImageSourceConfig(new ImageSourceConfig(TYPE_CAMERA, String.valueOf(Camera.CameraInfo.CAMERA_FACING_BACK)))
                                .setUiConfig(new UIConfig().setEnbaleAblum(false).setEnableRotate(false))


                ));
        featureItemMap.put(FEATURE_TEST_STICKER,
                new FeatureTabItem(
                        R.string.feature_test_sticker,
                        R.drawable.feature_sticker,
                        new FeatureConfig()
                        .setActivityClassName(StickerTestActivity.class.getName())
                ));
    }




    public List<FeatureTab> getFeatureTabs(Context context) {
        if (sFeatureTabs != null) {
            return sFeatureTabs;
        }
        sFeatureTabs = new ArrayList<>();
        String data = StreamUtils.readString(context, R.raw.custom_config);
        if (!TextUtils.isEmpty(data)){
            try{
                JSONArray jsonArray = new JSONArray(data);
                if (jsonArray == null) return  sFeatureTabs;
                for (int i = 0; i < jsonArray.length();i++){
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    String group = jsonObject.optString("group");
                    if (!groupTitleMap.containsKey(group)){
                        throw  new IllegalArgumentException(group + " is not in groupTitleMap");

                    }
                    FeatureTab tab = new FeatureTab(groupTitleMap.get(group),new ArrayList<>());

                    JSONArray features = jsonObject.getJSONArray("features");
                    for (int j = 0; j < features.length();j++){
                        String feature = features.optString(j);
                        if (!featureItemMap.containsKey(feature)){
                            throw  new IllegalArgumentException(feature + " not in featureItemMap");
                        }
                        if (LocaleUtils.isFaceLimit(context)){
                            if (TextUtils.equals(feature,FEATURE_FACE_VREIFY) ||TextUtils.equals(feature, FEATURE_FACE_CLUSTER) ){
                                continue;
                            }
                        }
                        // SLAM AR黑名单检查
                        if (SLAMBlackList.SLAM_BLACK_LIST.contains(Build.MODEL.toLowerCase())){
                            if (TextUtils.equals(feature,FEATURE_AR_SLAM)
                                    ||TextUtils.equals(feature, FEATURE_AR_OBJECT)
                                    ||TextUtils.equals(feature, FEATURE_AR_LANDMARK)
                                    ||TextUtils.equals(feature, FEATURE_AR_SKY_LAND)){
                                continue;
                            }
                        }

                        tab.addChild(featureItemMap.get(feature));
                    }
                    sFeatureTabs.add(tab);


                }


            } catch (JSONException e){
                e.printStackTrace();

            }
        }

        return sFeatureTabs;
    }

    private static Map<String, Object> mapOf(Object... varargs) {
        if (varargs.length % 2 != 0) {
            throw new IllegalArgumentException("args count must be multiple of 2");
        }

        Map<String, Object> map = new HashMap<>();
        for (int i = 0; i < varargs.length; i += 2) {
            map.put((String)varargs[i], varargs[i+1]);
        }
        return map;
    }
}
