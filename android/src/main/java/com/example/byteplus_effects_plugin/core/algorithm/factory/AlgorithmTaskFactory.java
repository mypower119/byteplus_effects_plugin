package com.example.byteplus_effects_plugin.core.algorithm.factory;

import android.content.Context;

import com.example.byteplus_effects_plugin.core.algorithm.ActionRecognitionAlgorithmTask;
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
import com.example.byteplus_effects_plugin.core.algorithm.StudentIdOcrAlgorithmTask;
import com.example.byteplus_effects_plugin.core.algorithm.VideoClsAlgorithmTask;
import com.example.byteplus_effects_plugin.core.algorithm.base.AlgorithmResourceProvider;
import com.example.byteplus_effects_plugin.core.algorithm.base.AlgorithmTask;
import com.example.byteplus_effects_plugin.core.algorithm.base.AlgorithmTaskKey;
import com.example.byteplus_effects_plugin.core.license.EffectLicenseProvider;

import java.util.HashMap;
import java.util.Map;

/**
 * Created on 5/7/21 2:59 PM
 */
public class AlgorithmTaskFactory {
    private static final Map<AlgorithmTaskKey, AlgorithmTaskGenerator<AlgorithmResourceProvider>> sRegister = new HashMap<>();

    static {
        AlgorithmTaskFactory.register(FaceAlgorithmTask.FACE, new AlgorithmTaskGenerator<FaceAlgorithmTask.FaceResourceProvider>() {
            @Override
            public AlgorithmTask<FaceAlgorithmTask.FaceResourceProvider, ?> create(Context context, FaceAlgorithmTask.FaceResourceProvider provider, EffectLicenseProvider licenseProvider) {
                return new FaceAlgorithmTask(context, provider, licenseProvider);
            }
        });
        register(HeadSegAlgorithmTask.HEAD_SEGMENT, new AlgorithmTaskGenerator<HeadSegAlgorithmTask.HeadSegResourceProvider>() {

            @Override
            public AlgorithmTask<HeadSegAlgorithmTask.HeadSegResourceProvider, ?> create(Context context, HeadSegAlgorithmTask.HeadSegResourceProvider provider, EffectLicenseProvider licenseProvider) {
                return new HeadSegAlgorithmTask(context, provider, licenseProvider);
            }
        });
        register(HairParserAlgorithmTask.HAIR_PARSER, new AlgorithmTaskGenerator<HairParserAlgorithmTask.HairParserResourceProvider>() {
            @Override
            public AlgorithmTask<HairParserAlgorithmTask.HairParserResourceProvider, ?> create(Context context, HairParserAlgorithmTask.HairParserResourceProvider provider, EffectLicenseProvider licenseProvider) {
                return new HairParserAlgorithmTask(context, provider, licenseProvider);
            }
        });
        register(FaceVerifyAlgorithmTask.FACE_VERIFY, new AlgorithmTaskGenerator<FaceVerifyAlgorithmTask.FaceVerifyResourceProvider>() {
            @Override
            public AlgorithmTask<FaceVerifyAlgorithmTask.FaceVerifyResourceProvider, ?> create(Context context, FaceVerifyAlgorithmTask.FaceVerifyResourceProvider provider, EffectLicenseProvider licenseProvider) {
                return new FaceVerifyAlgorithmTask(context, provider, licenseProvider);
            }
        });
        register(HandAlgorithmTask.HAND, new AlgorithmTaskGenerator<HandAlgorithmTask.HandResourceProvider>() {
            @Override
            public AlgorithmTask<HandAlgorithmTask.HandResourceProvider, ?> create(Context context, HandAlgorithmTask.HandResourceProvider provider, EffectLicenseProvider licenseProvider) {
                return new HandAlgorithmTask(context, provider, licenseProvider);
            }
        });
        register(SkeletonAlgorithmTask.SKELETON, new AlgorithmTaskGenerator<SkeletonAlgorithmTask.SkeletonResourceProvider>() {
            @Override
            public AlgorithmTask<SkeletonAlgorithmTask.SkeletonResourceProvider, ?> create(Context context, SkeletonAlgorithmTask.SkeletonResourceProvider provider, EffectLicenseProvider licenseProvider) {
                return new SkeletonAlgorithmTask(context, provider, licenseProvider);
            }
        });
        register(PetFaceAlgorithmTask.PET_FACE, new AlgorithmTaskGenerator<PetFaceAlgorithmTask.PetFaceResourceProvider>() {
            @Override
            public AlgorithmTask<PetFaceAlgorithmTask.PetFaceResourceProvider, ?> create(Context context, PetFaceAlgorithmTask.PetFaceResourceProvider provider, EffectLicenseProvider licenseProvider) {
                return new PetFaceAlgorithmTask(context, provider, licenseProvider);
            }
        });
        register(SkySegAlgorithmTask.SKY_SEGMENT, new AlgorithmTaskGenerator<SkySegAlgorithmTask.SkeSegResourceProvider>() {
            @Override
            public AlgorithmTask<SkySegAlgorithmTask.SkeSegResourceProvider, ?> create(Context context, SkySegAlgorithmTask.SkeSegResourceProvider provider, EffectLicenseProvider licenseProvider) {
                return new SkySegAlgorithmTask(context, provider, licenseProvider);
            }
        });
        register(LightClsAlgorithmTask.LIGHT_CLS, new AlgorithmTaskGenerator<LightClsAlgorithmTask.LightClsResourceProvider>() {
            @Override
            public AlgorithmTask<LightClsAlgorithmTask.LightClsResourceProvider, ?> create(Context context, LightClsAlgorithmTask.LightClsResourceProvider provider, EffectLicenseProvider licenseProvider) {
                return new LightClsAlgorithmTask(context, provider, licenseProvider);
            }
        });
        register(HumanDistanceAlgorithmTask.HUMAN_DISTANCE, new AlgorithmTaskGenerator<HumanDistanceAlgorithmTask.HumanDistanceResourceProvider>() {
            @Override
            public AlgorithmTask<HumanDistanceAlgorithmTask.HumanDistanceResourceProvider, ?> create(Context context, HumanDistanceAlgorithmTask.HumanDistanceResourceProvider provider, EffectLicenseProvider licenseProvider) {
                return new HumanDistanceAlgorithmTask(context, provider, licenseProvider);
            }
        });
        register(ConcentrateAlgorithmTask.CONCENTRATION, new AlgorithmTaskGenerator<ConcentrateAlgorithmTask.ConcentrateResourceProvider>() {
            @Override
            public AlgorithmTask<ConcentrateAlgorithmTask.ConcentrateResourceProvider, ?> create(Context context, ConcentrateAlgorithmTask.ConcentrateResourceProvider provider, EffectLicenseProvider licenseProvider) {
                return new ConcentrateAlgorithmTask(context, provider, licenseProvider);
            }
        });
        register(GazeEstimationAlgorithmTask.GAZE_ESTIMATION, new AlgorithmTaskGenerator<GazeEstimationAlgorithmTask.GazeEstimationResourceProvider>() {
            @Override
            public AlgorithmTask<GazeEstimationAlgorithmTask.GazeEstimationResourceProvider, ?> create(Context context, GazeEstimationAlgorithmTask.GazeEstimationResourceProvider provider, EffectLicenseProvider licenseProvider) {
                return new GazeEstimationAlgorithmTask(context, provider, licenseProvider);
            }
        });
        register(C1AlgorithmTask.C1, new AlgorithmTaskGenerator<C1AlgorithmTask.C1ResourceProvider>() {
            @Override
            public AlgorithmTask<C1AlgorithmTask.C1ResourceProvider, ?> create(Context context, C1AlgorithmTask.C1ResourceProvider provider, EffectLicenseProvider licenseProvider) {
                return new C1AlgorithmTask(context, provider, licenseProvider);
            }
        });
        register(C2AlgorithmTask.C2, new AlgorithmTaskGenerator<C2AlgorithmTask.C2ResourceProvider>() {
            @Override
            public AlgorithmTask<C2AlgorithmTask.C2ResourceProvider, ?> create(Context context, C2AlgorithmTask.C2ResourceProvider provider, EffectLicenseProvider licenseProvider) {
                return new C2AlgorithmTask(context, provider, licenseProvider);
            }
        });
        register(VideoClsAlgorithmTask.VIDEO_CLS, new AlgorithmTaskGenerator<VideoClsAlgorithmTask.VideoClsResourceProvider>() {
            @Override
            public AlgorithmTask<VideoClsAlgorithmTask.VideoClsResourceProvider, ?> create(Context context, VideoClsAlgorithmTask.VideoClsResourceProvider provider, EffectLicenseProvider licenseProvider) {
                return new VideoClsAlgorithmTask(context, provider, licenseProvider);
            }
        });
        register(FaceClusterAlgorithmTask.FACE_CLUSTER, new AlgorithmTaskGenerator<AlgorithmResourceProvider>() {
            @Override
            public AlgorithmTask<AlgorithmResourceProvider, ?> create(Context context, AlgorithmResourceProvider provider, EffectLicenseProvider licenseProvider) {
                return new FaceClusterAlgorithmTask(context, provider, licenseProvider);
            }
        });
        register(CarAlgorithmTask.CAR_ALGO, new AlgorithmTaskGenerator<CarAlgorithmTask.CarResourceProvider>() {
            @Override
            public AlgorithmTask<CarAlgorithmTask.CarResourceProvider, ?> create(Context context, CarAlgorithmTask.CarResourceProvider provider, EffectLicenseProvider licenseProvider) {
                return new CarAlgorithmTask(context, provider, licenseProvider);
            }
        });
        register(StudentIdOcrAlgorithmTask.STUDENT_ID_OCR, new AlgorithmTaskGenerator<StudentIdOcrAlgorithmTask.StudentIdOcrResourceProvider>() {
            @Override
            public AlgorithmTask<StudentIdOcrAlgorithmTask.StudentIdOcrResourceProvider, ?> create(Context context, StudentIdOcrAlgorithmTask.StudentIdOcrResourceProvider provider, EffectLicenseProvider licenseProvider) {
                return new StudentIdOcrAlgorithmTask(context, provider, licenseProvider);
            }
        });
        register(PortraitMattingAlgorithmTask.PORTRAIT_MATTING, new AlgorithmTaskGenerator<PortraitMattingAlgorithmTask.PortraitMattingResourceProvider>() {
            @Override
            public AlgorithmTask<PortraitMattingAlgorithmTask.PortraitMattingResourceProvider, ?> create(Context context, PortraitMattingAlgorithmTask.PortraitMattingResourceProvider provider, EffectLicenseProvider licenseProvider) {
                return new PortraitMattingAlgorithmTask(context, provider, licenseProvider);
            }
        });
        register(ActionRecognitionAlgorithmTask.ACTION_RECOGNITION, new AlgorithmTaskGenerator<ActionRecognitionAlgorithmTask.ActionRecognitionResourceProvider>() {
            @Override
            public AlgorithmTask<ActionRecognitionAlgorithmTask.ActionRecognitionResourceProvider, ?> create(Context context, ActionRecognitionAlgorithmTask.ActionRecognitionResourceProvider provider, EffectLicenseProvider licenseProvider) {
                return new ActionRecognitionAlgorithmTask(context, provider, licenseProvider);
            }
        });
        register(DynamicGestureAlgorithmTask.DYNAMIC_GESTURE, new AlgorithmTaskGenerator<DynamicGestureAlgorithmTask.DynamicGestureResourceProvider>() {
            @Override
            public AlgorithmTask<DynamicGestureAlgorithmTask.DynamicGestureResourceProvider, ?> create(Context context, DynamicGestureAlgorithmTask.DynamicGestureResourceProvider provider, EffectLicenseProvider licenseProvider) {
                return new DynamicGestureAlgorithmTask(context, provider, licenseProvider);
            }
        });
        register(SkinSegmentationAlgorithmTask.SKIN_SEGMENTATION, new AlgorithmTaskGenerator<SkinSegmentationAlgorithmTask.SkinSegmentationResourceProvider>() {
            @Override
            public AlgorithmTask<SkinSegmentationAlgorithmTask.SkinSegmentationResourceProvider, ?> create(Context context, SkinSegmentationAlgorithmTask.SkinSegmentationResourceProvider provider, EffectLicenseProvider licenseProvider) {
                return new SkinSegmentationAlgorithmTask(context, provider, licenseProvider);
            }
        });
        register(BachSkeletonAlgorithmTask.BACH_SKELETON, new AlgorithmTaskGenerator<BachSkeletonAlgorithmTask.BachSkeletonResourceProvider>() {
            @Override
            public AlgorithmTask<BachSkeletonAlgorithmTask.BachSkeletonResourceProvider, ?> create(Context context, BachSkeletonAlgorithmTask.BachSkeletonResourceProvider provider, EffectLicenseProvider licenseProvider) {
                return new BachSkeletonAlgorithmTask(context, provider, licenseProvider);
            }
        });
        register(ChromaKeyingAlgorithmTask.CHROMA_KEYING, new AlgorithmTaskGenerator<ChromaKeyingAlgorithmTask.ChromaKeyingResourceProvider>() {
            @Override
            public AlgorithmTask<ChromaKeyingAlgorithmTask.ChromaKeyingResourceProvider, ?> create(Context context, ChromaKeyingAlgorithmTask.ChromaKeyingResourceProvider provider, EffectLicenseProvider licenseProvider) {
                return new ChromaKeyingAlgorithmTask(context, provider, licenseProvider);
            }
        });
    }

    public static void register(AlgorithmTaskKey key, AlgorithmTaskGenerator generator) {
        sRegister.put(key, generator);
    }

    public static <T extends AlgorithmTask<AlgorithmResourceProvider, ?>> T
        create(AlgorithmTaskKey key, Context context, AlgorithmResourceProvider provider, EffectLicenseProvider licenseProvider) {
        AlgorithmTaskGenerator<AlgorithmResourceProvider> generator = sRegister.get(key);
        if (generator == null) {
            return null;
        }
        return (T) generator.create(context, provider, licenseProvider);
    }

    public interface AlgorithmTaskGenerator<T extends AlgorithmResourceProvider> {
        AlgorithmTask<T, ?> create(Context context, T provider, EffectLicenseProvider licenseProvider);
    }
}
