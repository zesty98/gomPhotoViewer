package com.gomdev.gles;

import android.opengl.Matrix;
import android.util.Log;

public abstract class GLESSpatial {
    static final String CLASS = "GLESSpatial";
    static final String TAG = GLESConfig.TAG + "_" + CLASS;
    static final boolean DEBUG = GLESConfig.DEBUG;

    protected boolean mIsVisible = true;

    private GLESSpatial mParent = null;

    private GLESTransform mWorldTransform = null;
    private GLESTransform mLocalTransform = null;

    private float[] mTempMatrix = new float[16];

    private boolean mNeedToUpdate = true;

    private String mName = null;

    public GLESSpatial() {
        init();
    }

    public GLESSpatial(String name) {
        mName = name;

        init();
    }

    private void init() {
        mWorldTransform = new GLESTransform(this);
        mLocalTransform = new GLESTransform(this);
    }

    public String getName() {
        return mName;
    }

    abstract public void update(double applicationTime, boolean parentHasChanged);

    abstract public void draw(GLESRenderer renderer);


    final public void updateWorldData(double applicationTime) {
        if (mNeedToUpdate == false) {
            return;
        }

        if (mParent != null) {
            GLESTransform parentWT = mParent.getWorldTransform();
            Matrix.multiplyMM(mTempMatrix, 0, parentWT.getMatrix(), 0,
                    mLocalTransform.getMatrix(), 0);
            mWorldTransform.setMatrix(mTempMatrix);
        } else {
            mWorldTransform.setMatrix(mLocalTransform.getMatrix());
        }

        mNeedToUpdate = false;
    }

    public GLESTransform getTransform() {
        return mLocalTransform;
    }

    public GLESTransform getWorldTransform() {
        return mWorldTransform;
    }

    public void setParent(GLESSpatial parent) {
        mParent = parent;
    }

    public GLESSpatial getParent() {
        return mParent;
    }

    public void needToUpdate() {
        mNeedToUpdate = true;
    }

    protected boolean getNeedToUpdate() {
        return mNeedToUpdate;
    }

    public void show() {
        mIsVisible = true;
    }

    public void hide() {
        mIsVisible = false;
    }

    public boolean getVisibility() {
        return mIsVisible;
    }

    public void dump() {
        StringBuilder str = new StringBuilder();

        str.append(CLASS);
        str.append("(");
        str.append(mName);
        str.append(")");

        Log.d(TAG, str.toString());
    }
}
