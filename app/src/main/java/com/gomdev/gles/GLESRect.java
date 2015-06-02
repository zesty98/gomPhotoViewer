package com.gomdev.gles;

public class GLESRect {
    static final String CLASS = "GLESRect";
    static final String TAG = GLESConfig.TAG + "_" + CLASS;
    static final boolean DEBUG = GLESConfig.DEBUG;

    int mX = 0;
    int mY = 0;
    int mWidth = 0;
    int mHeight = 0;

    public GLESRect() {

    }

    public GLESRect(int x, int y, int width, int height) {
        mX = x;
        mY = y;
        mWidth = width;
        mHeight = height;
    }

    public void set(int x, int y, int width, int height) {
        mX = x;
        mY = y;
        mWidth = width;
        mHeight = height;
    }

    public void setX(int x) {
        mX = x;
    }

    public int getX() {
        return mX;
    }

    public void setY(int y) {
        mY = y;
    }

    public int getY() {
        return mY;
    }

    public void setWidth(int width) {
        mWidth = width;
    }

    public int getWidth() {
        return mWidth;
    }

    public void setHeight(int height) {
        mHeight = height;
    }

    public int getHeight() {
        return mHeight;
    }

    @Override
    public String toString() {
        return "GLESRect [mX=" + mX + ", mY=" + mY + ", mWidth=" + mWidth
                + ", mHeight=" + mHeight + "]";
    }
}
