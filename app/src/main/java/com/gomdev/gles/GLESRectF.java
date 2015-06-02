package com.gomdev.gles;

public class GLESRectF {
    static final String CLASS = "GLESRectF";
    static final String TAG = GLESConfig.TAG + "_" + CLASS;
    static final boolean DEBUG = GLESConfig.DEBUG;

    float mX = 0;
    float mY = 0;
    float mWidth = 0;
    float mHeight = 0;

    public GLESRectF() {

    }

    public GLESRectF(float x, float y, float width, float height) {
        mX = x;
        mY = y;
        mWidth = width;
        mHeight = height;
    }

    public void set(float x, float y, float width, float height) {
        mX = x;
        mY = y;
        mWidth = width;
        mHeight = height;
    }

    public void setX(float x) {
        mX = x;
    }

    public float getX() {
        return mX;
    }

    public void setY(float y) {
        mY = y;
    }

    public float getY() {
        return mY;
    }

    public void setWidth(float width) {
        mWidth = width;
    }

    public float getWidth() {
        return mWidth;
    }

    public void setHeight(float height) {
        mHeight = height;
    }

    public float getHeight() {
        return mHeight;
    }

    @Override
    public String toString() {
        return "GLESRectF [mX=" + mX + ", mY=" + mY + ", mWidth=" + mWidth
                + ", mHeight=" + mHeight + "]";
    }
}
