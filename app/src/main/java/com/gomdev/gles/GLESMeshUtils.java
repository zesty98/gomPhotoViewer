package com.gomdev.gles;

import android.opengl.GLES20;
import android.util.Log;

import com.gomdev.gles.GLESVertexInfo.PrimitiveMode;
import com.gomdev.gles.GLESVertexInfo.RenderType;

public final class GLESMeshUtils {
    static final String CLASS = "GLESMeshUtils";
    static final String TAG = GLESConfig.TAG + "_" + CLASS;
    static final boolean DEBUG = GLESConfig.DEBUG;

    private GLESMeshUtils() {

    }

    public static GLESVertexInfo createTriangle(GLESShader shader, float width,
                                                float height,
                                                boolean useNormal, boolean useTexCoord, boolean useColor,
                                                boolean useIndex) {

        return createTriangle(shader, width, height, useNormal, useTexCoord,
                useColor,
                useIndex, 1f, 0f, 0f);
    }

    public static GLESVertexInfo createTriangle(GLESShader shader, float width,
                                                float height,
                                                boolean useNormal, boolean useTexCoord, boolean useColor,
                                                boolean useIndex, float red, float green, float blue) {

        float right = width * 0.5f;
        float left = -right;
        float top = height * 0.5f;
        float bottom = -top;
        float center = 0f;
        float z = 0.0f;

        float[] vertex = {
                left, bottom, z,
                right, bottom, z,
                center, top, z,
        };

        GLESVertexInfo vertexInfo = new GLESVertexInfo();

        vertexInfo.setBuffer(shader.getPositionAttribIndex(), vertex, 3);

        if (useTexCoord == true) {
            float[] texCoord = {
                    0f, 1f,
                    1f, 1f,
                    0.5f, 0f,
            };

            vertexInfo.setBuffer(shader.getTexCoordAttribIndex(), texCoord, 2);
        }

        if (useNormal == true) {
            float[] normal = {
                    0f, 0f, 1f,
                    0f, 0f, 1f,
                    0f, 0f, 1f,
            };

            vertexInfo.setBuffer(shader.getNormalAttribIndex(), normal, 3);
        }

        if (useColor == true) {
            float[] color = {
                    red, green, blue, 1f,
                    red, green, blue, 1f,
                    red, green, blue, 1f,
            };

            vertexInfo.setBuffer(shader.getColorAttribIndex(), color, 4);
        }

        if (useIndex == true) {
            short[] index = {
                    0, 1, 2,
            };

            vertexInfo.setIndexBuffer(index);

            vertexInfo.setRenderType(RenderType.DRAW_ELEMENTS);
            vertexInfo.setPrimitiveMode(PrimitiveMode.TRIANGLES);
        } else {
            vertexInfo.setRenderType(RenderType.DRAW_ARRAYS);
            vertexInfo.setPrimitiveMode(PrimitiveMode.TRIANGLES);
        }

        return vertexInfo;
    }

