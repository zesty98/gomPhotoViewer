package com.gomdev.gles;

public class GLESParticle {
    public float mX = 0f;
    public float mY = 0f;
    public float mZ = 0f;

    public float mR = 1f;
    public float mG = 1f;
    public float mB = 1f;
    public float mA = 1f;

    public float mSize = 1f;

    private GLESVector3 mVelocity = new GLESVector3(1f, 1f, 1f);

    private float mNormalizedDuration = 1f;

    public GLESParticle(float x, float y, float z) {
        mX = x;
        mY = y;
        mZ = z;
    }

    public void setVelocityX(float vel) {
        mVelocity.mX = vel;

        if (mVelocity.mX != 1.0f) {
            mNormalizedDuration = 1f / mVelocity.mX;
        }
    }

    public float getVelocityX() {
        return mVelocity.mX;
    }

    public void setVelocity(float x, float y, float z) {
        mVelocity.set(x, y, z);
    }

    public GLESVector3 getVelocity() {
        return mVelocity;
    }

    public float getNormalizedDuration() {
        return mNormalizedDuration;
    }
}
