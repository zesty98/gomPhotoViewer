package com.gomdev.gles;

import android.graphics.Bitmap;
import android.opengl.GLES20;

import java.nio.ByteBuffer;

/**
 * Created by gomdev on 15. 1. 3..
 */
public final class GLESCompressedTexture2D extends GLESTexture2D {
    static final String CLASS = "GLESTexture";
    static final String TAG = GLESConfig.TAG + "_" + CLASS;
    static final boolean DEBUG = GLESConfig.DEBUG;

    private int mImageSize = 0;
    private ByteBuffer mData = null;

    protected GLESCompressedTexture2D(int width, int height, ByteBuffer data) {
        super(width, height);

        mData = data;
        mImageSize = data.remaining();
    }

    @Override
    protected void makeTexture() {
        int[] textureIDs = new int[1];
        GLES20.glGenTextures(1, textureIDs, 0);
        mTextureID = textureIDs[0];

        GLES20.glBindTexture(mTarget, mTextureID);

        if (mData != null) {
            GLES20.glCompressedTexImage2D(mTarget, 0, mInternalFormat, mWidth, mHeight, 0, mImageSize, mData);
        } else {
            throw new IllegalStateException("mData is null!!!");
        }
        GLES20.glTexParameteri(mTarget, GLES20.GL_TEXTURE_WRAP_S,
                mWrapMode);
        GLES20.glTexParameteri(mTarget, GLES20.GL_TEXTURE_WRAP_T,
                mWrapMode);
        GLES20.glTexParameterf(mTarget,
                GLES20.GL_TEXTURE_MAG_FILTER, mMagFilter);
        GLES20.glTexParameterf(mTarget,
                GLES20.GL_TEXTURE_MIN_FILTER, mMinFilter);

        GLES20.glBindTexture(mTarget, 0);
    }

    @Override
    public void makeSubTexture(int offsetX, int offsetY, Bitmap bitmap) {
        String msg = "glCompressedTexSubImage2D() is not supported";
        throw new IllegalStateException(msg);
    }

    @Override
    public void makeSubTexture(int width, int height, Bitmap[] bitmaps) {
        String msg = "glCompressedTexSubImage2D() is not supported";
        throw new IllegalStateException(msg);
    }

    @Override
    public void changeTexture(Bitmap bitmap) {
        String msg = "changeTexture() is not supported";
        throw new IllegalStateException(msg);
    }

    @Override
    public void changeTexture(Bitmap[] bitmaps) {
        String msg = "changeTexture() is not supported";
        throw new IllegalStateException(msg);
    }
}
