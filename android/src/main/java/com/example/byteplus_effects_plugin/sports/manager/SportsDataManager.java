package com.example.byteplus_effects_plugin.sports.manager;

import com.example.byteplus_effects_plugin.core.algorithm.ActionRecognitionAlgorithmTask;
import com.example.byteplus_effects_plugin.R;
import com.example.byteplus_effects_plugin.sports.model.NumberPickerItem;
import com.example.byteplus_effects_plugin.sports.model.SportItem;

import java.util.Arrays;
import java.util.List;

/**
 * Created on 2021/7/15 18:43
 */
public class SportsDataManager {

    public List<SportItem> getHomeItems() {
        return Arrays.asList(
                new SportItem(ActionRecognitionAlgorithmTask.ActionType.OPEN_CLOSE_JUMP, R.drawable.img_openclose,
                        R.drawable.img_openclose_square, R.string.open_close_jump,
                        R.raw.sa_openclose, R.raw.mask_open_close_jump, false),
                new SportItem(ActionRecognitionAlgorithmTask.ActionType.DEEP_SQUAT, R.drawable.img_squat,
                        R.drawable.img_squat_square, R.string.deep_squat,
                        R.raw.sa_squat, R.raw.mask_open_close_jump, false),
                new SportItem(ActionRecognitionAlgorithmTask.ActionType.PLANK, R.drawable.img_plank,
                        R.drawable.img_plank_square, R.string.plank,
                        R.raw.sa_plank, R.raw.mask_push_up, true),
                new SportItem(ActionRecognitionAlgorithmTask.ActionType.PUSH_UP, R.drawable.img_pushup,
                        R.drawable.img_pushup_square, R.string.push_up,
                        R.raw.sa_pushup, R.raw.mask_push_up, true),
                new SportItem(ActionRecognitionAlgorithmTask.ActionType.SIT_UP, R.drawable.img_situp,
                        R.drawable.img_situp_square, R.string.sit_up,
                        R.raw.sa_situp, R.raw.mask_sit_up, true),
                new SportItem(ActionRecognitionAlgorithmTask.ActionType.HIGH_RUN, R.drawable.img_high_run,
                        R.drawable.img_high_run_square, R.string.sport_assistance_high_run,
                        R.raw.sa_high_run, R.raw.mask_open_close_jump, false),
                new SportItem(ActionRecognitionAlgorithmTask.ActionType.LUNGE, R.drawable.img_lunge,
                        R.drawable.img_lunge_square, R.string.sport_assistance_lunge,
                        R.raw.sa_lunge, R.raw.mask_deep_squat, false),
                new SportItem(ActionRecognitionAlgorithmTask.ActionType.HIP_BRIDGE, R.drawable.img_hip_bridge,
                        R.drawable.img_hip_bridge_square, R.string.sport_assistance_hip_bridge,
                        R.raw.sa_hip_bridge, R.raw.mask_sit_up, true),
                new SportItem(ActionRecognitionAlgorithmTask.ActionType.LUNGE_SQUAT, R.drawable.img_lunge_squat,
                        R.drawable.img_lunge_squat_square, R.string.sport_assistance_lunge_squat,
                        R.raw.sa_lunge_squat, R.raw.mask_deep_squat, false),
                new SportItem(ActionRecognitionAlgorithmTask.ActionType.KNEELING_PUSH_UP, R.drawable.img_kneeling_push_up,
                        R.drawable.img_kneeling_push_up_square, R.string.sport_assistance_kneeling_pushup,
                        R.raw.sa_kneeling_pushup, R.raw.mask_push_up, true)
        );
    }

    public static NumberPickerItem getMinutePickerItem(String suffix) {
        return new NumberPickerItem(
                0, 59, 2, suffix
        );
    }

    public static NumberPickerItem getSecondPickerItem(String suffix) {
        return new NumberPickerItem(
                0, 59, 0, suffix
        );
    }
}
