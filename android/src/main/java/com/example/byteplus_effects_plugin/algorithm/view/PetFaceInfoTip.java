package com.example.byteplus_effects_plugin.algorithm.view;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.example.byteplus_effects_plugin.R;
import com.bytedance.labcv.effectsdk.BefPetFaceInfo;
import com.bytedance.labcv.effectsdk.BytedEffectConstants;

public class PetFaceInfoTip extends ResultTip<BefPetFaceInfo.PetFace> {
    private static final String TAG = "PetFaceInfoTip";
    public final int[] PET_FACE_TYPES = {R.string.cat, R.string.dog, R.string.human, R.string.unknown};
    private TextView tvPetFaceType;

    private SparseArray<TextView> mActionMap = null;
    private TextView tvLeftEye;
    private TextView tvMouth;
    private TextView tvRightEye;

    private int height = getResources().getDimensionPixelSize(R.dimen.pet_face_info_height);
    private int mExpressionOnColor;
    private int mExpressionOffColor;

    public PetFaceInfoTip(@NonNull Context context) {
        super(context);
        addLayout(context, R.layout.view_pet_face_info);
        tvPetFaceType = findViewById(R.id.tv_pet_face_type);
        tvLeftEye = findViewById(R.id.tv_left_eye_pet_face_info);
        tvRightEye = findViewById(R.id.tv_right_eye_pet_face_info);
        tvMouth = findViewById(R.id.tv_mouth_pet_face_info);

        if (mActionMap == null) {
            mActionMap = new SparseArray<>();
            mActionMap.put(BytedEffectConstants.PetFaceAction.BEF_LEFT_EYE_PET_FACE, tvLeftEye);
            mActionMap.put(BytedEffectConstants.PetFaceAction.BEF_RIGHT_EYE_PET_FACE, tvRightEye);
            mActionMap.put(BytedEffectConstants.PetFaceAction.BEF_MOUTH_PET_FACE, tvMouth);

        }
        mExpressionOffColor = ActivityCompat.getColor(getContext(), R.color.colorGrey);
        mExpressionOnColor = ActivityCompat.getColor(getContext(), R.color.colorWhite);
        for (int i = 0; i < mActionMap.size(); i++) {
            mActionMap.valueAt(i).setTextColor(mExpressionOffColor);
        }
    }

    public PetFaceInfoTip(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public PetFaceInfoTip(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    void updateInfo(BefPetFaceInfo.PetFace item) {
        if (null == item) return;

        int type = item.getType();
        if (type > PET_FACE_TYPES.length) {
            type = PET_FACE_TYPES.length;
        }
        tvPetFaceType.setText(PET_FACE_TYPES[type - 1]);

        int action = item.getAction();
        for (int i = 0; i < mActionMap.size(); i++) {
            if ((mActionMap.keyAt(i) & action) != 0) {
                mActionMap.get(mActionMap.keyAt(i)).setTextColor(mExpressionOnColor);
            } else {
                mActionMap.get(mActionMap.keyAt(i)).setTextColor(mExpressionOffColor);
            }
        }

        Rect rect = item.getRect().toRect();
        getRectInScreenCord(rect);
        int top = rect.top;
        int left = rect.left;
        int width = getResources().getDimensionPixelSize(R.dimen.pet_face_info_width);
        MarginLayoutParams marginLayoutParams = (MarginLayoutParams) this.getLayoutParams();
        marginLayoutParams.leftMargin = left;
        marginLayoutParams.topMargin = top > height ? (top - height) : 0;
        setLayoutParams(marginLayoutParams);

    }


}