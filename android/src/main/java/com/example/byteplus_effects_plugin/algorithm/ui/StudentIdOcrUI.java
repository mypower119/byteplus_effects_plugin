//
// Created by luofei on 2020-09-06.
//


package com.example.byteplus_effects_plugin.algorithm.ui;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.byteplus_effects_plugin.algorithm.fragment.StudentIdOcrFragment;
import com.example.byteplus_effects_plugin.common.utils.BitmapUtils;
import com.example.byteplus_effects_plugin.core.algorithm.AlgorithmResourceHelper;
import com.example.byteplus_effects_plugin.core.algorithm.StudentIdOcrAlgorithmTask;
import com.example.byteplus_effects_plugin.core.algorithm.base.AlgorithmTaskKey;
import com.example.byteplus_effects_plugin.core.license.EffectLicenseHelper;
import com.example.byteplus_effects_plugin.R;
import com.bytedance.labcv.effectsdk.BefStudentIdOcrInfo;
import com.bytedance.labcv.effectsdk.BytedEffectConstants;

import java.nio.ByteBuffer;

;


public class StudentIdOcrUI extends BaseAlgorithmUI<BefStudentIdOcrInfo>
        implements StudentIdOcrFragment.IStudentIdOcrCallback, View.OnClickListener {
    private FrameLayout studentIdOcrFrameLayout;
    private ImageView studentIdOcrCancel;
    private TextView studentIdTextView;
    private ImageView studentIdOcrImageView;

    private StudentIdOcrAlgorithmTask mStudentIdOcrTask;

    @Override
    void initView() {
        super.initView();
        if (!checkAvailable(provider())) {
            return;
        }
        studentIdOcrFrameLayout = provider().findViewById(R.id.fl_student_id_ocr);
        studentIdOcrCancel = provider().findViewById(R.id.iv_student_id_ocr_cancel);
        studentIdOcrCancel.setOnClickListener(this);

        studentIdOcrImageView = provider().findViewById(R.id.iv_student_id_ocr);
        studentIdTextView = provider().findViewById(R.id.ptv_student_id_ocr);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.iv_student_id_ocr_cancel) {
            studentIdOcrFrameLayout.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onReceiveResult(BefStudentIdOcrInfo algorithmResult) {

    }

    @Override
    public IFragmentGenerator getFragmentGenerator() {
        return new IFragmentGenerator() {
            @Override
            public Fragment create() {
                return new StudentIdOcrFragment().setCallback(StudentIdOcrUI.this);
            }

            @Override
            public int title() {
                return R.string.tab_student_id_ocr;
            }

            @Override
            public AlgorithmTaskKey key() {
                return StudentIdOcrAlgorithmTask.STUDENT_ID_OCR;
            }
        };
    }

    @Override
    public void onProcessImage(Bitmap bitmap) {
        if (bitmap != null) {
            studentIdOcrFrameLayout.setVisibility(View.VISIBLE);
            studentIdOcrCancel.setVisibility(View.VISIBLE);

            StudentIdOcrAlgorithmTask studentIdOcrTask = new StudentIdOcrAlgorithmTask(provider().getContext(), new AlgorithmResourceHelper(provider().getContext()), EffectLicenseHelper.getInstance(provider().getContext()));

            int ret = studentIdOcrTask.initTask();
            if (ret == 0) {
                ByteBuffer bufer = BitmapUtils.bitmap2ByteBuffer(bitmap);
                BefStudentIdOcrInfo ocrInfo = studentIdOcrTask.process(bufer,
                        bitmap.getWidth(),
                        bitmap.getHeight(),
                        bitmap.getWidth() * 4,
                        BytedEffectConstants.PixlFormat.RGBA8888,
                        BytedEffectConstants.Rotation.CLOCKWISE_ROTATE_0);
                if (ocrInfo.getLength() > 0) {
                    studentIdOcrImageView.setVisibility(View.VISIBLE);
                    String str = new String(ocrInfo.getResult());
//                LogUtils.e("StudentOcr: " + ocrInfo.getLength() + "   " + str);
                    Bitmap dstBitmap = drawRectangle(bitmap, new Rect(ocrInfo.getX(), ocrInfo.getY(), ocrInfo.getX() + ocrInfo.getWidth(), ocrInfo.getY() + ocrInfo.getHeight()));
                    studentIdOcrImageView.setImageBitmap(dstBitmap);
                    studentIdTextView.setText(str);

                } else {
                    studentIdOcrImageView.setImageBitmap(bitmap);
                    studentIdTextView.setText("");
                }
            } else {
                studentIdOcrImageView.setImageBitmap(bitmap);
                studentIdTextView.setText("ByteEffects: init student ocr fail: " + ret);
            }
            studentIdOcrTask.destroyTask();
        }
    }

    private Bitmap drawRectangle(Bitmap imageBitmap, Rect rect) {
        int left, top, right, bottom;
        Bitmap mutableBitmap = imageBitmap.copy(Bitmap.Config.ARGB_8888, true);
        Canvas canvas = new Canvas(mutableBitmap);
        Paint paint = new Paint();
        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.STROKE);//  {zh} 不填充    {en} Not filled 
        paint.setStrokeWidth(5);  //  {zh} 线的宽度    {en} Width of line 
        canvas.drawRect(rect.left, rect.top, rect.right, rect.bottom, paint);
        return mutableBitmap;
    }
}
