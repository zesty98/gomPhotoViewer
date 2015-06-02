package com.gomdev.gles;

import android.graphics.Bitmap;

public final class GLESBitmapInfo {
    static final String CLASS = "GLESBitmapInfo";
    static final String TAG = GLESConfig.TAG + "_" + CLASS;
    static final boolean DEBUG = GLESConfig.DEBUG;

    int mWidth = 0;
    int mHeight = 0;

    int[] mData = null;

    public GLESBitmapInfo() {

    }

    public GLESBitmapInfo(Bitmap bitmap) {
        mWidth = bitmap.getWidth();
        mHeight = bitmap.getHeight();

        mData = new int[mWidth * mHeight];
        bitmap.getPixels(mData, 0, mWidth, 0, 0, mWidth, mHeight);
    }

    public GLESBitmapInfo(GLESBitmapInfo info) {
        mWidth = info.mWidth;
        mHeight = info.mHeight;
        mData = info.mData;
    }

    public float getWidth() {
        return mWidth;
    }

    public float getHeight() {
        return mHeight;
    }

    public int[] getData() {
        return mData;
    }

    @Override
    public String toString() {
        return "GLESBitmapInfo width=" + mWidth + " height=" + mHeight;
    }
}
