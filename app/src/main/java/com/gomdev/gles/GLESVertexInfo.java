package com.gomdev.gles;

import android.util.Log;
import android.util.SparseArray;

import java.nio.Buffer;
import java.nio.ShortBuffer;

public final class GLESVertexInfo {
    static final String CLASS = "GLESVertexInfo";
    static final String TAG = GLESConfig.TAG + "_" + CLASS;
    static final boolean DEBUG = GLESConfig.DEBUG;

    public enum PrimitiveMode {
        TRIANGLES,
        TRIANGLE_STRIP,
        TRIANGLE_FAN,
        LINES,
        LINE_STRIP,
        LINE_LOOP,
        POINTS
    }

    public enum RenderType {
        DRAW_ELEMENTS,
        DRAW_ARRAYS,
        DRAW_ELEMENTS_INSTANCED,
        DRAW_ARRAYS_INSTANCED,
    }

    class AttributeInfo {
        int mIndex = -1;
        Buffer mBuffer = null;
        int mVBOID = -1;
        int mNumOfElements = 0;
    }

    private SparseArray<AttributeInfo> mAttribs = new SparseArray<AttributeInfo>();

    private PrimitiveMode mPrimitiveMode = PrimitiveMode.TRIANGLES;
    private RenderType mRenderType = RenderType.DRAW_ELEMENTS;

    private int mNumOfVertex = 0;
    private int mNumOfInstance = 0;

    private boolean mUseIndex = false;

    private ShortBuffer mIndexBuffer = null;
    private int mIndexVBOID = -1;

    private int mVAOID = -1;

    public GLESVertexInfo() {

    }

    public void setPrimitiveMode(PrimitiveMode mode) {
        mPrimitiveMode = mode;
    }

    public PrimitiveMode getPrimitiveMode() {
        return mPrimitiveMode;
    }

    public void setRenderType(RenderType type) {
        mRenderType = type;
    }

    public RenderType getRenderType() {
        return mRenderType;
    }

    public void setNumOfInstance(int num) {
        mNumOfInstance = num;
    }

    public int getNumOfInstance() {
        return mNumOfInstance;
    }

    public void setBuffer(int index, float[] data, int numOfElements) {
        AttributeInfo attribInfo = new AttributeInfo();
        attribInfo.mIndex = index;
        attribInfo.mBuffer = GLESUtils.makeFloatBuffer(data);
        attribInfo.mNumOfElements = numOfElements;

        mAttribs.append(index, attribInfo);
    }

    public void setBuffer(int index, Buffer buffer) {
        AttributeInfo attribInfo = mAttribs.get(index);

        if (attribInfo == null) {
            Log.e(TAG, "setBuffer() index=" + index + " attribInfo is null");
            new Exception().printStackTrace();
            return;
        }

        attribInfo.mIndex = index;
        attribInfo.mBuffer = buffer;
    }

    public Buffer getBuffer(int index) {
        AttributeInfo attribInfo = mAttribs.get(index);
        if (attribInfo == null) {
            Log.e(TAG, "getBuffer() index=" + index + " attribInfo is null");
            new Exception().printStackTrace();
            return null;
        }

        return attribInfo.mBuffer;
    }

    public void setNumOfVertex(int numOfVertex) {
        mNumOfVertex = numOfVertex;
    }

    public int getNumOfVertex() {
        return mNumOfVertex;
    }

    public int getNumOfElements(int index) {
        AttributeInfo attribInfo = mAttribs.get(index);
        if (attribInfo == null) {
            Log.e(TAG, "getNumOfElements() index=" + index + " attribInfo is null");
            new Exception().printStackTrace();
            return 0;
        }

        return attribInfo.mNumOfElements;
    }

    public void setVBOID(int index, int vboID) {
        AttributeInfo attribInfo = mAttribs.get(index);
        if (attribInfo == null) {
            Log.e(TAG, "setVBOID() index=" + index + " vboID=" + vboID + " attribInfo is null");
            new Exception().printStackTrace();
            return;
        }

        attribInfo.mVBOID = vboID;
    }

    public int getVBOID(int index) {
        AttributeInfo attribInfo = mAttribs.get(index);
        if (attribInfo == null) {
            Log.e(TAG, "getBuffer() index=" + index + " attribInfo is null");
            new Exception().printStackTrace();
            return -1;
        }

        return attribInfo.mVBOID;
    }

    public boolean useAttrib(int index) {
        AttributeInfo attribInfo = mAttribs.get(index);
        if (attribInfo == null) {
            return false;
        }

        return true;
    }

    public void setIndexBuffer(short[] index) {
        mIndexBuffer = GLESUtils.makeShortBuffer(index);
        mUseIndex = true;
    }

    public ShortBuffer getIndexBuffer() {
        return mIndexBuffer;
    }

    public boolean useIndex() {
        return mUseIndex;
    }

    public void setIndexVBOID(int id) {
        mIndexVBOID = id;
    }

    public int getIndexVBOID() {
        return mIndexVBOID;
    }

    public void setVAOID(int id) {
        mVAOID = id;
    }

    public int getVAOID() {
        return mVAOID;
    }
}
