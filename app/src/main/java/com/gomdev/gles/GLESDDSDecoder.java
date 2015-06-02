package com.gomdev.gles;

import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public final class GLESDDSDecoder {
    static final String CLASS = "GLESDDSDecoder";
    static final String TAG = GLESConfig.TAG + "_" + CLASS;
    static final boolean DEBUG = GLESConfig.DEBUG;

    public static final int DDS_MAGIC_NUMBER = 0x20534444;  // 'DDS '
    public static final int DDS_MAGIC_NUMBER_SIZE = 4;
    public static final int DDS_HEADER_SIZE = 124;

    public static final int DDS_HEADER_WIDTH_OFFSET = 3 * 4;
    public static final int DDS_HEADER_HEIGHT_OFFSET = 2 * 4;
    public static final int DDS_HEADER_ENCODED_SIZE_OFFSET = 4 * 4;
    public static final int PIXELFORMAT_FLAGS_OFFSET = 19 * 4;
    public static final int PIXELFORMAT_FOURCC_OFFSET = 20 * 4;

    private static final int FOURCC_S3TC_DXT1_RGBA8 = 'D' | ('X' << 8) | ('T' << 16) | ('1' << 24);
    private static final int FOURCC_S3TC_DXT3_RGBA8 = 'D' | ('X' << 8) | ('T' << 16) | ('3' << 24);
    private static final int FOURCC_S3TC_DXT5_RGBA8 = 'D' | ('X' << 8) | ('T' << 16) | ('5' << 24);
    private static final int FOURCC_ATC_RGB8 = 'A' | ('T' << 8) | ('C' << 16) | (' ' << 24);
    private static final int FOURCC_ATC_RGBA8_EXPLICIT = 'A' | ('T' << 8) | ('C' << 16) | ('A' << 24);
    private static final int FOURCC_ATC_RGBA8_INTERPOLATED = 'A' | ('T' << 8) | ('C' << 16) | ('I' << 24);
    private static final int FOURCC_ETC1_RGB8 = 'E' | ('T' << 8) | ('C' << 16) | ('1' << 24);
    private static final int FOURCC_ETC2_RGBA8 = 'E' | ('T' << 8) | ('2' << 16) | ('A' << 24);
    private static final int FOURCC_ETC2_RGB8 = 'E' | ('T' << 8) | ('C' << 16) | ('2' << 24);
    private static final int FOURCC_ETC2_RGB8_A1 = 'E' | ('T' << 8) | ('2' << 16) | ('P' << 24);

    public static GLESCompressedTextureInfo decode(InputStream input) throws IOException {
        int width = 0;
        int height = 0;
        int encodedSize = 0;
        int blockSize = 0;

        GLESCompressedTextureInfo textureInfo = new GLESCompressedTextureInfo();

        byte[] ioBuffer = new byte[4096];
        {
            if (input.read(ioBuffer, 0, DDS_MAGIC_NUMBER_SIZE) != DDS_MAGIC_NUMBER_SIZE) {
                throw new IOException("Unable to read DDS file header.");
            }

            ByteBuffer magicNumBuffer = ByteBuffer.allocateDirect(DDS_MAGIC_NUMBER_SIZE).order(
                    ByteOrder.nativeOrder());
            magicNumBuffer.put(ioBuffer, 0, DDS_MAGIC_NUMBER_SIZE).position(0);

            int magic = magicNumBuffer.getInt(0);
            if (magic != DDS_MAGIC_NUMBER) {
                Log.e(TAG, "This file is not DDS file");
                return null;
            }

            if (input.read(ioBuffer, 0, DDS_HEADER_SIZE) != DDS_HEADER_SIZE) {
                throw new IOException("Unable to read DDS file header.");
            }

            ByteBuffer headerBuffer = ByteBuffer.allocateDirect(DDS_HEADER_SIZE).order(
                    ByteOrder.nativeOrder());
            headerBuffer.put(ioBuffer, 0, DDS_HEADER_SIZE).position(0);

            width = headerBuffer.getInt(DDS_HEADER_WIDTH_OFFSET);
            height = headerBuffer.getInt(DDS_HEADER_HEIGHT_OFFSET);
            encodedSize = headerBuffer.getInt(DDS_HEADER_ENCODED_SIZE_OFFSET);

            textureInfo.setWidth(width);
            textureInfo.setHeight(height);

            int flags = headerBuffer.getInt(PIXELFORMAT_FLAGS_OFFSET);
            int fourcc = headerBuffer.getInt(PIXELFORMAT_FOURCC_OFFSET);

            // qualcommm defect
//            if ((flags & 0x4) != 0) {
            parseFourCC(fourcc, textureInfo);
//            }

            encodedSize = Math.max(1, (width + 3) >> 2) * Math.max(1, (height + 3) >> 2) * textureInfo.getBlockSize();
        }

        ByteBuffer dataBuffer = ByteBuffer.allocateDirect(encodedSize).order(
                ByteOrder.nativeOrder());
        for (int i = 0; i < encodedSize; ) {
            int chunkSize = Math.min(ioBuffer.length, encodedSize - i);
            if (input.read(ioBuffer, 0, chunkSize) != chunkSize) {
                throw new IOException("Unable to read compressed data.");
            }
            dataBuffer.put(ioBuffer, 0, chunkSize);
            i += chunkSize;
        }
        dataBuffer.position(0);

        textureInfo.setData(dataBuffer);

        return textureInfo;
    }

    private static void parseFourCC(int fourcc, GLESCompressedTextureInfo textureInfo) {
        int bytesPerBlock = 0;
        int internalFormat = 0;

        // Compressed.
        switch (fourcc) {
            case FOURCC_S3TC_DXT1_RGBA8:
                internalFormat = GLESConfig.GL_COMPRESSED_RGBA_S3TC_DXT1_EXT;
                bytesPerBlock = 8;
                break;
            case FOURCC_S3TC_DXT3_RGBA8:
                internalFormat = GLESConfig.GL_COMPRESSED_RGBA_S3TC_DXT3_EXT;
                bytesPerBlock = 16;
                break;
            case FOURCC_S3TC_DXT5_RGBA8:
                internalFormat = GLESConfig.GL_COMPRESSED_RGBA_S3TC_DXT5_EXT;
                bytesPerBlock = 16;
                break;
            case FOURCC_ATC_RGB8:
                internalFormat = GLESConfig.GL_ATC_RGB_AMD;
                bytesPerBlock = 8;
                break;
            case FOURCC_ATC_RGBA8_EXPLICIT:
                internalFormat = GLESConfig.GL_ATC_RGBA_EXPLICIT_ALPHA_AMD;
                bytesPerBlock = 16;
                break;
            case FOURCC_ATC_RGBA8_INTERPOLATED:
                internalFormat = GLESConfig.GL_ATC_RGBA_INTERPOLATED_ALPHA_AMD;
                bytesPerBlock = 16;
                break;
            case FOURCC_ETC1_RGB8:
                internalFormat = GLESConfig.GL_ETC1_RGB8_OES;
                bytesPerBlock = 8;
                break;
            case FOURCC_ETC2_RGBA8:
                internalFormat = GLESConfig.GL_COMPRESSED_RGBA8_ETC2_EAC;
                bytesPerBlock = 16;
                break;
            case FOURCC_ETC2_RGB8:
                internalFormat = GLESConfig.GL_COMPRESSED_RGB8_ETC2;
                bytesPerBlock = 8;
                break;
            case FOURCC_ETC2_RGB8_A1:
                internalFormat = GLESConfig.GL_COMPRESSED_RGB8_PUNCHTHROUGH_ALPHA1_ETC2;
                bytesPerBlock = 8;
                break;
            default:
        }

        textureInfo.setInternalFormat(internalFormat);
        textureInfo.setBlockSize(bytesPerBlock);

        if (DEBUG) {
            char first = (char) (fourcc & 0xFF);
            char second = (char) ((fourcc & 0xFF00) >> 8);
            char third = (char) ((fourcc & 0xFF0000) >> 16);
            char forth = (char) ((fourcc & 0xFF000000) >> 24);

            Log.d(TAG, "parseFourCC() first=" + String.valueOf(first) +
                    " second=" + String.valueOf(second) +
                    " third=" + String.valueOf(third) +
                    " forth=" + String.valueOf(forth));
        }
    }
}