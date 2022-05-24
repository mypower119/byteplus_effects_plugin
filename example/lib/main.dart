import 'dart:io';

import 'package:flutter/material.dart';
import 'dart:async';

import 'package:flutter/services.dart';
import 'package:byteplus_effects_plugin/byteplus_effects_plugin.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatefulWidget {
  const MyApp({Key? key}) : super(key: key);

  @override
  State<MyApp> createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  String _platformVersion = 'Unknown';
  String? _imagePath;

  @override
  void initState() {
    super.initState();
    initPlatformState();
  }

  // Platform messages are asynchronous, so we initialize in an async method.
  Future<void> initPlatformState() async {
    String platformVersion;
    // Platform messages may fail, so we use a try/catch PlatformException.
    // We also handle the message potentially returning null.
    try {
      platformVersion =
          await ByteplusEffectsPlugin.instance.platformVersion ?? 'Unknown platform version';
    } on PlatformException {
      platformVersion = 'Failed to get platform version.';
    }

    // If the widget was removed from the tree while the asynchronous platform
    // message was in flight, we want to discard the reply rather than calling
    // setState to update our non-existent appearance.
    if (!mounted) return;

    setState(() {
      _platformVersion = platformVersion;
    });
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Plugin example app'),
        ),
        body: SingleChildScrollView(
          child: Column(
            children: [
              Text('Running on: $_platformVersion\n\n'),
              Text('_imagePath: $_imagePath\n'),
              if(_imagePath != null && _imagePath != '')...[
                Image.file(File(_imagePath!))
              ]
            ],
          ),
        ),
        floatingActionButton: IconButton(
          icon: Icon(Icons.add),
          onPressed: () async {
            // var res = await ByteplusEffectsPlugin.instance.pickImage(EffectsFeatureType.FEATURE_AR_HAT);
            var res = await ByteplusEffectsPlugin.instance.pickImage(EffectsFeatureType.FEATURE_ANIMOJI);
            // var res = await ByteplusEffectsPlugin.instance.pickImage(EffectsFeatureType.FEATURE_ANIMOJI);
            if(res != null) {
              setState(() {
                _imagePath = res;
              });
            }
          },
        ),
      ),
    );
  }
}
