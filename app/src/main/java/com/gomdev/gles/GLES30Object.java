package com.gomdev.gles;

public class GLES30Object extends GLESObject {
    static final String CLASS = "GLES30Object";
    static final String TAG = GLESConfig.TAG + "_" + CLASS;
    static final boolean DEBUG = GLESConfig.DEBUG;

    public GLES30Object() {
        super();
    }

    public GLES30Object(String name) {
        super(name);
    }

    @Override
    public void setVertexInfo(GLESVertexInfo vertexInfo, boolean useVBO,
                              boolean useVAO) {
        super.setVertexInfo(vertexInfo, useVBO, useVAO);

        if (useVAO == true && useVBO == true) {
            mUseVAO = useVAO;
        } else if (useVAO == false) {
            mUseVAO = useVAO;
        }
    }

    @Override
    public boolean useVAO() {
        return mUseVAO;
    }

    @Override
    public boolean isVAOSetup() {
        return mIsVAOSetup;
    }

    @Override
    public void setupVAO(boolean isVAOSetup) {
        mIsVAOSetup = isVAOSetup;
    }
}
