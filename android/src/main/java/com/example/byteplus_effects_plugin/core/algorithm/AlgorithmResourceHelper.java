package com.example.byteplus_effects_plugin.core.algorithm;

import android.content.Context;

import java.io.File;

/**
 * Created on 5/12/21 10:48 AM
 */
public class AlgorithmResourceHelper implements FaceAlgorithmTask.FaceResourceProvider,
        HeadSegAlgorithmTask.HeadSegResourceProvider,
        HandAlgorithmTask.HandResourceProvider,
        SkeletonAlgorithmTask.SkeletonResourceProvider,
        PortraitMattingAlgorithmTask.PortraitMattingResourceProvider,
        AnimojiAlgorithmTask.AnimojiResourceProvider,
        C1AlgorithmTask.C1ResourceProvider,
        C2AlgorithmTask.C2ResourceProvider,
        CarAlgorithmTask.CarResourceProvider,
        ConcentrateAlgorithmTask.ConcentrateResourceProvider,
        FaceVerifyAlgorithmTask.FaceVerifyResourceProvider,
        GazeEstimationAlgorithmTask.GazeEstimationResourceProvider,
        HumanDistanceAlgorithmTask.HumanDistanceResourceProvider,
        LightClsAlgorithmTask.LightClsResourceProvider,
        PetFaceAlgorithmTask.PetFaceResourceProvider,
        VideoClsAlgorithmTask.VideoClsResourceProvider,
        StudentIdOcrAlgorithmTask.StudentIdOcrResourceProvider,
        SkySegAlgorithmTask.SkeSegResourceProvider,
        ActionRecognitionAlgorithmTask.ActionRecognitionResourceProvider,
        HairParserAlgorithmTask.HairParserResourceProvider,
        DynamicGestureAlgorithmTask.DynamicGestureResourceProvider,
        SkinSegmentationAlgorithmTask.SkinSegmentationResourceProvider,
        BachSkeletonAlgorithmTask.BachSkeletonResourceProvider,
        ChromaKeyingAlgorithmTask.ChromaKeyingResourceProvider{
    public static final String RESOURCE = "resource";
    public static final String FACE = "ttfacemodel/tt_face_v10.0.model";
    public static final String PETFACE = "petfacemodel/tt_petface_v5.2.model";
    public static final String HAND_DETECT = "handmodel/tt_hand_det_v11.0.model";
    public static final String HAND_BOX = "handmodel/tt_hand_box_reg_v12.0.model";
    public static final String HAND_GESTURE = "handmodel/tt_hand_gesture_v11.0.model";
    public static final String HAND_KEY_POINT = "handmodel/tt_hand_kp_v6.0.model";
    public static final String HAND_SEGMENT = "handmodel/tt_hand_seg_v2.0.model";
    public static final String FACEEXTA = "ttfacemodel/tt_face_extra_v13.0.model";
    public static final String FACEATTRI = "ttfaceattrmodel/tt_face_attribute_tob_v7.0.model";
    public static final String FACEVERIFY = "ttfaceverify/tt_faceverify_v7.0.model";
    public static final String SKELETON = "skeleton_model/tt_skeleton_v7.0.model";
    public static final String PORTRAITMATTING = "mattingmodel/tt_matting_v14.0.model";
    public static final String HEADSEGMENT = "headsegmodel/tt_headseg_v6.0.model";
    public static final String HAIRPARSING = "hairparser/tt_hair_v11.0.model";
    public static final String LIGHTCLS = "lightcls/tt_lightcls_v1.0.model";
    public static final String HUMANDIST = "humandistance/tt_humandist_v1.0.model";
    public static final String GENERAL_OBJECT_DETECT = "generalobjectmodel/tt_general_obj_detection_v1.0.model";
    public static final String GENERAL_OBJECT_CLS = "generalobjectmodel/tt_general_obj_detection_cls_v1.0.model";
    public static final String GENERAL_OBJECT_TRACK = "generalobjectmodel/tt_sample_v1.0.model";
    public static final String C1 = "c1/tt_c1_small_v8.0.model";
    public static final String C2 = "c2/tt_C2Cls_v5.0.model";
    public static final String VIDEO_CLS = "video_cls/tt_videoCls_v4.0.model";
    public static final String GAZE_ESTIMAION = "gazeestimation/tt_gaze_v3.0.model";

    public static final String CAR_DETECT = "cardamanagedetect/tt_car_damage_detect_v2.0.model";
    public static final String CAR_BRAND_DETECT = "cardamanagedetect/tt_car_landmarks_v3.0.model";
    public static final String CAR_BRAND_OCR = "cardamanagedetect/tt_car_plate_ocr_v2.0.model";
    public static final String CAR_TRACK = "cardamanagedetect/tt_car_track_v2.0.model";

    public static final String STUDENT_ID_OCR = "student_id_ocr/tt_student_id_ocr_v2.0.model";
    public static final String SKY_SEGMENT= "skysegmodel/tt_skyseg_v7.0.model";
    public static final String ANIMOJI_MODEL = "animoji/animoji_v5.0.model";
    public static final String ACTION_RECOGNITION_MODEL = "action_recognition/tt_skeletonact_tob_v7.2.model";
    public static final String ACTION_RECOGNITION_TEMPLATE_OPEN_CLOSE_JUMP = "action_recognition/openclose-tmpl.dat";
    public static final String ACTION_RECOGNITION_TEMPLATE_PLANK = "action_recognition/plank-tmpl.dat";
    public static final String ACTION_RECOGNITION_TEMPLATE_PUSH_UP = "action_recognition/pushup-tmpl.dat";
    public static final String ACTION_RECOGNITION_TEMPLATE_SIT_UP = "action_recognition/situp-tmpl.dat";
    public static final String ACTION_RECOGNITION_TEMPLATE_DEEP_SAUAT = "action_recognition/squat-tmpl.dat";
    public static final String DYNAMIC_GESTURE = "dynamic_gesture/";
    public static final String SKIN_SEGMENTATION = "skin_segmentation/";
    public static final String BACH_SKELETON = "bach_skeleton/";
    public static final String CHROMA_KEYING = "chroma_keying/";
    public static final String ACTION_RECOGNITION_TEMPLATE_LUNGE = "action_recognition/lunge.dat";
    public static final String ACTION_RECOGNITION_TEMPLATE_LUNGE_SQUAT = "action_recognition/lunge_squat.dat";
    public static final String ACTION_RECOGNITION_TEMPLATE_HIGH_RUN = "action_recognition/high_run.dat";
    public static final String ACTION_RECOGNITION_TEMPLATE_KNEELING_PUSH_UP = "action_recognition/kneeling_pushup.dat";
    public static final String ACTION_RECOGNITION_TEMPLATE_HIP_BRIDGE = "action_recognition/hip_bridge.dat";

    private final Context mContext;

    public AlgorithmResourceHelper(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public String faceModel() {
        return getModelPath(FACE);
    }

    @Override
    public String gazeEstimationModel() {
        return getModelPath(GAZE_ESTIMAION);
    }

    @Override
    public String faceVerifyModel() {
        return getModelPath(FACEVERIFY);
    }

    @Override
    public String faceExtraModel() {
        return getModelPath(FACEEXTA);
    }

    @Override
    public String faceAttrModel() {
        return getModelPath(FACEATTRI);
    }

    @Override
    public String humanDistanceModel() {
        return getModelPath(HUMANDIST);
    }

    private String getResourcePath() {
        return mContext.getExternalFilesDir("assets").getAbsolutePath() + File.separator + RESOURCE;
    }

    private String getModelPath(String modelName) {
        return new File(new File(getResourcePath(), "ModelResource.bundle"), modelName).getAbsolutePath();
    }

    @Override
    public String headSegModel() {
        return getModelPath(HEADSEGMENT);
    }

    @Override
    public String hairParserModel() {
        return getModelPath(HAIRPARSING);
    }

    @Override
    public String studentIdOcrModel() {
        return getModelPath(STUDENT_ID_OCR);
    }

    @Override
    public String animojiModel() {
        return getModelPath(ANIMOJI_MODEL);
    }

    @Override
    public String c1Model() {
        return getModelPath(C1);
    }

    @Override
    public String c2Model() {
        return getModelPath(C2);
    }

    @Override
    public String carModel() {
        return getModelPath(CAR_DETECT);
    }

    @Override
    public String carBrandModel() {
        return getModelPath(CAR_BRAND_DETECT);
    }

    @Override
    public String brandOcrModel() {
        return getModelPath(CAR_BRAND_OCR);
    }

    @Override
    public String carTrackModel() {
        return getModelPath(CAR_TRACK);
    }

    @Override
    public String handModel() {
        return getModelPath(HAND_DETECT);
    }

    @Override
    public String handBoxModel() {
        return getModelPath(HAND_BOX);
    }

    @Override
    public String handGestureModel() {
        return getModelPath(HAND_GESTURE);
    }

    @Override
    public String handKeyPointModel() {
        return getModelPath(HAND_KEY_POINT);
    }

    @Override
    public String lightClsModel() {
        return getModelPath(LIGHTCLS);
    }

    @Override
    public String petFaceModel() {
        return getModelPath(PETFACE);
    }

    @Override
    public String portraitMattingModel() {
        return getModelPath(PORTRAITMATTING);
    }

    @Override
    public String skeletonModel() {
        return getModelPath(SKELETON);
    }

    @Override
    public String videoClsModel() {
        return getModelPath(VIDEO_CLS);
    }

    @Override
    public String skySegModel() {
        return getModelPath(SKY_SEGMENT);
    }

    @Override
    public String actionRecognitionModelPath() {
        return getModelPath(ACTION_RECOGNITION_MODEL);
    }

    @Override
    public String templateForActionType(ActionRecognitionAlgorithmTask.ActionType actionType) {
        switch (actionType) {
            case OPEN_CLOSE_JUMP:
                return getModelPath(ACTION_RECOGNITION_TEMPLATE_OPEN_CLOSE_JUMP);
            case SIT_UP:
                return getModelPath(ACTION_RECOGNITION_TEMPLATE_SIT_UP);
            case DEEP_SQUAT:
                return getModelPath(ACTION_RECOGNITION_TEMPLATE_DEEP_SAUAT);
            case PUSH_UP:
                return getModelPath(ACTION_RECOGNITION_TEMPLATE_PUSH_UP);
            case PLANK:
                return getModelPath(ACTION_RECOGNITION_TEMPLATE_PLANK);
            case LUNGE:
                return getModelPath(ACTION_RECOGNITION_TEMPLATE_LUNGE);
            case LUNGE_SQUAT:
                return getModelPath(ACTION_RECOGNITION_TEMPLATE_LUNGE_SQUAT);
            case HIGH_RUN:
                return getModelPath(ACTION_RECOGNITION_TEMPLATE_HIGH_RUN);
            case KNEELING_PUSH_UP:
                return getModelPath(ACTION_RECOGNITION_TEMPLATE_KNEELING_PUSH_UP);
            case HIP_BRIDGE:
                return getModelPath(ACTION_RECOGNITION_TEMPLATE_HIP_BRIDGE);
        }
        return null;
    }

    @Override
    public String dynamicGestureModel() {
        return getModelPath(DYNAMIC_GESTURE);
    }

    @Override
    public String skinSegmentationModel() {
        return getModelPath(SKIN_SEGMENTATION);
    }

    @Override
    public String bachSkeletonModel() {
        return getModelPath(BACH_SKELETON);
    }

    @Override
    public String chromaKeyingModel() {
        return getModelPath(CHROMA_KEYING);
    }
}
