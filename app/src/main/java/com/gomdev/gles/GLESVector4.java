package com.gomdev.gles;

public class GLESVector4 {
    static final String CLASS = "GLESVector4";
    static final String TAG = GLESConfig.TAG + "_" + CLASS;
    static final boolean DEBUG = GLESConfig.DEBUG;

    float mX = 0.0F;
    float mY = 0.0F;
    float mZ = 0.0F;
    float mW = 0.0F;

    public GLESVector4() {
        mX = 0f;
        mY = 0f;
        mZ = 0f;
        mW = 1f;
    }

    public GLESVector4(float x, float y, float z, float w) {
        mX = x;
        mY = y;
        mZ = z;
        mW = w;
    }

    public GLESVector4(GLESVector4 vector) {
        mX = vector.mX;
        mY = vector.mY;
        mZ = vector.mZ;
        mW = vector.mW;
    }

    public GLESVector4(float[] array) {
        mX = array[0];
        mY = array[1];
        mZ = array[2];
        mW = array[3];
    }

    public void setX(float x) {
        mX = x;
    }

    public float getX() {
        return mX;
    }

    public void setY(float y) {
        mY = y;
    }

    public float getY() {
        return mY;
    }

    public void setZ(float z) {
        mZ = z;
    }

    public float getZ() {
        return mZ;
    }

    public void setW(float w) {
        mW = w;
    }

    public float getW() {
        return mW;
    }

    public static GLESVector4 add(GLESVector4 vector1, GLESVector4 vector2) {
        GLESVector4 result = new GLESVector4(
                vector1.mX + vector2.mX,
                vector1.mY + vector2.mY,
                vector1.mZ + vector2.mZ,
                vector1.mW + vector2.mW);
        if (result.mW >= 1f) {
            result.mW = 1f;
        }

        return result;
    }

    public static GLESVector4 cross(GLESVector4 vector1, GLESVector4 vector2) {
        GLESVector4 result = new GLESVector4(
                vector1.mY * vector2.mZ - vector1.mZ * vector2.mY,
                vector1.mZ * vector2.mX - vector1.mX * vector2.mZ,
                vector1.mX * vector2.mY - vector1.mY * vector2.mX,
                0f);

        return result;
    }

    public static float dot(GLESVector4 vector1, GLESVector4 vector2) {
        return vector1.mX * vector2.mX + vector1.mY * vector2.mY + vector1.mZ
                * vector2.mZ;
    }

    public static GLESVector4 getNomalVector(GLESVector4 vector1,
                                             GLESVector4 vector2) {
        GLESVector4 normalVector = cross(vector1, vector2);
        normalVector.normalize();
        return normalVector;
    }

    public static GLESVector4 getNomalVector(float[] point1, float[] point2,
                                             float[] point3) {
        GLESVector4 normalVector = cross(
                new GLESVector4(point2[0] - point1[0], point2[1] - point1[1],
                        point2[2] - point1[2], 0f),
                new GLESVector4(point3[0] - point1[0], point3[1] - point1[1],
                        point3[2] - point1[2], 0f));
        normalVector.normalize();
        return normalVector;
    }

    public static GLESVector4 subtract(GLESVector4 vector1, GLESVector4 vector2) {
        GLESVector4 result = new GLESVector4(
                vector1.mX - vector2.mX,
                vector1.mY - vector2.mY,
                vector1.mZ - vector2.mZ,
                vector1.mW + vector2.mW);

        if (result.mW >= 1.0f) {
            result.mW = 1.0f;
        }

        return result;
    }

    public GLESVector4 add(GLESVector4 vector) {
        mX += vector.mX;
        mY += vector.mY;
        mZ += vector.mZ;
        mW += vector.mW;

        if (mW >= 1.0f) {
            mW = 1.0f;
        }

        return this;
    }

    public float[] get() {
        float[] array = new float[4];
        array[0] = mX;
        array[1] = mY;
        array[2] = mZ;
        array[3] = mW;
        return array;
    }

    public float length() {
        return (float) Math.sqrt(mX * mX + mY * mY + mZ * mZ);
    }

    public GLESVector4 multiply(float value) {
        mX = (value * mX);
        mY = (value * mY);
        mZ = (value * mZ);
        return this;
    }

    public GLESVector4 normalize() {
        float f = 1.0F / length();
        mX = (f * mX);
        mY = (f * mY);
        mZ = (f * mZ);
        return this;
    }

    public void set(float x, float y, float z, float w) {
        mX = x;
        mY = y;
        mZ = z;
        mW = w;
    }

    public GLESVector4 subtract(GLESVector4 vector) {
        mX -= vector.mX;
        mY -= vector.mY;
        mZ -= vector.mZ;
        mW += vector.mW;

        if (mW >= 1.0) {
            mW = 1.0f;
        }

        return this;
    }

    public String toString() {
        return "GLESVector mX=" + mX + " mY=" + mY + " mZ=" + mZ
                + " mW=" + mW;
    }
}
