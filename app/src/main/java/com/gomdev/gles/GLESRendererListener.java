package com.gomdev.gles;

public interface GLESRendererListener {
    public void setupVBO(GLESShader shader, GLESVertexInfo vertexInfo);

    public void setupVAO(GLESShader shader, GLESVertexInfo vertexInfo);

    public void enableVertexAttribute(GLESObject object);

    public void disableVertexAttribute(GLESObject object);
}
