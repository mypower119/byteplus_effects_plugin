package com.example.byteplus_effects_plugin;

import android.content.Intent;

import androidx.annotation.NonNull;

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
public class ByteplusEffectsPlugin implements FlutterPlugin, MethodCallHandler, ActivityAware, PluginRegistry.ActivityResultListener {
  /// The MethodChannel that will the communication between Flutter and native Android
  ///
  /// This local reference serves to register the plugin with the Flutter Engine and unregister it
  /// when the Flutter Engine is detached from the Activity
  private MethodChannel channel;
  private ActivityPluginBinding activityBinding;
  private FlutterPlugin.FlutterPluginBinding flutterPluginBinding;

  @Override
  public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
    System.out.print("android native onAttachedToEngine");
    this.flutterPluginBinding = flutterPluginBinding;
    channel = new MethodChannel(flutterPluginBinding.getBinaryMessenger(), "byteplus_effects_plugin");
    channel.setMethodCallHandler(this);
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
    String configA = (String) params.get("configA");
    channel.invokeMethod("ImageBack", configA);
//    Intent intent = new Intent(flutterPluginBinding.getApplicationContext(), A.class);
//    intent.putExtra("configA", configA);
//    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//    activityBinding.getActivity().startActivity(intent);
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
    if (requestCode == 99) {
      channel.invokeMethod("CameraBack", resultCode);
    }
    return false;
  }
}
