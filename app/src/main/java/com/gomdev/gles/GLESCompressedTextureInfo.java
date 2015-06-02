package com.gomdev.gles;

import java.nio.ByteBuffer;

/**
 * Created by gomdev on 15. 1. 2..
 */
public final class GLESCompressedTextureInfo {
    static final String CLASS = "GLESCompressedTextureInfo";
    static final String TAG = GLESConfig.TAG + "_" + CLASS;
    static final boolean DEBUG = GLESConfig.DEBUG;

    private int mWidth;
    private int mHeight;
    private int mInternalFormat;
    private int mBlockSize;
    private ByteBuffer mData;

    public GLESCompressedTextureInfo() {
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

    public void setData(ByteBuffer data) {
        mData = data;
    }

    public ByteBuffer getData() {
        return mData;
    }

    public void setInternalFormat(int format) {
        mInternalFormat = format;
    }

    public int getInternalFormat() {
        return mInternalFormat;
    }

    public void setBlockSize(int blockSize) {
        mBlockSize = blockSize;
    }

    public int getBlockSize() {
        return mBlockSize;
    }

}
