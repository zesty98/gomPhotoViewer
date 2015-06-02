package com.gomdev.gles;

import android.opengl.GLES20;
import android.util.Log;

import com.gomdev.gles.GLESVertexInfo.PrimitiveMode;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

public class GLES20Renderer extends GLESRenderer {
    static final String CLASS = "GLES20Renderer";
    static final String TAG = GLESConfig.TAG + "_" + CLASS;
    static final boolean DEBUG = GLESConfig.DEBUG;

    public GLES20Renderer() {

    }

    @Override
    public void setupVBO(GLESShader shader, GLESVertexInfo vertexInfo) {
        if (DEBUG) {
            Log.d(TAG, "setupVBO()");
        }
        int[] ids = new int[1];
        GLES20.glGenBuffers(1, ids, 0);

        int attribIndex = shader.getPositionAttribIndex();
        vertexInfo.setVBOID(attribIndex, ids[0]);

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, ids[0]);
        FloatBuffer floatBuffer = (FloatBuffer) vertexInfo.getBuffer(attribIndex);
        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER,
                floatBuffer.capacity() * GLESConfig.FLOAT_SIZE_BYTES,
                floatBuffer,
                GLES20.GL_STATIC_DRAW);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);

        attribIndex = shader.getTexCoordAttribIndex();
        if (vertexInfo.useAttrib(attribIndex) == true) {
            GLES20.glGenBuffers(1, ids, 0);
            vertexInfo.setVBOID(attribIndex, ids[0]);
            GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, ids[0]);
            floatBuffer = (FloatBuffer) vertexInfo.getBuffer(attribIndex);
            GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER,
                    floatBuffer.capacity() * GLESConfig.FLOAT_SIZE_BYTES,
                    floatBuffer,
                    GLES20.GL_STATIC_DRAW);
            GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
        }

        attribIndex = shader.getNormalAttribIndex();
        if (vertexInfo.useAttrib(attribIndex) == true) {
            GLES20.glGenBuffers(1, ids, 0);
            vertexInfo.setVBOID(attribIndex, ids[0]);
            GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, ids[0]);
            floatBuffer = (FloatBuffer) vertexInfo.getBuffer(attribIndex);
            GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER,
                    floatBuffer.capacity() * GLESConfig.FLOAT_SIZE_BYTES,
                    floatBuffer,
                    GLES20.GL_STATIC_DRAW);
            GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
        }

        attribIndex = shader.getColorAttribIndex();
        if (vertexInfo.useAttrib(attribIndex) == true) {
            GLES20.glGenBuffers(1, ids, 0);
            vertexInfo.setVBOID(attribIndex, ids[0]);
            GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, ids[0]);
            floatBuffer = (FloatBuffer) vertexInfo.getBuffer(attribIndex);
            GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER,
                    floatBuffer.capacity() * GLESConfig.FLOAT_SIZE_BYTES,
                    floatBuffer,
                    GLES20.GL_STATIC_DRAW);
            GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
        }

        if (vertexInfo.useIndex() == true) {
            GLES20.glGenBuffers(1, ids, 0);
            vertexInfo.setIndexVBOID(ids[0]);
            GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, ids[0]);
            ShortBuffer shortBuffer = vertexInfo.getIndexBuffer();
            GLES20.glBufferData(GLES20.GL_ELEMENT_ARRAY_BUFFER,
                    shortBuffer.capacity() * GLESConfig.SHORT_SIZE_BYTES,
                    shortBuffer,
                    GLES20.GL_STATIC_DRAW);
            GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);
        }

        if (mListener != null) {
            mListener.setupVBO(shader, vertexInfo);
        }
    }

    @Override
    protected void applyCamera(GLESObject object) {
        if (DEBUG) {
            Log.d(TAG, "applyCamera() object=" + object.getName());
        }

        GLESShader shader = object.getShader();
        GLESCamera camera = object.getCamera();

        if (shader == mCurrentShader && camera == mCurrentCamera) {
            return;
        }

        GLESRect viewport = camera.getViewport();
        GLES20.glViewport(viewport.mX, viewport.mY,
                viewport.mWidth, viewport.mHeight);

        String uniformName = GLESShaderConstant.UNIFORM_PROJ_MATRIX;
        int handle = shader.getUniformLocation(uniformName);
        GLES20.glUniformMatrix4fv(handle, 1, false,
                camera.getProjectionMatrix(), 0);

        uniformName = GLESShaderConstant.UNIFORM_VIEW_MATRIX;
        handle = shader.getUniformLocation(uniformName);
        GLES20.glUniformMatrix4fv(handle, 1, false, camera.getViewMatrix(),
                0);

        mCurrentShader = shader;
        mCurrentCamera = camera;
    }

    @Override
    protected void applyTransform(GLESObject object) {
        GLESShader shader = object.getShader();
        GLESTransform transform = object.getWorldTransform();

        float[] matrix = transform.getMatrix();
        String uniformName = GLESShaderConstant.UNIFORM_MODEL_MATRIX;
        GLES20.glUniformMatrix4fv(shader.getUniformLocation(uniformName),
                1, false, matrix, 0);
    }

    @Override
    protected void setGLState(GLESObject object) {
        GLESGLState glState = object.getGLState();

        if (mCurrentGLState == null ||
                glState.getBlendState() != mCurrentGLState.getBlendState()) {
            if (glState.getBlendState() == true) {
                GLES20.glEnable(GLES20.GL_BLEND);

                GLESGLState.BlendFunc blendFunc = glState.getBlendFunc();
                GLES20.glBlendFuncSeparate(blendFunc.mSrcColor,
                        blendFunc.mDstColor,
                        blendFunc.mSrcAlpha,
                        blendFunc.mDstAlpha);
            } else {
                GLES20.glDisable(GLES20.GL_BLEND);
            }
        }

        if (mCurrentGLState == null
                || glState.getDepthState() != mCurrentGLState.getDepthState()
                || glState.getDepthFunc() != mCurrentGLState.getDepthFunc()) {
            if (glState.getDepthState() == true) {
                GLES20.glEnable(GLES20.GL_DEPTH_TEST);

                GLES20.glDepthFunc(glState.getDepthFunc());
            } else {
                GLES20.glDisable(GLES20.GL_DEPTH_TEST);
            }
        }

        if (mCurrentGLState == null
                || glState.getCullFaceState() != mCurrentGLState
                .getCullFaceState()) {
            if (glState.getCullFaceState() == true) {
                GLES20.glEnable(GLES20.GL_CULL_FACE);

                GLES20.glCullFace(glState.getCullFace());
            } else {
                GLES20.glDisable(GLES20.GL_CULL_FACE);
            }
        }

        if (mCurrentGLState == null) {
            mCurrentGLState = new GLESGLState();
        }

        mCurrentGLState.set(glState);
    }

    @Override
    protected void bindTexture(GLESObject object) {
        GLESTexture texture = object.getTexture();

        if (texture != null) {
            GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
            int textureID = texture.getTextureID();
            GLES20.glBindTexture(texture.getTarget(), textureID);
            if (GLES20.glIsTexture(textureID) == false) {
                Log.e(TAG, "bindTexture() object=" + object.getName() + " textureID is invalid");
            }
        }
    }

    @Override
    protected void enableVertexAttribute(GLESObject object) {
        GLESVertexInfo vertexInfo = object.getVertexInfo();
        GLESShader shader = object.getShader();
        boolean useVBO = object.useVBO();

        int attribIndex = shader.getPositionAttribIndex();

        if (useVBO == true) {
            int id = vertexInfo.getVBOID(attribIndex);
            GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, id);

            int numOfElements = vertexInfo.getNumOfElements(attribIndex);
            GLES20.glVertexAttribPointer(shader.getPositionAttribIndex(),
                    numOfElements, GLES20.GL_FLOAT, false,
                    numOfElements * GLESConfig.FLOAT_SIZE_BYTES,
                    0);
            GLES20.glEnableVertexAttribArray(shader.getPositionAttribIndex());

            attribIndex = shader.getNormalAttribIndex();
            if (vertexInfo.useAttrib(attribIndex) == true) {
                id = vertexInfo.getVBOID(attribIndex);
                GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, id);

                numOfElements = vertexInfo.getNumOfElements(attribIndex);
                GLES20.glVertexAttribPointer(shader.getNormalAttribIndex(),
                        numOfElements, GLES20.GL_FLOAT, false,
                        numOfElements * GLESConfig.FLOAT_SIZE_BYTES,
                        0);
                GLES20.glEnableVertexAttribArray(shader.getNormalAttribIndex());
            }

            attribIndex = shader.getTexCoordAttribIndex();
            if (vertexInfo.useAttrib(attribIndex) == true) {
                id = vertexInfo.getVBOID(attribIndex);
                GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, id);

                numOfElements = vertexInfo.getNumOfElements(attribIndex);
                GLES20.glVertexAttribPointer(shader.getTexCoordAttribIndex(),
                        numOfElements, GLES20.GL_FLOAT, false,
                        numOfElements * GLESConfig.FLOAT_SIZE_BYTES,
                        0);
                GLES20.glEnableVertexAttribArray(shader
                        .getTexCoordAttribIndex());
            }

            attribIndex = shader.getColorAttribIndex();
            if (vertexInfo.useAttrib(attribIndex) == true) {
                id = vertexInfo.getVBOID(attribIndex);
                GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, id);

                numOfElements = vertexInfo.getNumOfElements(attribIndex);
                GLES20.glVertexAttribPointer(shader.getColorAttribIndex(),
                        numOfElements, GLES20.GL_FLOAT, false,
                        numOfElements * GLESConfig.FLOAT_SIZE_BYTES,
                        0);
                GLES20.glEnableVertexAttribArray(shader.getColorAttribIndex());
            }

            GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
        } else {
            int numOfElements = vertexInfo.getNumOfElements(attribIndex);
            GLES20.glVertexAttribPointer(shader.getPositionAttribIndex(),
                    numOfElements, GLES20.GL_FLOAT, false,
                    numOfElements * GLESConfig.FLOAT_SIZE_BYTES,
                    vertexInfo.getBuffer(attribIndex));
            GLES20.glEnableVertexAttribArray(shader.getPositionAttribIndex());

            attribIndex = shader.getNormalAttribIndex();
            if (vertexInfo.useAttrib(attribIndex) == true) {
                numOfElements = vertexInfo.getNumOfElements(attribIndex);
                GLES20.glVertexAttribPointer(shader.getNormalAttribIndex(),
                        numOfElements, GLES20.GL_FLOAT, false,
                        numOfElements * GLESConfig.FLOAT_SIZE_BYTES,
                        vertexInfo.getBuffer(attribIndex));
                GLES20.glEnableVertexAttribArray(shader.getNormalAttribIndex());
            }

            attribIndex = shader.getTexCoordAttribIndex();
            if (vertexInfo.useAttrib(attribIndex) == true) {
                numOfElements = vertexInfo.getNumOfElements(attribIndex);
                GLES20.glVertexAttribPointer(shader.getTexCoordAttribIndex(),
                        numOfElements, GLES20.GL_FLOAT, false,
                        numOfElements * GLESConfig.FLOAT_SIZE_BYTES,
                        vertexInfo.getBuffer(attribIndex));
                GLES20.glEnableVertexAttribArray(shader
                        .getTexCoordAttribIndex());
            }

            attribIndex = shader.getColorAttribIndex();
            if (vertexInfo.useAttrib(attribIndex) == true) {
                numOfElements = vertexInfo.getNumOfElements(attribIndex);
                GLES20.glVertexAttribPointer(shader.getColorAttribIndex(),
                        numOfElements, GLES20.GL_FLOAT, false,
                        numOfElements * GLESConfig.FLOAT_SIZE_BYTES,
                        vertexInfo.getBuffer(attribIndex));
                GLES20.glEnableVertexAttribArray(shader.getColorAttribIndex());
            }
        }

        if (mListener != null) {
            mListener.enableVertexAttribute(object);
        }
    }

    @Override
    protected void drawArrays(GLESObject object) {
        GLESVertexInfo vertexInfo = object.getVertexInfo();
        PrimitiveMode mode = vertexInfo.getPrimitiveMode();
        GLESShader shader = object.getShader();

        int positionIndex = shader.getPositionAttribIndex();

        int numOfVertex = 0;
        int numOfElements = vertexInfo.getNumOfElements(positionIndex);
        if (numOfElements == 0) {
            numOfVertex = vertexInfo.getNumOfVertex();
        } else {
            numOfVertex = vertexInfo.getBuffer(positionIndex).capacity()
                    / vertexInfo.getNumOfElements(positionIndex);
        }

        if (numOfVertex == 0) {
            throw new IllegalArgumentException(object.getName() + " numOfVertex is 0");
        }

        switch (mode) {
            case TRIANGLES:
                GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, numOfVertex);
                break;
            case TRIANGLE_FAN:
                GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, numOfVertex);
                break;
            case TRIANGLE_STRIP:
                GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, numOfVertex);
                break;
            case LINES:
                GLES20.glDrawArrays(GLES20.GL_LINES, 0, numOfVertex);
                break;
            case LINE_STRIP:
                GLES20.glDrawArrays(GLES20.GL_LINE_STRIP, 0, numOfVertex);
                break;
            case LINE_LOOP:
                GLES20.glDrawArrays(GLES20.GL_LINE_LOOP, 0, numOfVertex);
                break;
            case POINTS:
                GLES20.glDrawArrays(GLES20.GL_POINTS, 0, numOfVertex);
                break;
            default:
                Log.d(TAG, "drawArrays() mode is invalid. mode=" + mode);
                break;
        }
    }

    @Override
    protected void drawElements(GLESObject object) {
        GLESVertexInfo vertexInfo = object.getVertexInfo();
        PrimitiveMode mode = vertexInfo.getPrimitiveMode();

        if (object.useVBO() == true) {
            int id = vertexInfo.getIndexVBOID();
            GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, id);

            ShortBuffer indexBuffer = vertexInfo.getIndexBuffer();
            switch (mode) {
                case TRIANGLES:
                    GLES20.glDrawElements(GLES20.GL_TRIANGLES,
                            indexBuffer.capacity(),
                            GLES20.GL_UNSIGNED_SHORT, 0);
                    break;
                case TRIANGLE_FAN:
                    GLES20.glDrawElements(GLES20.GL_TRIANGLE_FAN,
                            indexBuffer.capacity(),
                            GLES20.GL_UNSIGNED_SHORT, 0);
                    break;
                case TRIANGLE_STRIP:
                    GLES20.glDrawElements(GLES20.GL_TRIANGLE_STRIP,
                            indexBuffer.capacity(),
                            GLES20.GL_UNSIGNED_SHORT, 0);
                    break;
                case LINES:
                    GLES20.glDrawElements(GLES20.GL_LINES,
                            indexBuffer.capacity(),
                            GLES20.GL_UNSIGNED_SHORT, 0);
                    break;
                case LINE_STRIP:
                    GLES20.glDrawElements(GLES20.GL_LINE_STRIP,
                            indexBuffer.capacity(),
                            GLES20.GL_UNSIGNED_SHORT, 0);
                    break;
                case LINE_LOOP:
                    GLES20.glDrawElements(GLES20.GL_LINE_LOOP,
                            indexBuffer.capacity(),
                            GLES20.GL_UNSIGNED_SHORT, 0);
                    break;
                case POINTS:
                    GLES20.glDrawElements(GLES20.GL_POINTS,
                            indexBuffer.capacity(),
                            GLES20.GL_UNSIGNED_SHORT, 0);
                    break;

                default:
                    Log.d(TAG, "drawElements() mode is invalid. mode=" + mode);
                    break;
            }

            GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);
        } else {
            ShortBuffer indexBuffer = vertexInfo.getIndexBuffer();
            switch (mode) {
                case TRIANGLES:
                    GLES20.glDrawElements(GLES20.GL_TRIANGLES,
                            indexBuffer.capacity(),
                            GLES20.GL_UNSIGNED_SHORT, indexBuffer);
                    break;
                case TRIANGLE_FAN:
                    GLES20.glDrawElements(GLES20.GL_TRIANGLE_FAN,
                            indexBuffer.capacity(),
                            GLES20.GL_UNSIGNED_SHORT, indexBuffer);
                    break;
                case TRIANGLE_STRIP:
                    GLES20.glDrawElements(GLES20.GL_TRIANGLE_FAN,
                            indexBuffer.capacity(),
                            GLES20.GL_UNSIGNED_SHORT, indexBuffer);
                    break;
                case LINES:
                    GLES20.glDrawElements(GLES20.GL_LINES,
                            indexBuffer.capacity(),
                            GLES20.GL_UNSIGNED_SHORT, indexBuffer);
                    break;
                case LINE_STRIP:
                    GLES20.glDrawElements(GLES20.GL_LINE_STRIP,
                            indexBuffer.capacity(),
                            GLES20.GL_UNSIGNED_SHORT, indexBuffer);
                    break;
                case LINE_LOOP:
                    GLES20.glDrawElements(GLES20.GL_LINE_LOOP,
                            indexBuffer.capacity(),
                            GLES20.GL_UNSIGNED_SHORT, indexBuffer);
                    break;
                case POINTS:
                    GLES20.glDrawElements(GLES20.GL_POINTS,
                            indexBuffer.capacity(),
                            GLES20.GL_UNSIGNED_SHORT, indexBuffer);
                    break;
                default:
                    Log.d(TAG, "drawElements() mode is invalid. mode=" + mode);
                    break;
            }
        }
    }

    @Override
    protected void drawArraysInstanced(GLESObject object) {
        throw new IllegalStateException(
                "this feature is available in OpenGL ES 3.0");
    }

    @Override
    protected void drawElementsInstanced(GLESObject object) {
        throw new IllegalStateException(
                "this feature is available in OpenGL ES 3.0");
    }

    @Override
    protected void disableVertexAttribute(GLESObject object) {
        GLESVertexInfo vertexInfo = object.getVertexInfo();
        GLESShader shader = object.getShader();

        GLES20.glDisableVertexAttribArray(shader.getPositionAttribIndex());

        int attribIndex = shader.getNormalAttribIndex();
        if (vertexInfo.useAttrib(attribIndex) == true) {
            GLES20.glDisableVertexAttribArray(shader.getNormalAttribIndex());
        }

        attribIndex = shader.getTexCoordAttribIndex();
        if (vertexInfo.useAttrib(attribIndex) == true) {
            GLES20.glDisableVertexAttribArray(shader.getTexCoordAttribIndex());
        }

        attribIndex = shader.getColorAttribIndex();
        if (vertexInfo.useAttrib(attribIndex) == true) {
            GLES20.glDisableVertexAttribArray(shader.getColorAttribIndex());
        }

        if (mListener != null) {
            mListener.disableVertexAttribute(object);
        }
    }
}
