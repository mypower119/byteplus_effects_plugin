package com.example.byteplus_effects_plugin.algorithm.ui;

import com.example.byteplus_effects_plugin.core.algorithm.AnimojiAlgorithmTask;
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
import com.example.byteplus_effects_plugin.core.algorithm.base.AlgorithmTaskKey;

import java.util.HashMap;
import java.util.Map;

/**
 * Created on 5/12/21 10:36 AM
 */
public class AlgorithmUIFactory {
    private static final Map<AlgorithmTaskKey, AlgorithmUIGenerator> sMap = new HashMap<>();

    static {
        AlgorithmUIFactory.register(FaceAlgorithmTask.FACE, new AlgorithmUIGenerator() {
            @Override
            public AlgorithmUI create() {
                return new FaceUI();
            }
        });
        register(HeadSegAlgorithmTask.HEAD_SEGMENT, new AlgorithmUIGenerator() {
            @Override
            public AlgorithmUI create() {
                return new HeadSegUI();
            }
        });
        register(HairParserAlgorithmTask.HAIR_PARSER, new AlgorithmUIGenerator() {
            @Override
            public AlgorithmUI create() {
                return new HairParserUI();
            }
        });
        register(FaceVerifyAlgorithmTask.FACE_VERIFY, new AlgorithmUIGenerator() {
            @Override
            public AlgorithmUI create() {
                return new FaceVerifyUI();
            }
        });
        register(AnimojiAlgorithmTask.ANIMOJI, new AlgorithmUIGenerator() {
            @Override
            public AlgorithmUI create() {
                return new AnimojiUI();
            }
        });
        register(C1AlgorithmTask.C1, new AlgorithmUIGenerator() {
            @Override
            public AlgorithmUI create() {
                return new C1UI();
            }
        });
        register(C2AlgorithmTask.C2, new AlgorithmUIGenerator() {
            @Override
            public AlgorithmUI create() {
                return new C2UI();
            }
        });
        register(CarAlgorithmTask.CAR_ALGO, new AlgorithmUIGenerator() {
            @Override
            public AlgorithmUI create() {
                return new CarUI();
            }
        });
        register(ConcentrateAlgorithmTask.CONCENTRATION, new AlgorithmUIGenerator() {
            @Override
            public AlgorithmUI create() {
                return new ConcentrationUI();
            }
        });
        register(FaceClusterAlgorithmTask.FACE_CLUSTER, new AlgorithmUIGenerator() {
            @Override
            public AlgorithmUI create() {
                return new FaceClusterUI();
            }
        });
        register(GazeEstimationAlgorithmTask.GAZE_ESTIMATION, new AlgorithmUIGenerator() {
            @Override
            public AlgorithmUI create() {
                return new GazeEstimationUI();
            }
        });
        register(HairParserAlgorithmTask.HAIR_PARSER, new AlgorithmUIGenerator() {
            @Override
            public AlgorithmUI create() {
                return new HairParserUI();
            }
        });
        register(HandAlgorithmTask.HAND, new AlgorithmUIGenerator() {
            @Override
            public AlgorithmUI create() {
                return new HandUI();
            }
        });
        register(HumanDistanceAlgorithmTask.HUMAN_DISTANCE, new AlgorithmUIGenerator() {
            @Override
            public AlgorithmUI create() {
                return new HumanDistanceUI();
            }
        });
        register(LightClsAlgorithmTask.LIGHT_CLS, new AlgorithmUIGenerator() {
            @Override
            public AlgorithmUI create() {
                return new LightClsUI();
            }
        });
        register(PetFaceAlgorithmTask.PET_FACE, new AlgorithmUIGenerator() {
            @Override
            public AlgorithmUI create() {
                return new PetFaceUI();
            }
        });
        register(PortraitMattingAlgorithmTask.PORTRAIT_MATTING, new AlgorithmUIGenerator() {
            @Override
            public AlgorithmUI create() {
                return new PortraitMattingUI();
            }
        });
        register(SkeletonAlgorithmTask.SKELETON, new AlgorithmUIGenerator() {
            @Override
            public AlgorithmUI create() {
                return new SkeletonUI();
            }
        });
        register(SkySegAlgorithmTask.SKY_SEGMENT, new AlgorithmUIGenerator() {
            @Override
            public AlgorithmUI create() {
                return new SkySegUI();
            }
        });
        register(StudentIdOcrAlgorithmTask.STUDENT_ID_OCR, new AlgorithmUIGenerator() {
            @Override
            public AlgorithmUI create() {
                return new StudentIdOcrUI();
            }
        });
        register(VideoClsAlgorithmTask.VIDEO_CLS, new AlgorithmUIGenerator() {
            @Override
            public AlgorithmUI create() {
                return new VideoClsUI();
            }
        });
        register(DynamicGestureAlgorithmTask.DYNAMIC_GESTURE, new AlgorithmUIGenerator() {
            @Override
            public AlgorithmUI create() { return new DynamicGestureUI(); }
        });
        register(SkinSegmentationAlgorithmTask.SKIN_SEGMENTATION, new AlgorithmUIGenerator() {
            @Override
            public AlgorithmUI create() { return new SkinSegmentationUI(); }
        });
        register(BachSkeletonAlgorithmTask.BACH_SKELETON, new AlgorithmUIGenerator() {
            @Override
            public AlgorithmUI create() { return new BachSkeletonUI(); }
        });
        register(ChromaKeyingAlgorithmTask.CHROMA_KEYING, new AlgorithmUIGenerator() {
            @Override
            public AlgorithmUI create() { return new ChromaKeyingUI(); }
        });
    }

    public static void register(AlgorithmTaskKey key, AlgorithmUIGenerator generator) {
        sMap.put(key, generator);
    }

    public static AlgorithmUI create(AlgorithmTaskKey key) {
        AlgorithmUIGenerator generator = sMap.get(key);
        if (generator == null) {
            return null;
        }
        return generator.create();
    }

    public interface AlgorithmUIGenerator {
        AlgorithmUI create();
    }
}
