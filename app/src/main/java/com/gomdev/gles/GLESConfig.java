package com.gomdev.gles;

public final class GLESConfig {
    public static final boolean DEBUG = false;
    public static final String TAG = "gomdev";

    public enum Version {
        GLES_10,
        GLES_11,
        GLES_20,
        GLES_30,
        GLES_31
    }

    public static final Version DEFAULT_GLES_VERSION = Version.GLES_20;

    public static final int GL_ATC_RGB_AMD = 0x8C92;
    public static final int GL_ATC_RGBA_EXPLICIT_ALPHA_AMD = 0x8C93;
    public static final int GL_ATC_RGBA_INTERPOLATED_ALPHA_AMD = 0x87EE;
    public static final int GL_ETC1_RGB8_OES = 0x8D64;
    public static final int GL_COMPRESSED_RGBA8_ETC2_EAC = 0x9278;
    public static final int GL_COMPRESSED_RGB8_ETC2 = 0x9274;
    public static final int GL_COMPRESSED_RGB8_PUNCHTHROUGH_ALPHA1_ETC2 = 0x9276;
    public static final int GL_COMPRESSED_RGBA_S3TC_DXT1_EXT = 0x83F1;
    public static final int GL_COMPRESSED_RGBA_S3TC_DXT3_EXT = 0x83F2;
    public static final int GL_COMPRESSED_RGBA_S3TC_DXT5_EXT = 0x83F3;

    public static final int POSITION_LOCATION = 0;
    public static final int TEXCOORD_LOCATION = 1;
    public static final int NORMAL_LOCATION = 2;
    public static final int COLOR_LOCATION = 3;
    public static final int POINTSIZE_LOCATION = 4;

    public static final int NUM_OF_VERTEX_ELEMENT = 3;
    public static final int NUM_OF_VERTEX_ELEMENT_WITH_W = 4;
    public static final int NUM_OF_TEXCOORD_ELEMENT = 2;
    public static final int NUM_OF_NORMAL_ELEMENT = 3;
    public static final int NUM_OF_COLOR_ELEMENT = 4;
    public static final int NUM_OF_INDEX_ELEMENT = 6;

    public static final int SHORT_SIZE_BYTES = 2;
    public static final int FLOAT_SIZE_BYTES = 4;
}
