package com.example.byteplus_effects_plugin.sports.model;

import android.widget.NumberPicker;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created on 2021/7/18 10:23
 */
public class NumberPickerItem {
    private final int from;
    private final int to;
    private final int value;
    private final String displaySuffix;

    public NumberPickerItem(int from, int to, int value, String displaySuffix) {
        this.from = from;
        this.to = to;
        this.value = value;
        this.displaySuffix = displaySuffix;
    }

    public void attachToNumberPicker(NumberPicker picker) {
        picker.setMinValue(from);
        picker.setMaxValue(to);
        picker.setValue(value);
        picker.setDisplayedValues(displayValues());
    }

    private String[] displayValues() {
        List<String> stringList = new ArrayList<>(to - from + 1);
        for (int i = from; i <= to; i++) {
            stringList.add(String.format(Locale.getDefault(), "%02d %s", i, displaySuffix));
        }
        return stringList.toArray(new String[0]);
    }
}
