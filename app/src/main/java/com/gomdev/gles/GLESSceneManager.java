package com.gomdev.gles;

import com.gomdev.gles.GLESConfig.Version;

public final class GLESSceneManager {
    static final String CLASS = "GLESSceneManager";
    static final String TAG = GLESConfig.TAG + "_" + CLASS;
    static final boolean DEBUG = GLESConfig.DEBUG;

    private GLESNode mRootNode = null;

    private GLESSceneManager() {
    }

    public static GLESSceneManager createSceneManager() {
        return new GLESSceneManager();
    }

    public GLESNode createRootNode(String name) {
        mRootNode = new GLESNode(name);
        return mRootNode;
    }

    public GLESNode getRootNode() {
        return mRootNode;
    }

    public GLESNode createNode(String name) {
        return new GLESNode(name);
    }

    public GLESObject createObject(String name) {
        Version version = GLESContext.getInstance().getVersion();

        GLESObject object = null;
        switch (version) {
            case GLES_20:
                object = new GLESObject(name);
                break;
            case GLES_30:
                object = new GLES30Object(name);
                break;
            default:
                object = new GLESObject(name);
        }

        return object;
    }
}