    public static GLESVertexInfo createPlaneMesh(GLESShader shader,
                                                 float width,
                                                 float height,
                                                 int resolution, boolean useTexCoord, boolean useNormal) {

        int wResolution = 0;
        int hResolution = resolution + 2;
        if (height > width) {
            wResolution = (int) (resolution * width / height) + 2;
        } else {
            wResolution = (int) (resolution * height / width) + 2;
        }

        if (DEBUG) {
            Log.d(TAG, "createMesh() hResolution=" + hResolution
                    + " wResolution=" + wResolution);
        }

        int numOfVertics = (wResolution + 1) * (hResolution + 1);

        float[] vertices = new float[numOfVertics
                * GLESConfig.NUM_OF_VERTEX_ELEMENT];
        float[] texCoord = null;
        float[] normal = null;

        if (useNormal == true) {
            normal = new float[numOfVertics * GLESConfig.NUM_OF_NORMAL_ELEMENT];
        }

        if (useTexCoord == true) {
            texCoord = new float[numOfVertics
                    * GLESConfig.NUM_OF_TEXCOORD_ELEMENT];
        }

        float halfWidth = width * 0.5f;
        float halfHeight = height * 0.5f;

        int vertexIndex = 0;
        int texCoordIndex = 0;
        int normalIndex = 0;
        for (int y = 0; y <= hResolution; y++) {
            final float normalizedY = (float) y / hResolution;
            final float yOffset = (1.0f - normalizedY) * height;
            for (int x = 0; x <= wResolution; x++) {
                float normalizedX = (float) x / wResolution;
                float xOffset = normalizedX * width;

                vertices[vertexIndex++] = xOffset - halfWidth;
                vertices[vertexIndex++] = yOffset - halfHeight;
                vertices[vertexIndex++] = 0f;

                if (useNormal == true) {
                    normal[normalIndex++] = 0f;
                    normal[normalIndex++] = 0f;
                    normal[normalIndex++] = 1f;
                }

                if (useTexCoord == true) {
                    texCoord[texCoordIndex++] = normalizedX;
                    texCoord[texCoordIndex++] = normalizedY;
                }
            }
        }

        GLESVertexInfo vertexInfo = new GLESVertexInfo();

        vertexInfo.setBuffer(shader.getPositionAttribIndex(), vertices, 3);

        if (useTexCoord == true) {
            vertexInfo.setBuffer(shader.getTexCoordAttribIndex(), texCoord, 2);
        }

        if (useNormal == true) {
            vertexInfo.setBuffer(shader.getNormalAttribIndex(), normal, 3);
        }

        short[] indices = new short[hResolution * wResolution
                * GLESConfig.NUM_OF_INDEX_ELEMENT];

        for (int y = 0, idx = 0; y < hResolution; y++) {
            final int curY = y * (wResolution + 1);
            final int belowY = (y + 1) * (wResolution + 1);
            for (int x = 0; x < wResolution; x++) {
                int curV = curY + x;
                int belowV = belowY + x;

                indices[idx++] = (short) curV;
                indices[idx++] = (short) (belowV);
                indices[idx++] = (short) (curV + 1);

                indices[idx++] = (short) (belowV);
                indices[idx++] = (short) (belowV + 1);
                indices[idx++] = (short) (curV + 1);

                // if(DEBUG) Log.d(TAG, "index (" + curV + ", " + belowV + ", "
                // + (curV + 1) + ") \t (" + belowV + ", " + (belowV + 1) + ", "
                // + (curV + 1) + ")");
            }
        }

        vertexInfo.setIndexBuffer(indices);

        vertexInfo.setRenderType(RenderType.DRAW_ELEMENTS);
        vertexInfo.setPrimitiveMode(PrimitiveMode.TRIANGLES);

        return vertexInfo;
    }

    public static GLESVertexInfo createPlane(GLESShader shader, float width,
                                             float height,
                                             boolean useNormal, boolean useTexCoord, boolean useColor,
                                             boolean useIndex) {

        return createPlane(shader, width, height, useNormal, useTexCoord,
                useColor,
                useIndex, 1f, 0f, 0f);
    }

    public static GLESVertexInfo createPlane(GLESShader shader, float width,
                                             float height,
                                             boolean useNormal, boolean useTexCoord, boolean useColor,
                                             boolean useIndex, float red, float green, float blue) {

        GLESVertexInfo vertexInfo = createPlane(shader,
                width, height,
                0f,
                useNormal, useTexCoord, useColor, useIndex,
                red, green, blue);

        return vertexInfo;
    }

    public static GLESVertexInfo createPlane(GLESShader shader,
                                             float width, float height,
                                             float depth,
                                             boolean useNormal, boolean useTexCoord, boolean useColor,
                                             boolean useIndex, float red, float green, float blue) {

        float right = width * 0.5f;
        float left = -right;
        float top = height * 0.5f;
        float bottom = -top;
        float z = depth;

        float[] vertex = {
                left, bottom, z,
                right, bottom, z,
                left, top, z,
                right, top, z
        };

        GLESVertexInfo vertexInfo = new GLESVertexInfo();

        vertexInfo.setBuffer(shader.getPositionAttribIndex(), vertex, 3);

        if (useTexCoord == true) {
            float[] texCoord = {
                    0f, 1f,
                    1f, 1f,
                    0f, 0f,
                    1f, 0f
            };

            vertexInfo.setBuffer(shader.getTexCoordAttribIndex(), texCoord, 2);
        }

        if (useNormal == true) {
            float[] normal = {
                    0f, 0f, 1f,
                    0f, 0f, 1f,
                    0f, 0f, 1f,
                    0f, 0f, 1f
            };

            vertexInfo.setBuffer(shader.getNormalAttribIndex(), normal, 3);
        }

        if (useColor == true) {
            float[] color = {
                    red, green, blue, 1f,
                    red, green, blue, 1f,
                    red, green, blue, 1f,
                    red, green, blue, 1f,
            };

            vertexInfo.setBuffer(shader.getColorAttribIndex(), color, 4);
        }

        if (useIndex == true) {
            short[] index = {
                    0, 1, 2,
                    2, 1, 3
            };

            vertexInfo.setIndexBuffer(index);

            vertexInfo.setRenderType(RenderType.DRAW_ELEMENTS);
            vertexInfo.setPrimitiveMode(PrimitiveMode.TRIANGLES);
        } else {
            vertexInfo.setRenderType(RenderType.DRAW_ARRAYS);
            vertexInfo.setPrimitiveMode(PrimitiveMode.TRIANGLE_STRIP);
        }

        return vertexInfo;
    }

