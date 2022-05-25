package com.example.byteplus_effects_plugin;

import static com.example.byteplus_effects_plugin.app.activity.PermissionsActivity.PERMISSION_AUDIO;
import static com.example.byteplus_effects_plugin.app.activity.PermissionsActivity.PERMISSION_CAMERA;
import static com.example.byteplus_effects_plugin.app.activity.PermissionsActivity.PERMISSION_STORAGE;
import static com.example.byteplus_effects_plugin.common.config.ImageSourceConfig.ImageSourceType.TYPE_CAMERA;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.hardware.Camera;
import android.os.Build;
import android.view.View;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.byteplus_effects_plugin.algorithm.config.AlgorithmConfig;
import com.example.byteplus_effects_plugin.app.activity.MainActivity;
import com.example.byteplus_effects_plugin.app.activity.PermissionsActivity;
import com.example.byteplus_effects_plugin.app.boradcast.LocalBroadcastReceiver;
import com.example.byteplus_effects_plugin.app.model.FeatureConfig;
import com.example.byteplus_effects_plugin.app.model.FeatureTabItem;
import com.example.byteplus_effects_plugin.app.model.MainDataManager;
import com.example.byteplus_effects_plugin.app.model.UserData;
import com.example.byteplus_effects_plugin.app.task.RequestLicenseTask;
import com.example.byteplus_effects_plugin.app.task.UnzipTask;
import com.example.byteplus_effects_plugin.common.config.ImageSourceConfig;
import com.example.byteplus_effects_plugin.common.config.UIConfig;
import com.example.byteplus_effects_plugin.common.customfeatureswitch.CustomFeatureSwitch;
import com.example.byteplus_effects_plugin.common.utils.ToastUtils;
import com.example.byteplus_effects_plugin.core.util.LogUtils;
import com.example.byteplus_effects_plugin.effect.config.EffectConfig;
import com.example.byteplus_effects_plugin.effect.config.StickerConfig;
import com.example.byteplus_effects_plugin.lens.config.ImageQualityConfig;
import com.example.byteplus_effects_plugin.resource.database.DatabaseManager;
import com.google.gson.Gson;
import com.tencent.bugly.crashreport.CrashReport;

import java.lang.ref.WeakReference;
import java.util.HashMap;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;

import io.flutter.embedding.engine.plugins.activity.ActivityAware;
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;
import io.flutter.plugin.common.PluginRegistry;

/** ByteplusEffectsPlugin */
public class ByteplusEffectsPlugin implements FlutterPlugin, MethodCallHandler, ActivityAware, PluginRegistry.ActivityResultListener, UnzipTask.IUnzipViewCallback {
  /// The MethodChannel that will the communication between Flutter and native Android
  ///
  /// This local reference serves to register the plugin with the Flutter Engine and unregister it
  /// when the Flutter Engine is detached from the Activity
  private MethodChannel channel;
  public static ActivityPluginBinding activityBinding;
  private FlutterPlugin.FlutterPluginBinding flutterPluginBinding;
  private static WeakReference<Context> context;
  private Point mDefaultPreviewSize = new Point(1280,720);

  @Override
  public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
    System.out.print("android native onAttachedToEngine");
    this.flutterPluginBinding = flutterPluginBinding;
    channel = new MethodChannel(flutterPluginBinding.getBinaryMessenger(), "byteplus_effects_plugin");
    channel.setMethodCallHandler(this);

