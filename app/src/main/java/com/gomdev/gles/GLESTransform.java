package com.gomdev.gles;

import android.opengl.Matrix;
import android.util.Log;

public final class GLESTransform {
    static final String CLASS = "GLESTransform";
    static final String TAG = GLESConfig.TAG + "_" + CLASS;
    static final boolean DEBUG = GLESConfig.DEBUG;

    private GLESSpatial mOwner = null;

    private GLESVector3 mTranslate = new GLESVector3(0f, 0f, 0f);
    private GLESVector3 mPreTranslate = new GLESVector3(0f, 0f, 0f);
    private float mScale = 1f;
    private GLESVector3 mRotate = new GLESVector3(0f, 0f, 0f);

    private float[] mMMatrix = new float[16];

    private boolean mIsPreTranslate = false;
    private boolean mIsTranslate = false;
    private boolean mIsScale = false;
    private boolean mIsRotate = false;

    private boolean mNeedToUpdate = false;

    public GLESTransform() {
        init();
    }

    public GLESTransform(GLESSpatial owner) {
        mOwner = owner;

        init();
    }

    private void init() {
        mIsTranslate = false;
        mTranslate.set(0f, 0f, 0f);

        mIsPreTranslate = false;
        mPreTranslate.set(0f, 0f, 0f);

        mIsScale = false;
        mScale = 1f;

        mIsRotate = false;
        mRotate.set(0f, 0f, 0f);

        Matrix.setIdentityM(mMMatrix, 0);
    }

    public void destroy() {
    }

    public GLESSpatial getOwner() {
        return mOwner;
    }

    public void setIdentity() {
        init();
    }

    public void setTranslate(float x, float y, float z) {
        mTranslate.set(x, y, z);
        mIsTranslate = true;
        mNeedToUpdate = true;
    }

    public void translate(float x, float y, float z) {
        mTranslate.mX += x;
        mTranslate.mY += y;
        mTranslate.mZ += z;

        mIsTranslate = true;
        mNeedToUpdate = true;
    }

    public GLESVector3 getTranslate() {
        return mTranslate;
    }

    public boolean isSetTranslate() {
        return mIsTranslate;
    }

    public void setPreTranslate(float x, float y, float z) {
        mPreTranslate.set(x, y, z);

        mIsPreTranslate = true;
        mNeedToUpdate = true;
    }

    public void preTranslate(float x, float y, float z) {
        mPreTranslate.mX += x;
        mPreTranslate.mY += y;
        mPreTranslate.mZ += z;

        mIsPreTranslate = true;
        mNeedToUpdate = true;
    }

    public GLESVector3 getPreTranslate() {
        return mPreTranslate;
    }

    public boolean isSetPreTranslate() {
        return mIsPreTranslate;
    }

    public void setRotate(float angle, float x, float y, float z) {
        if (x == 1f) {
            mRotate.mX = angle;
            mRotate.mY = 0f;
            mRotate.mZ = 0f;
        } else if (y == 1f) {
            mRotate.mY = angle;
            mRotate.mX = 0f;
            mRotate.mZ = 0f;
        } else if (z == 1f) {
            mRotate.mZ = angle;
            mRotate.mX = 0f;
            mRotate.mY = 0f;
        } else {
            Log.e(TAG, "setRotate() not support");
        }

        mIsRotate = true;
        mNeedToUpdate = true;
    }

    public void rotate(float angle, float x, float y, float z) {
        if (x == 1f) {
            mRotate.mX += angle;
        } else if (y == 1f) {
            mRotate.mY += angle;
        } else if (z == 1f) {
            mRotate.mZ += angle;
        } else {
            Log.e(TAG, "rotate() not support ");
        }

        mIsRotate = true;
        mNeedToUpdate = true;
    }

    public boolean isSetRotate() {
        return mIsRotate;
    }

    public void setScale(float scale) {
        mScale = scale;

        mIsScale = true;
        mNeedToUpdate = true;
    }

    public void scale(float scale) {
        mScale *= scale;
        mIsScale = true;
        mNeedToUpdate = true;
    }

    public boolean isSetScale() {
        return mIsScale;
    }

    public float[] getMatrix() {
        if (mNeedToUpdate == false) {
            return mMMatrix;
        }

        Matrix.setIdentityM(mMMatrix, 0);

        if (mIsTranslate == true) {
            Matrix.translateM(mMMatrix, 0, mTranslate.mX, mTranslate.mY,
                    mTranslate.mZ);
        }

        if (mIsScale == true) {
            Matrix.scaleM(mMMatrix, 0, mScale, mScale, mScale);
        }

        if (mIsRotate == true) {
            if (mRotate.mX != 0f) {
                Matrix.rotateM(mMMatrix, 0, mRotate.mX, 1f, 0f, 0f);
            }

            if (mRotate.mY != 0f) {
                Matrix.rotateM(mMMatrix, 0, mRotate.mY, 0f, 1f, 0f);
            }

            if (mRotate.mZ != 0f) {
                Matrix.rotateM(mMMatrix, 0, mRotate.mZ, 0f, 0f, 1f);
            }
        }

        if (mIsPreTranslate == true) {
            Matrix.translateM(mMMatrix, 0, mPreTranslate.mX,
                    mPreTranslate.mY,
                    mPreTranslate.mZ);
        }

        mNeedToUpdate = false;

        return mMMatrix;
    }

    public void setMatrix(float[] matrix) {
        System.arraycopy(matrix, 0, mMMatrix, 0, mMMatrix.length);
    }

    public void dump(String str) {
        if (!DEBUG) {
            return;
        }

        Log.d(TAG, "dump()");
        Log.d(TAG, "\t Translate " + mTranslate);
        Log.d(TAG, "\t Scale " + mScale);
        Log.d(TAG, "\t Rotate " + mRotate);
    }
}