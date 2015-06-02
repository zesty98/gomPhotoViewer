package com.gomdev.gles;

import android.opengl.Matrix;
import android.util.Log;

public final class GLESCamera {
    static final String CLASS = "GLESCamera";
    static final String TAG = GLESConfig.TAG + "_" + CLASS;
    static final boolean DEBUG = GLESConfig.DEBUG;

    private float[] mPMatrix = new float[16];
    private float[] mVMatrix = new float[16];

    private GLESRect mViewport = null;

    public GLESCamera() {
    }

    public void destroy() {
        mPMatrix = null;
        mVMatrix = null;
    }

    public void setFrustum(float fovy, float aspect, float near, float far) {
        mPMatrix = new float[16];

        Matrix.perspectiveM(mPMatrix, 0, fovy, aspect, near, far);
    }

    public void setFrustum(float left, float right, float bottom, float top,
                           float near, float far) {
        Matrix.frustumM(mPMatrix, 0, left, right, bottom, top, near, far);
    }

    public void setOrtho(float left, float right, float bottom, float top,
                         float near, float far) {
        Matrix.orthoM(mPMatrix, 0, left, right, bottom, top, near, far);
    }

    public float[] getProjectionMatrix() {
        if (mPMatrix == null) {
            Log.e(TAG, "getProjectionMatrix() mPMatrix is null.");
        }

        return mPMatrix;
    }

    public void setLookAt(float eyeX, float eyeY, float eyeZ, float centerX,
                          float centerY, float centerZ, float upX, float upY, float upZ) {
        Matrix.setLookAtM(mVMatrix, 0, eyeX, eyeY, eyeZ, centerX, centerY,
                centerZ, upX, upY, upZ);
    }

    public float[] getViewMatrix() {
        if (mVMatrix == null) {
            Log.e(TAG, "getViewMatrix() mVMatrix is null.");
        }

        return mVMatrix;
    }

    public float[] getPVMatrix() {
        if (mVMatrix != null || mPMatrix != null) {
            float[] vpMatrix = new float[16];

            Matrix.multiplyMM(vpMatrix, 0, mPMatrix, 0, mVMatrix, 0);
            return vpMatrix;
        }

        return null;
    }

    public void setViewport(GLESRect viewport) {
        mViewport = viewport;
    }

    public GLESRect getViewport() {
        return mViewport;
    }

    public void dump(String str) {
        if (!DEBUG) {
            return;
        }

        Log.d(TAG, str + " mPMatrix : ");

        for (int i = 0; i < mPMatrix.length; i += 4) {
            Log.d(TAG, "\t [ " + mPMatrix[i] + ", " + mPMatrix[i + 1] + ", "
                    + mPMatrix[i + 2] + ", " + mPMatrix[i + 3] + " ]");
        }

        Log.d(TAG, str + " mVMatrix : ");

        for (int i = 0; i < mVMatrix.length; i += 4) {
            Log.d(TAG, "\t [ " + mVMatrix[i] + ", " + mVMatrix[i + 1] + ", "
                    + mVMatrix[i + 2] + ", " + mVMatrix[i + 3] + " ]");
        }

    }
}