    initBytePlusEffects();
  }

  private void initBytePlusEffects() {
    DatabaseManager.init(flutterPluginBinding.getApplicationContext());
    ToastUtils.init(flutterPluginBinding.getApplicationContext());
    com.example.byteplus_effects_plugin.common.utils.ToastUtils.init(flutterPluginBinding.getApplicationContext());
    CrashReport.initCrashReport(flutterPluginBinding.getApplicationContext(), "2f0fc1f6c2", true);
    context = new WeakReference<>(flutterPluginBinding.getApplicationContext());
    LogUtils.syncIsDebug(flutterPluginBinding.getApplicationContext());

    checkResourceReady();
    LocalBroadcastManager.getInstance(flutterPluginBinding.getApplicationContext()).registerReceiver(new LocalBroadcastReceiver(), new IntentFilter(LocalBroadcastReceiver.ACTION));
  }

  public void checkResourceReady() {
    UnzipTask task = new UnzipTask(this);
    task.execute(UnzipTask.DIR);
  }

  public void checkLicenseReady() {
    RequestLicenseTask task = new RequestLicenseTask(new RequestLicenseTask.ILicenseViewCallback() {

      @Override
      public Context getContext() {
        return flutterPluginBinding.getApplicationContext();
      }

      @Override
      public void onStartTask() {

      }

      @Override
      public void onEndTask(boolean result) {


      }
    });
    task.execute();
  }

  public static int getVersionCode() {
    return 4030100;
  }

  public static String getVersionName() {
    return "4.3.1_standard";
  }

  public static Context context() {
    return context.get();
  }

  @Override
  public void onMethodCall(@NonNull MethodCall call, @NonNull Result result) {
    System.out.print("android native onMethodCall");
    if (call.method.equals("getPlatformVersion")) {
      result.success("Android " + android.os.Build.VERSION.RELEASE);
    } else if (call.method.equals("pickImage")) {
      result.success("Image path");
      handlePickImage(call);
    } else {
      result.notImplemented();
    }
  }

  private void handlePickImage(@NonNull MethodCall call) {
    HashMap<String, Object> params = (HashMap<String, Object>) call.arguments;
    String featureType = (String) params.get("feature_type");

    FeatureTabItem itemConfig = MainDataManager.featureItemMap.get(featureType);
    onItemClick(itemConfig);

    // TODO: Test all features.
//        Intent intent = new Intent(flutterPluginBinding.getApplicationContext(), MainActivity.class);
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        activityBinding.getActivity().startActivity(intent);
  }

  private void onItemClick(FeatureTabItem item) {
    FeatureConfig config = item.getConfig();

    if(item.getTitleId() == R.string.feature_ar_watch)
    {
      CustomFeatureSwitch.getInstance().setFeature_watch(true);
    }
    else if(item.getTitleId() == R.string.feature_ar_bracelet)
    {
      CustomFeatureSwitch.getInstance().setFeature_bracelet(true);
    }

    startActivity(config);
  }

  private void startActivity(FeatureConfig config) {
    ImageSourceConfig imageSourceConfig;
    if (config.getImageSourceConfig() != null) {
      imageSourceConfig = config.getImageSourceConfig();
    } else {
      /** {zh}
       * 默认设置视频源为：前置相机，支持视频录制，默认分辨为mDefaultPreviewSize
       */
      /** {en}
       * Default setting video source is: front camera, support video recording, default resolution is mDefaultPreviewSize
       */

      imageSourceConfig = new ImageSourceConfig(TYPE_CAMERA, String.valueOf(Camera.CameraInfo.CAMERA_FACING_FRONT));

    }

    imageSourceConfig.setRecordable(false);
    imageSourceConfig.setRequestWidth(mDefaultPreviewSize.x);
    imageSourceConfig.setRequestHeight(mDefaultPreviewSize.y);

    Class<?> clz;
    try {
      if (config.getActivityClassName() == null) {
        throw new ClassNotFoundException();
      }
      clz = Class.forName(config.getActivityClassName());
    } catch (ClassNotFoundException e) {
      ToastUtils.show("class " + config.getActivityClassName() + " not found," +
              " ensure your config for this item");
      return;
    }

    Intent intent = new Intent(activityBinding.getActivity(), clz);
    // in sake of speed
    if (config.getAlgorithmConfig() != null){
      intent.putExtra(AlgorithmConfig.ALGORITHM_CONFIG_KEY, new Gson().toJson(config.getAlgorithmConfig()));

    }
    intent.putExtra(ImageSourceConfig.IMAGE_SOURCE_CONFIG_KEY, new Gson().toJson(imageSourceConfig));
    if (config.getImageQualityConfig() != null){
      intent.putExtra(ImageQualityConfig.IMAGE_QUALITY_KEY, new Gson().toJson(config.getImageQualityConfig()));

    }
    if (config.getStickerConfig() != null){
      intent.putExtra(StickerConfig.StickerConfigKey, new Gson().toJson(config.getStickerConfig()));

    }
    if (config.getEffectConfig() != null){
      intent.putExtra(EffectConfig.EffectConfigKey, new Gson().toJson(config.getEffectConfig()));
    }

    if (config.getUiConfig() != null){
      intent.putExtra(UIConfig.KEY, new Gson().toJson(config.getUiConfig()));
    }
    try {
      checkPermissionAndStart(intent);
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    }
  }

  private void checkPermissionAndStart(Intent intent) throws ClassNotFoundException {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      if (activityBinding.getActivity().checkSelfPermission(PERMISSION_CAMERA) != PackageManager.PERMISSION_GRANTED ||
              activityBinding.getActivity().checkSelfPermission(PERMISSION_STORAGE) != PackageManager.PERMISSION_GRANTED ||
              activityBinding.getActivity().checkSelfPermission(PERMISSION_AUDIO) != PackageManager.PERMISSION_GRANTED) {
        // start Permissions activity
        ComponentName componentName = intent.getComponent();
        intent.setClass(activityBinding.getActivity(), PermissionsActivity.class);
        intent.putExtra(PermissionsActivity.PERMISSION_SUC_ACTIVITY, Class.forName(componentName.getClassName()));
        activityBinding.getActivity().startActivity(intent);
        return;
      }
    }
    activityBinding.getActivity().startActivityForResult(intent, 99);
  }

  @Override
  public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
    channel.setMethodCallHandler(null);
  }

  @Override
  public void onAttachedToActivity(@NonNull ActivityPluginBinding binding) {
    activityBinding = binding;
    binding.addActivityResultListener(this);
  }

  @Override
  public void onDetachedFromActivityForConfigChanges() {
    onDetachedFromActivity();
  }

  @Override
  public void onReattachedToActivityForConfigChanges(@NonNull ActivityPluginBinding binding) {
    onAttachedToActivity(binding);
  }

  @Override
  public void onDetachedFromActivity() {
    activityBinding.removeActivityResultListener(this);
    activityBinding = null;
  }

  @Override
  public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
    if (requestCode == 99 && data != null && data.getExtras().containsKey("image_path")) {
      channel.invokeMethod("CameraBack", data.getExtras().get("image_path") != null ? data.getExtras().get("image_path").toString() : null);
    } else {
      channel.invokeMethod("CameraBack", null);
    }
    return false;
  }

  @Override
  public Context getContext() {
    return flutterPluginBinding.getApplicationContext();
  }

  @Override
  public void onStartTask() {

  }

  @Override
  public void onEndTask(boolean result) {
    checkLicenseReady();
  }
}
