package com.gomdev.gles;

public class GLESVector3 {
    static final String CLASS = "GLESVector3";
    static final String TAG = GLESConfig.TAG + "_" + CLASS;
    static final boolean DEBUG = GLESConfig.DEBUG;

    float mX = 0.0F;
    float mY = 0.0F;
    float mZ = 0.0F;

    public GLESVector3() {
        mX = 0f;
        mY = 0f;
        mZ = 0f;
    }

    public GLESVector3(float x, float y, float z) {
        mX = x;
        mY = y;
        mZ = z;
    }

    public GLESVector3(GLESVector3 vector) {
        mX = vector.mX;
        mY = vector.mY;
        mZ = vector.mZ;
    }

    public GLESVector3(float[] array) {
        mX = array[0];
        mY = array[1];
        mZ = array[2];
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

    public static GLESVector3 add(GLESVector3 vector1, GLESVector3 vector2) {
        return new GLESVector3(vector1.mX + vector2.mX,
                vector1.mY + vector2.mY,
                vector1.mZ + vector2.mZ);
    }

    public static GLESVector3 cross(GLESVector3 vector1, GLESVector3 vector2) {
        return new GLESVector3(
                vector1.mY * vector2.mZ - vector1.mZ * vector2.mY, vector1.mZ
                * vector2.mX - vector1.mX * vector2.mZ, vector1.mX
                * vector2.mY - vector1.mY * vector2.mX);
    }

    public static float dot(GLESVector3 vector1, GLESVector3 vector2) {
        return vector1.mX * vector2.mX + vector1.mY * vector2.mY + vector1.mZ
                * vector2.mZ;
    }

    public static GLESVector3 getNomalVector(GLESVector3 vector1,
                                             GLESVector3 vector2) {
        GLESVector3 normalVector = cross(vector1, vector2);
        normalVector.normalize();
        return normalVector;
    }

    public static GLESVector3 getNomalVector(float[] point1, float[] point2,
                                             float[] point3) {
        GLESVector3 normalVector = cross(new GLESVector3(point2[0] - point1[0],
                point2[1] - point1[1], point2[2] - point1[2]), new GLESVector3(
                point3[0] - point1[0], point3[1] - point1[1], point3[2]
                - point1[2]));
        normalVector.normalize();
        return normalVector;
    }

    public static GLESVector3 subtract(GLESVector3 vector1, GLESVector3 vector2) {
        return new GLESVector3(vector1.mX - vector2.mX,
                vector1.mY - vector2.mY,
                vector1.mZ - vector2.mZ);
    }

    public GLESVector3 add(GLESVector3 vector) {
        mX += vector.mX;
        mY += vector.mY;
        mZ += vector.mZ;
        return this;
    }

    public float[] get() {
        float[] array = new float[3];
        array[0] = mX;
        array[1] = mY;
        array[2] = mZ;
        return array;
    }

    public float length() {
        return (float) Math.sqrt(mX * mX + mY * mY + mZ * mZ);
    }

    public GLESVector3 multiply(float value) {
        mX = (value * mX);
        mY = (value * mY);
        mZ = (value * mZ);
        return this;
    }

    public GLESVector3 normalize() {
        float f = 1.0F / length();
        mX = (f * mX);
        mY = (f * mY);
        mZ = (f * mZ);
        return this;
    }

    public void set(float x, float y, float z) {
        mX = x;
        mY = y;
        mZ = z;
    }

    public void set(GLESVector3 vec) {
        mX = vec.mX;
        mY = vec.mY;
        mZ = vec.mZ;
    }

    public GLESVector3 subtract(GLESVector3 vector) {
        mX -= vector.mX;
        mY -= vector.mY;
        mZ -= vector.mZ;
        return this;
    }

    public String toString() {
        return "GLESVector mX=" + mX + " mY=" + mY + " mZ=" + mZ;
    }
}
