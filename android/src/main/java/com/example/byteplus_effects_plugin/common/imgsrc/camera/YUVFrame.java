package com.example.byteplus_effects_plugin.common.imgsrc.camera;

public class YUVFrame {


    // only support yuv nv21 format
    public byte[] y = null;
    public byte[] vu = null;

    private int width = 0;
    private int height = 0;
    private int format = 0;

    public int getFormat() {
        return format;
    }

    public void setFormat(int format) {
        this.format = format;
    }


    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public YUVFrame() {

    }

    public void copyFromBuffer(byte[] buffer, int width, int height) {

        if (width * height != this.width * this.height ||
            this.y == null || this.vu == null) {
            this.y = new byte[width * height];
            this.vu = new byte[width * height / 2];

            this.width = width;
            this.height = height;
        }


        System.arraycopy(buffer, 0, this.y, 0, width * height);
        System.arraycopy(buffer, width * height, this.vu, 0, width * height * 1 / 2);
    }

}
