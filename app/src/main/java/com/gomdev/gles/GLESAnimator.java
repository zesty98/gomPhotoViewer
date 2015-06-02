package com.gomdev.gles;

import android.util.Log;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;

public class GLESAnimator {
    static final String CLASS = "GLESAnimator";
    static final String TAG = GLESConfig.TAG + "_" + CLASS;
    static final boolean DEBUG = GLESConfig.DEBUG;

    private GLESAnimatorCallback mCallback;
    private Interpolator mInterpolator = null;

    private GLESVector3 mCurrent = new GLESVector3(0f, 0f, 0f);
    private GLESVector3 mDistance = new GLESVector3(0f, 0f, 0f);

    private long mDuration = 1000L;

    private GLESVector3 mFrom;
    private GLESVector3 mTo;

    private float mFromValue;
    private float mToValue;

    private boolean mIsFinished = false;
    private boolean mIsSetValue = false;
    private boolean mIsStarted = false;
    private boolean mIsRepeat = false;

    private long mStartOffset = 0L;
    private long mStartTick = 0L;

    private boolean mUseVector = true;

    public GLESAnimator(float fromValue, float toValue,
                        GLESAnimatorCallback callback) {
        mFromValue = fromValue;
        mToValue = toValue;
        mDistance.set(mToValue - mFromValue, 0.0F, 0.0F);
        mIsSetValue = true;
        mUseVector = false;
        mCallback = callback;
    }

    public GLESAnimator(GLESAnimatorCallback callback) {
        mIsSetValue = false;
        mCallback = callback;
    }

    public GLESAnimator(GLESVector3 from, GLESVector3 to,
                        GLESAnimatorCallback callback) {
        mFrom = from;
        mTo = to;
        mDistance.set(mTo.mX - mFrom.mX, mTo.mY - mFrom.mY, mTo.mZ - mFrom.mZ);
        mIsSetValue = true;
        mUseVector = true;
        mCallback = callback;
    }

    public void cancel() {
        mIsFinished = true;
        mIsStarted = false;
        if (mCallback != null)
            mCallback.onCancel();
    }

    public void destroy() {
        mInterpolator = null;
    }

    public boolean doAnimation() {
        if (mIsStarted == false) {
            return false;
        }

        if ((mIsFinished == true) && (mCallback != null)) {
            mCallback.onFinished();
            mIsStarted = false;
            return false;
        }

        long currentTick = System.currentTimeMillis();
        if (currentTick - mStartTick < mStartOffset) {
            return true;
        }

        long startTick = mStartTick + mStartOffset;
        float normalizedDuration = (float) (currentTick - startTick)
                / mDuration;

        if (normalizedDuration >= 1.0F) {
            normalizedDuration = 1.0F;

            if (mIsRepeat == true) {
                mStartTick = System.currentTimeMillis();
            } else {
                mIsFinished = true;
            }
        }

        if (mInterpolator == null) {
            mInterpolator = new LinearInterpolator();
        }

        normalizedDuration = mInterpolator.getInterpolation(normalizedDuration);

        if (mUseVector == true) {
            mCurrent.mX = mFrom.mX + mDistance.mX * normalizedDuration;
            mCurrent.mY = mFrom.mY + mDistance.mY * normalizedDuration;
            mCurrent.mZ = mFrom.mZ + mDistance.mZ * normalizedDuration;
        } else {
            mCurrent.mX = mFromValue + mDistance.mX * normalizedDuration;
            mCurrent.mY = 0f;
            mCurrent.mZ = 0f;
        }

        if (mCallback != null) {
            mCallback.onAnimation(mCurrent);
        }

        return true;
    }

    public GLESVector3 getCurrentValue() {
        if (mIsFinished == true) {
            return null;
        }

        long currentTick = System.currentTimeMillis();

        if ((currentTick - mStartTick) < mStartOffset) {
            return null;
        }

        long startTick = mStartTick + mStartOffset;

        float normalizedDuration = (float) (currentTick - startTick)
                / mDuration;

        if (normalizedDuration >= 1.0f) {
            normalizedDuration = 1.0f;
            mIsFinished = true;
        }

        if (mInterpolator == null) {
            mInterpolator = new LinearInterpolator();
        }

        normalizedDuration = mInterpolator.getInterpolation(normalizedDuration);

        if (mUseVector == true) {
            mCurrent.mX = mFrom.mX + mDistance.mX * normalizedDuration;
            mCurrent.mY = mFrom.mY + mDistance.mY * normalizedDuration;
            mCurrent.mZ = mFrom.mZ + mDistance.mZ * normalizedDuration;
        } else {
            mCurrent.mX = mFromValue + mDistance.mX * normalizedDuration;
            mCurrent.mY = 0f;
            mCurrent.mZ = 0f;
        }

        return mCurrent;
    }

    public boolean isFinished() {
        return mIsFinished;
    }

    public boolean isOnAnimation() {
        return !mIsFinished;
    }

    public void setDuration(long start, long end) {
        if (start < 0L) {
            Log.e(TAG, "setDuration() start=" + start + " is invalid");
            return;
        }

        mStartOffset = start;
        mDuration = (end - start);
    }

    public void setValues(GLESVector3 from, GLESVector3 to) {
        mUseVector = true;
        mIsSetValue = true;

        mFrom = from;
        mTo = to;

        mDistance.set(mTo.mX - mFrom.mX, mTo.mY - mFrom.mY, mTo.mZ - mFrom.mZ);
    }

    public void setValues(float from, float to) {
        mUseVector = false;
        mIsSetValue = true;

        mFromValue = from;
        mToValue = to;

        mDistance.set(mToValue - mFromValue, 0.0F, 0.0F);
    }

    public void setInterpolator(Interpolator interpolator) {
        mInterpolator = interpolator;
    }

    public void setRepeat(boolean isRepeat) {
        mIsRepeat = isRepeat;
    }

    public void start() {
        if (mIsSetValue == false) {
            throw new IllegalStateException(
                    "quilt GLESAnimatorstart() should use start(from, to)");
        }

        mIsFinished = false;
        mIsStarted = true;
        mStartTick = System.currentTimeMillis();
    }

    public void start(float fromValue, float toValue) {
        mUseVector = false;

        mFromValue = fromValue;
        mToValue = toValue;

        mDistance.set(mToValue - mFromValue, 0.0F, 0.0F);

        mIsFinished = false;
        mIsStarted = true;

        mStartTick = System.currentTimeMillis();
    }

    public void start(GLESVector3 from, GLESVector3 to) {
        mUseVector = true;

        mFrom = from;
        mTo = to;

        mDistance.set(mTo.mX - mFrom.mX, mTo.mY - mFrom.mY, mTo.mZ - mFrom.mZ);

        mIsFinished = false;
        mIsStarted = true;

        mStartTick = System.currentTimeMillis();
    }
}