    public static GLESVertexInfo createPlaneForDebug(GLESShader shader,
                                                     float width, float height,
                                                     boolean useNormal, boolean useTexCoord, boolean useColor,
                                                     boolean useIndex) {

        return createPlaneForDebug(shader, width, height, useNormal,
                useTexCoord,
                useColor,
                useIndex, 1f, 0f, 0f);
    }

    public static GLESVertexInfo createPlaneForDebug(GLESShader shader,
                                                     float width, float height,
                                                     boolean useNormal, boolean useTexCoord, boolean useColor,
                                                     boolean useIndex, float red, float green, float blue) {

        float right = width * 0.5f;
        float left = -right;
        float top = height * 0.5f;
        float bottom = -top;
        float z = 0.0f;

        float[] vertex = {
                left, bottom, z,
                right, bottom, z,
                right, top, z,
                left, top, z,
        };

        GLESVertexInfo vertexInfo = new GLESVertexInfo();

        vertexInfo.setBuffer(shader.getPositionAttribIndex(), vertex, 3);

        if (useTexCoord == true) {
            float[] texCoord = {
                    0f, 1f,
                    1f, 1f,
                    1f, 0f,
                    0f, 0f,
            };

            vertexInfo.setBuffer(shader.getTexCoordAttribIndex(), texCoord, 2);
        }

        if (useNormal == true) {
            float[] normal = {
                    0f, 0f, 1f,
                    0f, 0f, 1f,
                    0f, 0f, 1f,
                    0f, 0f, 1f
            };

            vertexInfo.setBuffer(shader.getNormalAttribIndex(), normal, 3);
        }

        if (useColor == true) {
            float[] color = {
                    red, green, blue, 1f,
                    red, green, blue, 1f,
                    red, green, blue, 1f,
                    red, green, blue, 1f,
            };

            vertexInfo.setBuffer(shader.getColorAttribIndex(), color, 4);
        }

        GLES20.glLineWidth(4.0f);

        if (useIndex == true) {
            short[] index = {
                    0, 1, 2, 3
            };

            vertexInfo.setIndexBuffer(index);

            vertexInfo.setRenderType(RenderType.DRAW_ELEMENTS);
            vertexInfo.setPrimitiveMode(PrimitiveMode.LINE_LOOP);
        } else {
            vertexInfo.setRenderType(RenderType.DRAW_ARRAYS);
            vertexInfo.setPrimitiveMode(PrimitiveMode.LINE_LOOP);
        }

        return vertexInfo;
    }

