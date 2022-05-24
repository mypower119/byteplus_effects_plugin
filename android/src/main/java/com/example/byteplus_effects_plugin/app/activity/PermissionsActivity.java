// Copyright (C) 2018 Beijing Bytedance Network Technology Co., Ltd.
package com.example.byteplus_effects_plugin.app.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.example.byteplus_effects_plugin.ByteplusEffectsPlugin;


public class PermissionsActivity extends Activity {
    public static final String PERMISSION_SUC_ACTIVITY = "permission_suc_activity";
    // permissions
    public static final String PERMISSION_STORAGE = Manifest.permission.WRITE_EXTERNAL_STORAGE;
    public static final String PERMISSION_AUDIO = Manifest.permission.RECORD_AUDIO;
    public static final String PERMISSION_CAMERA = Manifest.permission.CAMERA;

    // permission code
    public static final int PERMISSION_CODE_STORAGE = 1;
    public static final int PERMISSION_CODE_CAMERA = 2;
    public static final int PERMISSION_CODE_AUDIO = 3;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        checkCameraPermission();
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (null == grantResults || grantResults.length < 1) return;
        if (requestCode == PERMISSION_CODE_CAMERA) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                checkStoragePermission();
            } else {
                Toast.makeText(this, "Camera权限被拒绝", Toast.LENGTH_SHORT).show();
                finish();
            }
        } else if (requestCode == PERMISSION_CODE_STORAGE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                checkMicrophonePermission();
            } else {
                Toast.makeText(this, "存储卡读写权限被拒绝", Toast.LENGTH_SHORT).show();
                finish();
            }
        } else if (requestCode == PERMISSION_CODE_AUDIO) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startMainActivity();
            } else {
                Toast.makeText(this, "麦克风权限被拒绝", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void startMainActivity() {
        Class<?> sucActivity = (Class<?>) getIntent().getSerializableExtra(PERMISSION_SUC_ACTIVITY);
        Intent intent = new Intent(this, sucActivity);
        intent.putExtras(getIntent());
//        startActivity(intent);
        ByteplusEffectsPlugin.activityBinding.getActivity().startActivityForResult(intent, 99);
        finish();
        overridePendingTransition(0, 0);
    }

    private void checkCameraPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(PERMISSION_CAMERA) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.CAMERA}, PERMISSION_CODE_CAMERA);
            } else {
                checkStoragePermission();
            }
        } else {
            startMainActivity();
        }
    }

    private void checkStoragePermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(PERMISSION_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{PERMISSION_STORAGE},PERMISSION_CODE_STORAGE);
            } else {
                checkMicrophonePermission();
            }
        }

    }

    private void checkMicrophonePermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO}, PERMISSION_CODE_AUDIO);
            } else {
                startMainActivity();
            }
        }
    }
}
