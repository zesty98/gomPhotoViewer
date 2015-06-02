package com.gomdev.gles;

import android.content.Context;

import com.gomdev.gles.GLESConfig.Version;

public final class GLESContext {
    static final String CLASS = "GLESContext";
    static final String TAG = GLESConfig.TAG + "_" + CLASS;
    static final boolean DEBUG = GLESConfig.DEBUG;

    private static GLESContext sContext = new GLESContext();

    private GLESRenderer mRenderer = null;
    private Context mContext = null;
    private Version mGLESVersion = GLESConfig.DEFAULT_GLES_VERSION;
    private String mShaderErrorLog = null;

    public static GLESContext getInstance() {
        return sContext;
    }

    private GLESContext() {

    }

    public void setRenderer(GLESRenderer renderer) {
        mRenderer = renderer;
    }

    public GLESRenderer getRenderer() {
        return mRenderer;
    }

    public void setContext(Context context) {
        mContext = context;
    }

    public Context getContext() {
        return mContext;
    }

    public void setVersion(Version version) {
        mGLESVersion = version;
    }

    public Version getVersion() {
        return mGLESVersion;
    }

    public void setShaderErrorLog(String log) {
        mShaderErrorLog = log;
    }

    public String getShaderErrorLog() {
        return mShaderErrorLog;
    }
}