    public static GLESVertexInfo createCube(GLESShader shader, float cubeSize,
                                            boolean useNormal, boolean useTexCoord, boolean useColor) {
        float half = cubeSize * 0.5f;

        float[] vertex = new float[]{
                // front
                -half, -half, half,
                half, -half, half,
                half, half, half,
                -half, half, half,

                // left
                -half, -half, -half,
                -half, -half, half,
                -half, half, half,
                -half, half, -half,

                // back
                half, -half, -half,
                -half, -half, -half,
                -half, half, -half,
                half, half, -half,

                // right
                half, -half, half,
                half, -half, -half,
                half, half, -half,
                half, half, half,

                // top
                -half, half, half,
                half, half, half,
                half, half, -half,
                -half, half, -half,

                // bottom
                -half, -half, -half,
                half, -half, -half,
                half, -half, half,
                -half, -half, half
        };

        GLESVertexInfo vertexInfo = new GLESVertexInfo();

        vertexInfo.setBuffer(shader.getPositionAttribIndex(), vertex, 3);

        short[] index = new short[]{
                0, 1, 3, 1, 2, 3,
                4, 5, 7, 5, 6, 7,
                8, 9, 11, 9, 10, 11,
                12, 13, 15, 13, 14, 15,
                16, 17, 19, 17, 18, 19,
                20, 21, 23, 21, 22, 23
        };

        vertexInfo.setIndexBuffer(index);

        if (useNormal == true) {

            float[] normal = new float[]{
                    // front
                    0f, 0f, 1f,
                    0f, 0f, 1f,
                    0f, 0f, 1f,
                    0f, 0f, 1f,

                    // left
                    -1f, 0f, 0f,
                    -1f, 0f, 0f,
                    -1f, 0f, 0f,
                    -1f, 0f, 0f,

                    // back
                    0f, 0f, -1f,
                    0f, 0f, -1f,
                    0f, 0f, -1f,
                    0f, 0f, -1f,

                    // right
                    1f, 0f, 0f,
                    1f, 0f, 0f,
                    1f, 0f, 0f,
                    1f, 0f, 0f,

                    // top
                    0f, 1f, 0f,
                    0f, 1f, 0f,
                    0f, 1f, 0f,
                    0f, 1f, 0f,

                    // bottom
                    0f, -1f, 0f,
                    0f, -1f, 0f,
                    0f, -1f, 0f,
                    0f, -1f, 0f
            };

            vertexInfo.setBuffer(shader.getNormalAttribIndex(), normal, 3);
        }

        if (useTexCoord == true) {
            float[] texCoord = new float[]{
                    // front
                    0.0f, 1.0f,
                    1.0f, 1.0f,
                    1.0f, 0.0f,
                    0.0f, 0.0f,

                    // left
                    0.0f, 1.0f,
                    1.0f, 1.0f,
                    1.0f, 0.0f,
                    0.0f, 0.0f,

                    // back
                    0.0f, 1.0f,
                    1.0f, 1.0f,
                    1.0f, 0.0f,
                    0.0f, 0.0f,

                    // right
                    0.0f, 1.0f,
                    1.0f, 1.0f,
                    1.0f, 0.0f,
                    0.0f, 0.0f,

                    // top
                    0.0f, 1.0f,
                    1.0f, 1.0f,
                    1.0f, 0.0f,
                    0.0f, 0.0f,

                    // bottom
                    0.0f, 1.0f,
                    1.0f, 1.0f,
                    1.0f, 0.0f,
                    0.0f, 0.0f
            };

            vertexInfo.setBuffer(shader.getTexCoordAttribIndex(), texCoord, 2);
        }

        if (useColor == true) {
            float[] color = new float[]{
                    // front
                    0f, 0f, 1f, 1f,
                    0f, 0f, 1f, 1f,
                    0f, 0f, 1f, 1f,
                    0f, 0f, 1f, 1f,

                    // left
                    1f, 1f, 0f, 1f,
                    1f, 1f, 0f, 1f,
                    1f, 1f, 0f, 1f,
                    1f, 1f, 0f, 1f,

                    // back
                    0f, 1f, 1f, 1f,
                    0f, 1f, 1f, 1f,
                    0f, 1f, 1f, 1f,
                    0f, 1f, 1f, 1f,

                    // right
                    1f, 0f, 1f, 1f,
                    1f, 0f, 1f, 1f,
                    1f, 0f, 1f, 1f,
                    1f, 0f, 1f, 1f,

                    // top
                    0f, 1f, 0f, 1f,
                    0f, 1f, 0f, 1f,
                    0f, 1f, 0f, 1f,
                    0f, 1f, 0f, 1f,

                    // bottom
                    1f, 0f, 0f, 1f,
                    1f, 0f, 0f, 1f,
                    1f, 0f, 0f, 1f,
                    1f, 0f, 0f, 1f
            };

            vertexInfo.setBuffer(shader.getColorAttribIndex(), color, 4);
        }

        vertexInfo.setRenderType(RenderType.DRAW_ELEMENTS);
        vertexInfo.setPrimitiveMode(PrimitiveMode.TRIANGLES);

        return vertexInfo;
    }

    public static GLESVertexInfo createSphere(GLESShader shader, float radius,
                                              int numOfVerticalLine,
                                              int numOfHorizontalLine, boolean useTexCoord, boolean useNormal,
                                              boolean useColor) {
        return createSphere(shader, radius, numOfVerticalLine,
                numOfHorizontalLine,
                useTexCoord, useNormal, useColor, 1f, 0f, 0f, 1f);
    }

