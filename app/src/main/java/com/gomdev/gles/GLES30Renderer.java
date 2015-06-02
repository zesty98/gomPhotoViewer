package com.gomdev.gles;

import android.annotation.TargetApi;
import android.opengl.GLES30;
import android.os.Build;
import android.util.Log;

import com.gomdev.gles.GLESVertexInfo.PrimitiveMode;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
public final class GLES30Renderer extends GLES20Renderer {
    static final String CLASS = "GLES30Renderer";
    static final String TAG = GLESConfig.TAG + "_" + CLASS;
    static final boolean DEBUG = GLESConfig.DEBUG;

    public GLES30Renderer() {

    }

    @Override
    public void setupVBO(GLESShader shader, GLESVertexInfo vertexInfo) {
        int[] ids = new int[1];
        GLES30.glGenBuffers(1, ids, 0);

        int attribIndex = shader.getPositionAttribIndex();
        vertexInfo.setVBOID(attribIndex, ids[0]);

        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, ids[0]);
        FloatBuffer floatBuffer = (FloatBuffer) vertexInfo
                .getBuffer(attribIndex);
        GLES30.glBufferData(GLES30.GL_ARRAY_BUFFER,
                floatBuffer.capacity() * GLESConfig.FLOAT_SIZE_BYTES,
                floatBuffer,
                GLES30.GL_STATIC_DRAW);
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, 0);

        attribIndex = shader.getTexCoordAttribIndex();
        if (vertexInfo.useAttrib(attribIndex) == true) {
            GLES30.glGenBuffers(1, ids, 0);
            vertexInfo.setVBOID(attribIndex, ids[0]);
            GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, ids[0]);
            floatBuffer = (FloatBuffer) vertexInfo.getBuffer(attribIndex);
            GLES30.glBufferData(GLES30.GL_ARRAY_BUFFER,
                    floatBuffer.capacity() * GLESConfig.FLOAT_SIZE_BYTES,
                    floatBuffer,
                    GLES30.GL_STATIC_DRAW);
            GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, 0);
        }

        attribIndex = shader.getNormalAttribIndex();
        if (vertexInfo.useAttrib(attribIndex) == true) {
            GLES30.glGenBuffers(1, ids, 0);
            vertexInfo.setVBOID(attribIndex, ids[0]);
            GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, ids[0]);
            floatBuffer = (FloatBuffer) vertexInfo.getBuffer(attribIndex);
            GLES30.glBufferData(GLES30.GL_ARRAY_BUFFER,
                    floatBuffer.capacity() * GLESConfig.FLOAT_SIZE_BYTES,
                    floatBuffer,
                    GLES30.GL_STATIC_DRAW);
            GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, 0);
        }

        attribIndex = shader.getColorAttribIndex();
        if (vertexInfo.useAttrib(attribIndex) == true) {
            GLES30.glGenBuffers(1, ids, 0);
            vertexInfo.setVBOID(attribIndex, ids[0]);
            GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, ids[0]);
            floatBuffer = (FloatBuffer) vertexInfo.getBuffer(attribIndex);
            GLES30.glBufferData(GLES30.GL_ARRAY_BUFFER,
                    floatBuffer.capacity() * GLESConfig.FLOAT_SIZE_BYTES,
                    floatBuffer,
                    GLES30.GL_STATIC_DRAW);
            GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, 0);
        }

        if (vertexInfo.useIndex() == true) {
            GLES30.glGenBuffers(1, ids, 0);
            vertexInfo.setIndexVBOID(ids[0]);
            GLES30.glBindBuffer(GLES30.GL_ELEMENT_ARRAY_BUFFER, ids[0]);
            ShortBuffer shortBuffer = vertexInfo.getIndexBuffer();
            GLES30.glBufferData(GLES30.GL_ELEMENT_ARRAY_BUFFER,
                    shortBuffer.capacity() * GLESConfig.SHORT_SIZE_BYTES,
                    shortBuffer,
                    GLES30.GL_STATIC_DRAW);
            GLES30.glBindBuffer(GLES30.GL_ELEMENT_ARRAY_BUFFER, 0);
        }

        if (mListener != null) {
            mListener.setupVBO(shader, vertexInfo);
        }
    }

    @Override
    public void setupVAO(GLESShader shader, GLESVertexInfo vertexInfo) {
        int[] vaoIDs = new int[1];
        GLES30.glGenVertexArrays(1, vaoIDs, 0);
        vertexInfo.setVAOID(vaoIDs[0]);
        GLES30.glBindVertexArray(vaoIDs[0]);


        GLES30.glBindBuffer(GLES30.GL_ELEMENT_ARRAY_BUFFER,
                vertexInfo.getIndexVBOID());

        int attribIndex = shader.getPositionAttribIndex();

        int numOfElements = vertexInfo.getNumOfElements(attribIndex);
        GLES30.glEnableVertexAttribArray(GLESConfig.POSITION_LOCATION);

        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER,
                vertexInfo.getVBOID(attribIndex));
        GLES30.glVertexAttribPointer(GLESConfig.POSITION_LOCATION,
                numOfElements, GLES30.GL_FLOAT, false,
                numOfElements * GLESConfig.FLOAT_SIZE_BYTES,
                0);


        attribIndex = shader.getTexCoordAttribIndex();
        if (vertexInfo.useAttrib(attribIndex) == true) {
            numOfElements = vertexInfo.getNumOfElements(attribIndex);
            GLES30.glEnableVertexAttribArray(GLESConfig.TEXCOORD_LOCATION);

            GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER,
                    vertexInfo.getVBOID(attribIndex));
            GLES30.glVertexAttribPointer(GLESConfig.TEXCOORD_LOCATION,
                    numOfElements, GLES30.GL_FLOAT, false,
                    numOfElements * GLESConfig.FLOAT_SIZE_BYTES,
                    0);

        }

        attribIndex = shader.getNormalAttribIndex();
        if (vertexInfo.useAttrib(attribIndex) == true) {
            numOfElements = vertexInfo.getNumOfElements(attribIndex);
            GLES30.glEnableVertexAttribArray(GLESConfig.NORMAL_LOCATION);

            GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER,
                    vertexInfo.getVBOID(attribIndex));
            GLES30.glVertexAttribPointer(GLESConfig.NORMAL_LOCATION,
                    numOfElements, GLES30.GL_FLOAT, false,
                    numOfElements * GLESConfig.FLOAT_SIZE_BYTES,
                    0);
        }

        attribIndex = shader.getColorAttribIndex();
        if (vertexInfo.useAttrib(attribIndex) == true) {
            numOfElements = vertexInfo.getNumOfElements(attribIndex);
            GLES30.glEnableVertexAttribArray(GLESConfig.COLOR_LOCATION);

            GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER,
                    vertexInfo.getVBOID(attribIndex));
            GLES30.glVertexAttribPointer(GLESConfig.COLOR_LOCATION,
                    numOfElements, GLES30.GL_FLOAT, false,
                    numOfElements * GLESConfig.FLOAT_SIZE_BYTES,
                    0);
        }

        if (mListener != null) {
            mListener.setupVAO(shader, vertexInfo);
        }

        GLES30.glBindVertexArray(vaoIDs[0]);
    }

    @Override
    protected void enableVertexAttribute(GLESObject object) {
        GLESVertexInfo vertexInfo = object.getVertexInfo();
        GLESShader shader = object.getShader();

        boolean useVBO = object.useVBO();
        boolean useVAO = object.useVAO();
        if (useVAO == true) {
            GLES30.glBindVertexArray(vertexInfo.getVAOID());
            return;
        }

        int attribIndex = shader.getPositionAttribIndex();

        if (useVBO == true) {
            int id = vertexInfo.getVBOID(attribIndex);
            GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, id);

            int numOfElements = vertexInfo.getNumOfElements(attribIndex);
            GLES30.glVertexAttribPointer(GLESConfig.POSITION_LOCATION,
                    numOfElements, GLES30.GL_FLOAT, false,
                    numOfElements * GLESConfig.FLOAT_SIZE_BYTES,
                    0);
            GLES30.glEnableVertexAttribArray(GLESConfig.POSITION_LOCATION);

            attribIndex = shader.getNormalAttribIndex();
            if (vertexInfo.useAttrib(attribIndex) == true) {
                id = vertexInfo.getVBOID(attribIndex);
                GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, id);

                numOfElements = vertexInfo.getNumOfElements(attribIndex);
                GLES30.glVertexAttribPointer(GLESConfig.NORMAL_LOCATION,
                        numOfElements, GLES30.GL_FLOAT, false,
                        numOfElements * GLESConfig.FLOAT_SIZE_BYTES,
                        0);
                GLES30.glEnableVertexAttribArray(GLESConfig.NORMAL_LOCATION);
            }

            attribIndex = shader.getTexCoordAttribIndex();
            if (vertexInfo.useAttrib(attribIndex) == true) {
                id = vertexInfo.getVBOID(attribIndex);
                GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, id);

                numOfElements = vertexInfo.getNumOfElements(attribIndex);
                GLES30.glVertexAttribPointer(GLESConfig.TEXCOORD_LOCATION,
                        numOfElements, GLES30.GL_FLOAT, false,
                        numOfElements * GLESConfig.FLOAT_SIZE_BYTES,
                        0);
                GLES30.glEnableVertexAttribArray(GLESConfig.TEXCOORD_LOCATION);
            }

            attribIndex = shader.getColorAttribIndex();
            if (vertexInfo.useAttrib(attribIndex) == true) {
                id = vertexInfo.getVBOID(attribIndex);
                GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, id);

                numOfElements = vertexInfo.getNumOfElements(attribIndex);
                GLES30.glVertexAttribPointer(GLESConfig.COLOR_LOCATION,
                        numOfElements, GLES30.GL_FLOAT, false,
                        numOfElements * GLESConfig.FLOAT_SIZE_BYTES,
                        0);
                GLES30.glEnableVertexAttribArray(GLESConfig.COLOR_LOCATION);
            }

            GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, 0);
        } else {
            int numOfElements = 0;

            attribIndex = shader.getPositionAttribIndex();
            if (vertexInfo.useAttrib(attribIndex) == true) {
                numOfElements = vertexInfo.getNumOfElements(attribIndex);
                GLES30.glVertexAttribPointer(GLESConfig.POSITION_LOCATION,
                        numOfElements, GLES30.GL_FLOAT, false,
                        numOfElements * GLESConfig.FLOAT_SIZE_BYTES,
                        vertexInfo.getBuffer(attribIndex));
                GLES30.glEnableVertexAttribArray(GLESConfig.POSITION_LOCATION);
            }

            attribIndex = shader.getNormalAttribIndex();
            if (vertexInfo.useAttrib(attribIndex) == true) {
                numOfElements = vertexInfo.getNumOfElements(attribIndex);
                GLES30.glVertexAttribPointer(GLESConfig.NORMAL_LOCATION,
                        numOfElements, GLES30.GL_FLOAT, false,
                        numOfElements * GLESConfig.FLOAT_SIZE_BYTES,
                        vertexInfo.getBuffer(attribIndex));
                GLES30.glEnableVertexAttribArray(GLESConfig.NORMAL_LOCATION);
            }

            attribIndex = shader.getTexCoordAttribIndex();
            if (vertexInfo.useAttrib(attribIndex) == true) {
                numOfElements = vertexInfo.getNumOfElements(attribIndex);
                GLES30.glVertexAttribPointer(GLESConfig.TEXCOORD_LOCATION,
                        numOfElements, GLES30.GL_FLOAT, false,
                        numOfElements * GLESConfig.FLOAT_SIZE_BYTES,
                        vertexInfo.getBuffer(attribIndex));
                GLES30.glEnableVertexAttribArray(GLESConfig.TEXCOORD_LOCATION);
            }

            attribIndex = shader.getColorAttribIndex();
            if (vertexInfo.useAttrib(attribIndex) == true) {
                numOfElements = vertexInfo.getNumOfElements(attribIndex);
                GLES30.glVertexAttribPointer(GLESConfig.COLOR_LOCATION,
                        numOfElements, GLES30.GL_FLOAT, false,
                        numOfElements * GLESConfig.FLOAT_SIZE_BYTES,
                        vertexInfo.getBuffer(attribIndex));
                GLES30.glEnableVertexAttribArray(GLESConfig.COLOR_LOCATION);
            }
        }

        if (mListener != null) {
            mListener.enableVertexAttribute(object);
        }
    }

    @Override
    protected void disableVertexAttribute(GLESObject object) {
        GLESVertexInfo vertexInfo = object.getVertexInfo();
        GLESShader shader = object.getShader();

        boolean useVAO = object.useVAO();
        if (useVAO == true) {
            GLES30.glBindVertexArray(0);
            return;
        }

        GLES30.glDisableVertexAttribArray(GLESConfig.POSITION_LOCATION);

        int attribIndex = shader.getNormalAttribIndex();
        if (vertexInfo.useAttrib(attribIndex) == true) {
            GLES30.glDisableVertexAttribArray(GLESConfig.NORMAL_LOCATION);
        }

        attribIndex = shader.getTexCoordAttribIndex();
        if (vertexInfo.useAttrib(attribIndex) == true) {
            GLES30.glDisableVertexAttribArray(GLESConfig.TEXCOORD_LOCATION);
        }

        attribIndex = shader.getColorAttribIndex();
        if (vertexInfo.useAttrib(attribIndex) == true) {
            GLES30.glDisableVertexAttribArray(GLESConfig.COLOR_LOCATION);
        }

        if (mListener != null) {
            mListener.disableVertexAttribute(object);
        }
    }

    @Override
    protected void drawArraysInstanced(GLESObject object) {
        GLESVertexInfo vertexInfo = object.getVertexInfo();
        GLESShader shader = object.getShader();
        PrimitiveMode mode = vertexInfo.getPrimitiveMode();
        int instanceCount = vertexInfo.getNumOfInstance();

        int positionIndex = shader.getPositionAttribIndex();

        int numOfVertex = vertexInfo.getBuffer(positionIndex).capacity()
                / vertexInfo.getNumOfElements(positionIndex);

        switch (mode) {
            case TRIANGLES:
                GLES30.glDrawArraysInstanced(GLES30.GL_TRIANGLES, 0, numOfVertex,
                        instanceCount);
                break;
            case TRIANGLE_FAN:
                GLES30.glDrawArraysInstanced(GLES30.GL_TRIANGLE_FAN, 0,
                        numOfVertex, instanceCount);
                break;
            case TRIANGLE_STRIP:
                GLES30.glDrawArraysInstanced(GLES30.GL_TRIANGLE_STRIP, 0,
                        numOfVertex, instanceCount);
                break;
            case LINES:
                GLES30.glDrawArraysInstanced(GLES30.GL_LINES, 0,
                        numOfVertex, instanceCount);
                break;
            case LINE_STRIP:
                GLES30.glDrawArraysInstanced(GLES30.GL_LINE_STRIP, 0,
                        numOfVertex, instanceCount);
                break;
            case LINE_LOOP:
                GLES30.glDrawArraysInstanced(GLES30.GL_LINE_LOOP, 0,
                        numOfVertex, instanceCount);
                break;
            case POINTS:
                GLES30.glDrawArraysInstanced(GLES30.GL_POINTS, 0,
                        numOfVertex, instanceCount);
                break;
            default:
                Log.d(TAG, "drawArrays() mode is invalid. mode=" + mode);
                break;
        }
    }

    @Override
    protected void drawElementsInstanced(GLESObject object) {
        GLESVertexInfo vertexInfo = object.getVertexInfo();
        PrimitiveMode mode = vertexInfo.getPrimitiveMode();
        int instanceCount = vertexInfo.getNumOfInstance();

        if (object.useVBO() == true) {
            int id = vertexInfo.getIndexVBOID();
            GLES30.glBindBuffer(GLES30.GL_ELEMENT_ARRAY_BUFFER, id);

            ShortBuffer indexBuffer = vertexInfo.getIndexBuffer();
            switch (mode) {
                case TRIANGLES:
                    GLES30.glDrawElementsInstanced(GLES30.GL_TRIANGLES,
                            indexBuffer.capacity(),
                            GLES30.GL_UNSIGNED_SHORT, 0, instanceCount);
                    break;
                case TRIANGLE_FAN:
                    GLES30.glDrawElementsInstanced(GLES30.GL_TRIANGLE_FAN,
                            indexBuffer.capacity(),
                            GLES30.GL_UNSIGNED_SHORT, 0, instanceCount);
                    break;
                case TRIANGLE_STRIP:
                    GLES30.glDrawElementsInstanced(GLES30.GL_TRIANGLE_STRIP,
                            indexBuffer.capacity(),
                            GLES30.GL_UNSIGNED_SHORT, 0, instanceCount);
                    break;
                case LINES:
                    GLES30.glDrawElementsInstanced(GLES30.GL_LINES,
                            indexBuffer.capacity(),
                            GLES30.GL_UNSIGNED_SHORT, 0, instanceCount);
                    break;
                case LINE_STRIP:
                    GLES30.glDrawElementsInstanced(GLES30.GL_LINE_STRIP,
                            indexBuffer.capacity(),
                            GLES30.GL_UNSIGNED_SHORT, 0, instanceCount);
                    break;
                case LINE_LOOP:
                    GLES30.glDrawElementsInstanced(GLES30.GL_LINE_LOOP,
                            indexBuffer.capacity(),
                            GLES30.GL_UNSIGNED_SHORT, 0, instanceCount);
                    break;
                case POINTS:
                    GLES30.glDrawElementsInstanced(GLES30.GL_POINTS,
                            indexBuffer.capacity(),
                            GLES30.GL_UNSIGNED_SHORT, 0, instanceCount);
                    break;
                default:
                    Log.d(TAG, "drawElements() mode is invalid. mode=" + mode);
                    break;
            }

            GLES30.glBindBuffer(GLES30.GL_ELEMENT_ARRAY_BUFFER, 0);
        } else {
            ShortBuffer indexBuffer = vertexInfo.getIndexBuffer();
            switch (mode) {
                case TRIANGLES:
                    GLES30.glDrawElementsInstanced(GLES30.GL_TRIANGLES,
                            indexBuffer.capacity(),
                            GLES30.GL_UNSIGNED_SHORT, indexBuffer, instanceCount);
                    break;
                case TRIANGLE_FAN:
                    GLES30.glDrawElementsInstanced(GLES30.GL_TRIANGLE_FAN,
                            indexBuffer.capacity(),
                            GLES30.GL_UNSIGNED_SHORT, indexBuffer, instanceCount);
                    break;
                case TRIANGLE_STRIP:
                    GLES30.glDrawElementsInstanced(GLES30.GL_TRIANGLE_FAN,
                            indexBuffer.capacity(),
                            GLES30.GL_UNSIGNED_SHORT, indexBuffer, instanceCount);
                    break;
                case LINES:
                    GLES30.glDrawElementsInstanced(GLES30.GL_LINES,
                            indexBuffer.capacity(),
                            GLES30.GL_UNSIGNED_SHORT, indexBuffer, instanceCount);
                    break;
                case LINE_STRIP:
                    GLES30.glDrawElementsInstanced(GLES30.GL_LINE_STRIP,
                            indexBuffer.capacity(),
                            GLES30.GL_UNSIGNED_SHORT, indexBuffer, instanceCount);
                    break;
                case LINE_LOOP:
                    GLES30.glDrawElementsInstanced(GLES30.GL_LINE_LOOP,
                            indexBuffer.capacity(),
                            GLES30.GL_UNSIGNED_SHORT, indexBuffer, instanceCount);
                    break;
                case POINTS:
                    GLES30.glDrawElementsInstanced(GLES30.GL_POINTS,
                            indexBuffer.capacity(),
                            GLES30.GL_UNSIGNED_SHORT, indexBuffer, instanceCount);
                    break;
                default:
                    Log.d(TAG, "drawElements() mode is invalid. mode=" + mode);
                    break;
            }
        }
    }
}
