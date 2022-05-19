package com.example.byteplus_effects_plugin.sports.utils;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;

/**
 * Created on 2021/12/6 16:56
 */
public class SensorManager implements SensorEventListener {
    private OnSensorChangedListener mListener;
    private android.hardware.SensorManager mManager;

    public SensorManager(OnSensorChangedListener listener) {
        this.mListener = listener;
    }

    public void start(Context context) {
        if (mManager == null) {
            mManager = (android.hardware.SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        }
        mManager.registerListener(this,
                mManager.getDefaultSensor(Sensor.TYPE_GRAVITY),
                android.hardware.SensorManager.SENSOR_DELAY_UI);
    }

    public void stop() {
        mManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float[] values = event.values;
        float x = values[0], y = values[1], z = values[2];

        float zTheta = (float) (Math.atan2(z, Math.sqrt(x * x + y * y)) / Math.PI * -90 * 2 - 90);

        if (mListener != null) {
            mListener.onSensorChanged(zTheta + 180);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public interface OnSensorChangedListener {
        void onSensorChanged(float theta);
    }
}
