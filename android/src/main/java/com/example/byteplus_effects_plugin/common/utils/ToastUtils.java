package com.example.byteplus_effects_plugin.common.utils;

import android.content.Context;
import android.widget.Toast;

import com.example.byteplus_effects_plugin.core.util.LogUtils;


public class ToastUtils {
    private static Context mAppContext = null;

    public static void init(Context context) {
        mAppContext = context;
    }


    public static void show(String msg) {
        if (null == mAppContext) {
            LogUtils.d("ToastUtils not inited with Context");
            return;
        }
        Toast.makeText(mAppContext, msg, Toast.LENGTH_SHORT).show();
    }



}
