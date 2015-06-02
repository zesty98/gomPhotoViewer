package com.gomdev.gles;

import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.util.Log;

public class GLESTextureCubemap extends GLESTexture {
    static final String CLASS = "GLESTexture";
    static final String TAG = GLESConfig.TAG + "_" + CLASS;
    static final boolean DEBUG = GLESConfig.DEBUG;

    private Bitmap[] mBitmaps = null;

    protected GLESTextureCubemap(int width, int height) {
        super();

        mWidth = width;
        mHeight = height;

        init();
    }

    protected GLESTextureCubemap(int width, int height, Bitmap[] bitmaps) {
        super();

        mWidth = width;
        mHeight = height;

        mBitmaps = bitmaps;

        init();
    }

    private void init() {
        mTarget = GLES20.GL_TEXTURE_CUBE_MAP;
        mWrapMode = GLES20.GL_REPEAT;
    }

    @Override
    public void destroy() {
        if (GLES20.glIsTexture(mTextureID) == true) {
            int[] textureIDs = new int[1];
            textureIDs[0] = mTextureID;
            GLES20.glBindTexture(GLES20.GL_TEXTURE_CUBE_MAP, 0);
            GLES20.glDeleteTextures(1, textureIDs, 0);
        }
    }

    @Override
    protected void makeTexture() {
        if (mBitmaps == null) {
            Log.e(TAG, "makeTexture() bitmaps is null");
            return;
        }

        int[] textureIDs = new int[1];
        GLES20.glGenTextures(1, textureIDs, 0);
        mTextureID = textureIDs[0];

        GLES20.glBindTexture(mTarget, mTextureID);

        GLUtils.texImage2D(GLES20.GL_TEXTURE_CUBE_MAP_POSITIVE_X, 0,
                mBitmaps[0], 0);
        GLUtils.texImage2D(GLES20.GL_TEXTURE_CUBE_MAP_NEGATIVE_X, 0,
                mBitmaps[1], 0);
        GLUtils.texImage2D(GLES20.GL_TEXTURE_CUBE_MAP_POSITIVE_Y, 0,
                mBitmaps[2], 0);
        GLUtils.texImage2D(GLES20.GL_TEXTURE_CUBE_MAP_NEGATIVE_Y, 0,
                mBitmaps[3], 0);
        GLUtils.texImage2D(GLES20.GL_TEXTURE_CUBE_MAP_POSITIVE_Z, 0,
                mBitmaps[4], 0);
        GLUtils.texImage2D(GLES20.GL_TEXTURE_CUBE_MAP_NEGATIVE_Z, 0,
                mBitmaps[5], 0);

        GLES20.glTexParameteri(mTarget,
                GLES20.GL_TEXTURE_WRAP_S,
                mWrapMode);
        GLES20.glTexParameteri(mTarget,
                GLES20.GL_TEXTURE_WRAP_T,
                mWrapMode);
        GLES20.glTexParameterf(mTarget,
                GLES20.GL_TEXTURE_MAG_FILTER, mMagFilter);
        GLES20.glTexParameterf(mTarget,
                GLES20.GL_TEXTURE_MIN_FILTER, mMinFilter);

        GLES20.glBindTexture(mTarget, 0);
    }

    @Override
    public void makeSubTexture(int width, int height, Bitmap bitmap) {
        Log.e(TAG, "should use makeSubTexture(..., Bitmap[] bitmap)");
    }

    @Override
    public void makeSubTexture(int width, int height, Bitmap[] bitmaps) {
        if (bitmaps == null) {
            Log.e(TAG, "makeSubTexture() bitmap is null");
            return;
        }

        GLES20.glBindTexture(mTarget, mTextureID);

        GLUtils.texSubImage2D(GLES20.GL_TEXTURE_CUBE_MAP_POSITIVE_X, 0, 0, 0,
                bitmaps[0]);
        GLUtils.texSubImage2D(GLES20.GL_TEXTURE_CUBE_MAP_NEGATIVE_X, 0, 0, 0,
                bitmaps[1]);
        GLUtils.texSubImage2D(GLES20.GL_TEXTURE_CUBE_MAP_POSITIVE_Y, 0, 0, 0,
                bitmaps[2]);
        GLUtils.texSubImage2D(GLES20.GL_TEXTURE_CUBE_MAP_NEGATIVE_Y, 0, 0, 0,
                bitmaps[3]);
        GLUtils.texSubImage2D(GLES20.GL_TEXTURE_CUBE_MAP_POSITIVE_Z, 0, 0, 0,
                bitmaps[4]);
        GLUtils.texSubImage2D(GLES20.GL_TEXTURE_CUBE_MAP_NEGATIVE_Z, 0, 0, 0,
                bitmaps[5]);

        GLES20.glBindTexture(mTarget, 0);
    }

    @Override
    public void changeTexture(Bitmap bitmap) {
        Log.e(TAG, "should use changeTexture(..., Bitmap[] bitmap)");
    }

    @Override
    public void changeTexture(Bitmap[] bitmaps) {
        if (bitmaps == null) {
            Log.e(TAG, "changeTexture() bitmap is null");
        }

        mBitmaps = bitmaps;

        if (GLES20.glIsTexture(mTextureID) == false) {
            makeTexture();
            return;
        }

        Bitmap bitmap = bitmaps[0];

        float bitmapWidth = bitmap.getWidth();
        float bitmapHeight = bitmap.getHeight();
        if ((Float.compare(bitmapWidth, mWidth) == 0)
                && (Float.compare(bitmapHeight, mHeight) == 0)) {
            GLES20.glBindTexture(mTarget, mTextureID);

            GLUtils.texSubImage2D(GLES20.GL_TEXTURE_CUBE_MAP_POSITIVE_X, 0, 0,
                    0, bitmaps[0]);
            GLUtils.texSubImage2D(GLES20.GL_TEXTURE_CUBE_MAP_NEGATIVE_X, 0, 0,
                    0, bitmaps[1]);
            GLUtils.texSubImage2D(GLES20.GL_TEXTURE_CUBE_MAP_POSITIVE_Y, 0, 0,
                    0, bitmaps[2]);
            GLUtils.texSubImage2D(GLES20.GL_TEXTURE_CUBE_MAP_NEGATIVE_Y, 0, 0,
                    0, bitmaps[3]);
            GLUtils.texSubImage2D(GLES20.GL_TEXTURE_CUBE_MAP_POSITIVE_Z, 0, 0,
                    0, bitmaps[4]);
            GLUtils.texSubImage2D(GLES20.GL_TEXTURE_CUBE_MAP_NEGATIVE_Z, 0, 0,
                    0, bitmaps[5]);
        } else {
            int[] textureIDs = new int[1];
            textureIDs[0] = mTextureID;

            GLES20.glBindTexture(mTarget, 0);
            GLES20.glDeleteTextures(1, textureIDs, 0);

            makeTexture();
        }

        GLES20.glBindTexture(mTarget, 0);

        return;
    }

}