    public static GLESVertexInfo createSphere(GLESShader shader, float radius,
                                              int numOfVerticalLine,
                                              int numOfHorizontalLine, boolean useTexCoord, boolean useNormal,
                                              boolean useColor, float r, float g, float b, float a) {
        double theta = 0f;
        double sinTheta = 0f;
        double cosTheta = 0f;

        double phi = 0f;
        double sinPhi = 0f;
        double cosPhi = 0f;

        float normalX = 0f;
        float normalY = 0f;
        float normalZ = 0f;

        GLESVertexInfo vertexInfo = new GLESVertexInfo();

        numOfVerticalLine--;

        int numOfVertex = (numOfVerticalLine + 1) * (numOfHorizontalLine + 1);
        float[] vertices = new float[numOfVertex * 3];
        float[] normal = new float[numOfVertex * 3];
        float[] color = new float[numOfVertex * 4];
        float[] texCoord = new float[numOfVertex * 2];

        for (int i = 0; i <= numOfVerticalLine; ++i) {
            theta = Math.PI * (i / (double) numOfVerticalLine);
            sinTheta = Math.sin(theta);
            cosTheta = Math.cos(theta);

            int vertexStride = i * (numOfHorizontalLine + 1) * 3;
            int normalStride = i * (numOfHorizontalLine + 1) * 3;
            int colorStride = i * (numOfHorizontalLine + 1) * 4;
            int texCoordStride = i * (numOfHorizontalLine + 1) * 2;

            for (int j = numOfHorizontalLine, k = 0; j >= 0; --j, k++) {
                phi = 2 * Math.PI * (j / (double) numOfHorizontalLine);
                sinPhi = Math.sin(phi);
                cosPhi = Math.cos(phi);

                normalX = (float) (cosPhi * sinTheta);
                normalY = (float) (cosTheta);
                normalZ = (float) (sinPhi * sinTheta);

                normal[normalStride + k * 3 + 0] = normalX;
                normal[normalStride + k * 3 + 1] = normalY;
                normal[normalStride + k * 3 + 2] = normalZ;

                vertices[vertexStride + k * 3 + 0] = (float) (normalX * radius);
                vertices[vertexStride + k * 3 + 1] = (float) (normalY * radius);
                vertices[vertexStride + k * 3 + 2] = (float) (normalZ * radius);

                color[colorStride + k * 4 + 0] = r;
                color[colorStride + k * 4 + 1] = g;
                color[colorStride + k * 4 + 2] = b;
                color[colorStride + k * 4 + 3] = a;

                texCoord[texCoordStride + k * 2 + 0] = (float) (1 - (j / (double) numOfHorizontalLine));
                texCoord[texCoordStride + k * 2 + 1] = (float) (i / (double) numOfVerticalLine);
            }
        }

        vertexInfo.setBuffer(shader.getPositionAttribIndex(), vertices, 3);

        if (useNormal == true) {
            vertexInfo.setBuffer(shader.getNormalAttribIndex(), normal, 3);
        }

        if (useTexCoord == true) {
            vertexInfo.setBuffer(shader.getTexCoordAttribIndex(), texCoord, 2);
        }

        if (useColor == true) {
            vertexInfo.setBuffer(shader.getColorAttribIndex(), color, 4);
        } else if (useTexCoord == false) {
            new IllegalArgumentException(
                    "Only one of useTexCoord and useColor should be true");
        }

        int numOfIndex = numOfVerticalLine * numOfHorizontalLine * 6;
        short[] indices = new short[numOfIndex];

        short first = 0;
        short second = 0;

        for (int i = 0; i < numOfVerticalLine; ++i) {
            int stride = i * numOfHorizontalLine * 6;
            for (int j = 0; j < numOfHorizontalLine; ++j) {
                first = (short) ((i * (numOfHorizontalLine + 1)) + j);
                second = (short) (first + numOfHorizontalLine + 1);

                indices[stride + j * 6 + 0] = first;
                indices[stride + j * 6 + 1] = second;
                indices[stride + j * 6 + 2] = (short) (first + 1);

                indices[stride + j * 6 + 3] = second;
                indices[stride + j * 6 + 4] = (short) (second + 1);
                indices[stride + j * 6 + 5] = (short) (first + 1);
            }
        }

        vertexInfo.setIndexBuffer(indices);

        vertexInfo.setRenderType(RenderType.DRAW_ELEMENTS);
        vertexInfo.setPrimitiveMode(PrimitiveMode.TRIANGLES);

        return vertexInfo;
    }
}
