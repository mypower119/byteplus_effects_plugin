
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

  Future<String?> pickImage() async {
    await _channel.invokeMethod('pickImage', {
      'configA' : 'helloConfig'
    });
    await for (MethodCall m in _methodStream) {
      if (m.method == "ImageBack") {
        return m.arguments as String?;
      }
    }
  }
}
