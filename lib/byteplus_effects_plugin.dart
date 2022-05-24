// ignore_for_file: constant_identifier_names
// ignore_for_file: camel_case_types

import 'dart:async';

import 'package:flutter/services.dart';

class ByteplusEffectsPlugin {
  final MethodChannel _channel = const MethodChannel('byteplus_effects_plugin');
  final StreamController<MethodCall> _methodStreamController = StreamController.broadcast();
  Stream<MethodCall> get _methodStream => _methodStreamController.stream;

  ByteplusEffectsPlugin._() {
    _channel.setMethodCallHandler((MethodCall call) async {
      print('[setMethodCallHandler] call = $call');
      _methodStreamController.add(call);
    });
  }

  static final ByteplusEffectsPlugin _instance = ByteplusEffectsPlugin._();
  static ByteplusEffectsPlugin get instance => _instance;

  Future<String?> get platformVersion async {
    final String? version = await _channel.invokeMethod('getPlatformVersion');
    return version;
  }

  Future<String?> pickImage(String featureType) async {
    await _channel.invokeMethod('pickImage', {
      'feature_type' : featureType
    });
    await for (MethodCall m in _methodStream) {
      if (m.method == "CameraBack") {
        return m.arguments as String?;
      }
    }
    // return null;
  }
}

class EffectsFeatureType {
  /// Feature items
  static const String FEATURE_BEAUTY_LITE = "feature_beauty_lite";
  static const String FEATURE_BEAUTY_STANDARD = "feature_beauty_standard";
  static const String FEATURE_STICKER = "feature_sticker";
  static const String FEATURE_STYLE_MAKEUP = "feature_style_makeup";
  static const String FEATURE_ANIMOJI = "feature_animoji";
  static const String FEATURE_MATTING_STIKCER = "feature_matting_sticker";
  static const String FEATURE_BACKGROUND_BLUR = "feature_background_blur";
  static const String FEATURE_AR_SCAN = "feature_ar_scan";
  static const String FEATURE_QR_SCAN = "feature_qr_scan";
  static const String FEATURE_AMAZING_STICKER = "feature_amazing_sticker";

  static const String FEATURE_FACE = "feature_face";
  static const String FEATURE_HAND = "feature_hand";
  static const String FEATURE_SKELETON = "feature_skeleton";
  static const String FEATURE_PET_FACE = "feature_pet_face";
  static const String FEATURE_HEAD_SEG = "feature_head_seg";
  static const String FEATURE_HAIR_PARSE = "feature_hair_parse";
  static const String FEATURE_PORTRAIT_MATTING = "feature_portrait";
  static const String FEATURE_SKY_SEG = "feature_sky_seg";
  static const String FEATURE_LIGHT = "feature_light";
  static const String FEATURE_HUMAN_DISTANCE = "feature_human_distance";
  static const String FEATURE_CONCENTRATE = "feature_concentrate";
  static const String FEATURE_GAZE = "feature_gaze_estimation";
  static const String FEATURE_C1 = "feature_c1";
  static const String FEATURE_C2 = "feature_c2";
  static const String FEATURE_VIDEO_CLS = "feature_video_cls";
  static const String FEATURE_CAR = "feature_car";
  static const String FEATURE_FACE_VREIFY = "feature_face_verify";
  static const String FEATURE_FACE_CLUSTER = "feature_face_cluster";
  static const String FEATURE_DYNAMIC_GESTURE = "feature_dynamic_gesture";
  static const String FEATURE_SKIN_SEGMENTATION = "feature_skin_segmentation";
  static const String FEATURE_BACH_SKELETON = "feature_bach_skeleton";
  static const String FEATURE_CHROMA_KEYING = "feature_chroma_keying";
  static const String FEATURE_SPORT_ASSISTANCE = "feature_sport_assistance";
  static const String FEATURE_VIDEO_SR = "feature_video_sr";
  static const String FEATURE_NIGHT_SCENE = "feature_night_scene";
  static const String FEATURE_ADAPTIVE_SHARPEN = "feature_adaptive_sharpen";
  static const String FEATURE_CREATION_KIT = "feature_creation_kit";

  static const String FEATURE_AR_SLAM = "feature_ar_slam";
  static const String FEATURE_AR_OBJECT = "feature_ar_object";
  static const String FEATURE_AR_LANDMARK = "feature_ar_landmark";
  static const String FEATURE_AR_SKY_LAND = "feature_sky_land";

  static const String FEATURE_AR_HAIR_DYE = "feature_ar_hair_dye";
  static const String FEATURE_AR_PURSE = "feature_ar_purse";
  static const String FEATURE_AR_NAIL = "feature_ar_nail";
  static const String FEATURE_AR_SHOE = "feature_ar_shoe";
  static const String FEATURE_AR_LIPSTICK = "feature_ar_lipstick";
  static const String FEATURE_AR_HAT = "feature_ar_hat";
  static const String FEATURE_AR_NECKLACE = "feature_ar_necklace";
  static const String FEATURE_AR_GLASSES = "feature_ar_glasses";
  static const String FEATURE_AR_BRACELET = "feature_ar_bracelet";
  static const String FEATURE_AR_RING = "feature_ar_ring";
  static const String FEATURE_AR_EARRINGS = "feature_ar_earrings";
  static const String FEATURE_AR_WATCH = "feature_ar_watch";

  static const String FEATURE_TEST_STICKER = "feature_sticker_test";
}
