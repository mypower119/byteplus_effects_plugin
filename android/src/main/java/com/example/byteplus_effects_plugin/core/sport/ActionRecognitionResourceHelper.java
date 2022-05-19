package com.example.byteplus_effects_plugin.core.sport;

import android.content.Context;

import com.example.byteplus_effects_plugin.core.algorithm.ActionRecognitionAlgorithmTask;

import java.io.File;
import java.util.Objects;

/**
 * Created on 2021/7/21 14:30
 */
public class ActionRecognitionResourceHelper implements ActionRecognitionAlgorithmTask.ActionRecognitionResourceProvider {

    public static final String RESOURCE = "resource";
    private final Context mContext;

    public ActionRecognitionResourceHelper(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public String actionRecognitionModelPath() {
        return null;
    }

    @Override
    public String templateForActionType(ActionRecognitionAlgorithmTask.ActionType actionType) {
        return null;
    }

    private String getResourcePath() {
        return Objects.requireNonNull(mContext.getExternalFilesDir("assets")).getAbsolutePath() + File.separator + RESOURCE;
    }

    private String getModelPath(String modelName) {
        return new File(new File(getResourcePath(), "ModelResource.bundle"), modelName).getAbsolutePath();
    }
}
