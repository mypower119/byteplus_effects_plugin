// Copyright (C) 2018 Beijing Bytedance Network Technology Co., Ltd.
package com.example.byteplus_effects_plugin.app;

import android.app.Application;
import android.content.Context;

import com.example.byteplus_effects_plugin.common.utils.ToastUtils;
import com.example.byteplus_effects_plugin.core.util.LogUtils;
import com.example.byteplus_effects_plugin.resource.database.DatabaseManager;
import com.tencent.bugly.crashreport.CrashReport;

import java.lang.ref.WeakReference;

public class DemoApplication extends Application {
    private static WeakReference<Context> context;

    @Override
    public void onCreate() {
        super.onCreate();
        DatabaseManager.init(this);
        ToastUtils.init(this);
        com.example.byteplus_effects_plugin.common.utils.ToastUtils.init(this);
        CrashReport.initCrashReport(getApplicationContext(), "2f0fc1f6c2", true);
        context = new WeakReference<>(getApplicationContext());
        LogUtils.syncIsDebug(getApplicationContext());
    }

    public static Context context() {
        return context.get();
    }
}
