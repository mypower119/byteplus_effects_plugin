package com.example.byteplus_effects_plugin.effect.model;

import androidx.annotation.IdRes;

public class ColorItem {
    @IdRes
    private int title;
    private float r;
    private float g;
    private float b;
    private float a = 1;

    public float getR() {
        return r;
    }

    public float getG() {
        return g;
    }

    public float getB() {
        return b;
    }

    public float getA() {
        return a;
    }

    public ColorItem(int title, float r, float g, float b) {
        this.title = title;
        this.r = r;
        this.g = g;
        this.b = b;
    }

    public ColorItem(int title, float r, float g, float b, float a) {
        this.title = title;
        this.r = r;
        this.g = g;
        this.b = b;
        this.a = a;
    }

    public int toInt(){
        return ((int)(a*255)) <<24 | ((int) (r*255))<<16|((int) (g*255))<<8|(int)(b*255);
    }
}
