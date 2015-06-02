package com.gomdev.gles;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Window;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

public final class GLESUtils {
    static final String CLASS = "GLESUtils";
    static final String TAG = GLESConfig.TAG + "_" + CLASS;
    static final boolean DEBUG = GLESConfig.DEBUG;

    private static final int NUM_OF_FRAME = 16;

    private static float sDpiConvertUnit = 0f;

    private static int sFrameCount = 0;
    private static long sStartTick = -1L;
    private static long sTotalTime = 0L;
    private static float sFPS = 0f;

    private static float sWidthPixels = 0f;
    private static float sHeightPixels = 0f;
    private static int sTitleBarHeight = 0;
    private static int sStatusBarHeight = 0;

    public static Bitmap checkAndReplaceBitmap(Bitmap bitmap) {
        if (bitmap == null) {
            Log.e(TAG,
                    "checkAndReplaceBitmap() Bitmap is null. Replace with transparent bitmap");
            bitmap = makeBitmap(16, 16, Bitmap.Config.ARGB_8888, 0);
        }
        return bitmap;
    }

    public static void checkFPS() {
        float fps = 0.0f;
        long currentTick = 0L;

        if (sStartTick < 0L) {
            sStartTick = System.nanoTime();
            sFrameCount = 0;
        } else {
            ++sFrameCount;

            if (sFrameCount >= NUM_OF_FRAME) {
                currentTick = System.nanoTime();
                sTotalTime = currentTick - sStartTick;
                fps = (float) sFrameCount * 1000000000 / sTotalTime;

                Log.d(TAG, "checkFPS() fps=" + fps);

                sFrameCount = 0;
                sStartTick = currentTick;
                sTotalTime = 0L;
            }
        }
    }

    public static float getFPS() {
        long currentTick = 0L;

        if (sStartTick < 0L) {
            sStartTick = System.nanoTime();
            sFrameCount = 0;
        } else {
            ++sFrameCount;

            if (sFrameCount >= NUM_OF_FRAME) {
                currentTick = System.nanoTime();
                sTotalTime = currentTick - sStartTick;
                sFPS = (float) sFrameCount * 1000000000 / sTotalTime;

                sFrameCount = 0;
                sStartTick = currentTick;
                sTotalTime = 0L;
            }
        }

        return sFPS;
    }

    public static FloatBuffer makeFloatBuffer(float[] array) {
        FloatBuffer buffer = ByteBuffer
                .allocateDirect(array.length * GLESConfig.FLOAT_SIZE_BYTES)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        buffer.put(array).position(0);

        return buffer;
    }

    public static ShortBuffer makeShortBuffer(short[] array) {
        ShortBuffer buffer = ByteBuffer
                .allocateDirect(array.length * GLESConfig.SHORT_SIZE_BYTES)
                .order(ByteOrder.nativeOrder()).asShortBuffer();
        buffer.put(array).position(0);

        return buffer;
    }

    public static float getWidthPixels(Context context) {
        if (Float.compare(sWidthPixels, 0.0F) == 0) {
            DisplayMetrics metric = context.getResources().getDisplayMetrics();
            sWidthPixels = metric.widthPixels;
            sHeightPixels = metric.heightPixels;
        }
        return sWidthPixels;
    }

    public static float getHeightPixels(Context context) {
        if (Float.compare(sHeightPixels, 0.0F) == 0) {
            DisplayMetrics metric = context.getResources().getDisplayMetrics();
            sWidthPixels = metric.widthPixels;
            sHeightPixels = metric.heightPixels;
        }
        return sHeightPixels;
    }

    public static int getPixelFromDpi(Context context, float dpi) {
        if (Float.compare(sDpiConvertUnit, 0.0F) == 0) {
            sDpiConvertUnit = context.getResources().getDisplayMetrics().densityDpi / 160.0F;
        }
        return (int) (dpi * sDpiConvertUnit);
    }

    public static float getDpiFromPixel(Context context, int pixel) {
        if (Float.compare(sDpiConvertUnit, 0.0F) == 0) {
            sDpiConvertUnit = context.getResources().getDisplayMetrics().densityDpi / 160.0F;
        }

        return (float) pixel / sDpiConvertUnit;
    }

    public static float getPixelsFromPercentage(float paramFloat1,
                                                float paramFloat2) {
        return paramFloat2 * (0.01F * paramFloat1);
    }

    public static Bitmap makeBitmap(int width, int height, Config config,
                                    int color) {
        Bitmap bitmap = Bitmap.createBitmap(width, height, config);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(color);
        canvas.drawBitmap(bitmap, 0.0F, 0.0F, null);
        return bitmap;
    }

    public static Bitmap makeCheckerboard(int width, int height, int unitSize) {
        int[] data = new int[width * height];
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if ((j & unitSize) == (i & unitSize)) {
                    data[i * width + j] = Color.BLACK;
                } else {
                    data[i * width + j] = Color.WHITE;
                }
            }
        }

        Bitmap bitmap = Bitmap.createBitmap(data, width, height,
                Config.ARGB_8888);

        return bitmap;
    }

    public static String getAppDataPathName(Context context) {
        StringBuilder builder = new StringBuilder(Environment
                .getDataDirectory().getAbsolutePath());
        builder.append(File.separatorChar);
        builder.append("data");
        builder.append(File.separatorChar);
        builder.append(context.getPackageName());
        builder.append(File.separatorChar);
        return builder.toString();
    }

    public static String getAppVersionName(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);

            return packageInfo.versionName;
        } catch (NameNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return null;
    }

    public static String getShaderBinaryFilePath(Context context, String prefix) {
        String appDataPath = GLESUtils.getAppDataPathName(context);
        String versionName = GLESUtils.getAppVersionName(context);
        String path = appDataPath + prefix + "_" + versionName + ".dat";

        return path;
    }

    public static String getStringFromReosurce(Context context, int resourceID) {
        byte[] data;
        int strLength;
        String str = null;
        Resources res = context.getResources();
        InputStream is = res.openRawResource(resourceID);
        try {
            try {
                data = new byte[1024];
                strLength = 0;
                while (true) {
                    int bytesLeft = data.length - strLength;
                    if (bytesLeft == 0) {
                        byte[] buf2 = new byte[data.length * 2];
                        System.arraycopy(data, 0, buf2, 0, data.length);
                        data = buf2;
                        bytesLeft = data.length - strLength;
                    }
                    int bytesRead = is.read(data, strLength, bytesLeft);
                    if (bytesRead <= 0) {
                        break;
                    }
                    strLength += bytesRead;
                }
            } finally {
                is.close();
            }
        } catch (IOException e) {
            throw new Resources.NotFoundException();
        }

        try {
            str = new String(data, 0, strLength, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            Log.e(TAG, "Could not decode shader string");
        }

        return str;
    }

    public static void generateMipamp(GLESBitmapInfo src) {
        int level = 1;

        int width = src.mWidth;
        int height = src.mHeight;
        int newWidth;
        int newHeight;

        GLESBitmapInfo prevMipmap = src;
        GLESBitmapInfo nextMipmap = new GLESBitmapInfo();

        while (width > 1 && height > 1) {
            generateNextMipMap(prevMipmap, nextMipmap);

            newWidth = nextMipmap.mWidth;
            newHeight = nextMipmap.mHeight;

            Bitmap bitmap = Bitmap.createBitmap(nextMipmap.mData,
                    newWidth, newHeight, Config.ARGB_8888);
            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, level, bitmap, 0);
            bitmap.recycle();

            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                    GLES20.GL_TEXTURE_MIN_FILTER,
                    GLES20.GL_LINEAR_MIPMAP_LINEAR);

            level++;

            prevMipmap.mWidth = newWidth;
            prevMipmap.mHeight = newHeight;
            prevMipmap.mData = nextMipmap.mData;

            width = newWidth;
            height = newHeight;
        }
    }

    private static void generateNextMipMap(GLESBitmapInfo src,
                                           GLESBitmapInfo dst) {
        int x, y;

        int dstWidth;
        int dstHeight;
        int[] dstData;

        int srcWidth = src.mWidth;
        int srcHeight = src.mHeight;
        int[] srcData = src.mData;

        dstWidth = srcWidth / 2;

        if (dstWidth <= 0) {
            dstWidth = 1;
        }

        dstHeight = srcHeight / 2;

        if (dstHeight <= 0) {
            dstHeight = 1;
        }

        dstData = new int[dstWidth * dstHeight];

        int[] srcIndex = new int[4];
        byte r = 0;
        byte g = 0;
        byte b = 0;
        int sample;
        int dstColor;
        int srcColor;
        for (y = 0; y < dstHeight; y++) {
            for (x = 0; x < dstWidth; x++) {
                r = 0;
                g = 0;
                b = 0;

                // Compute the offsets for 2x2 grid of pixels in previous
                // image to perform box filter
                srcIndex[0] = (((y * 2) * srcWidth) + (x * 2));
                srcIndex[1] = (((y * 2) * srcWidth) + (x * 2 + 1));
                srcIndex[2] = ((((y * 2) + 1) * srcWidth) + (x * 2));
                srcIndex[3] = ((((y * 2) + 1) * srcWidth) + (x * 2 + 1));

                // Sum all pixels
                for (sample = 0; sample < 4; sample++) {
                    srcColor = srcData[srcIndex[sample]];
                    r += (srcColor & 0x00FF0000 >> 16);
                    g += (srcColor & 0x0000FF00 >> 8);
                    b += (srcColor & 0x000000FF);
                }

                // Average results
                r /= 4;
                g /= 4;
                b /= 4;

                dstColor = 0xFF000000 | (r << 16) | (g << 8) | b;

                // Store resulting pixels
                dstData[(y * dstWidth + x)] = dstColor;
            }
        }

        dst.mWidth = dstWidth;
        dst.mHeight = dstHeight;
        dst.mData = dstData;
    }

    public static int getTitleBarHeight(Activity activity) {
        if (sTitleBarHeight == 0) {
            calcTitleBarAndStatusBarHeight(activity);
        }

        return sTitleBarHeight;
    }

    public static int getStatusBarHeight(Activity activity) {
        if (sStatusBarHeight == 0) {
            calcTitleBarAndStatusBarHeight(activity);
        }

        return sStatusBarHeight;
    }

    private static void calcTitleBarAndStatusBarHeight(Activity activity) {
        Rect rectgle = new Rect();
        Window window = activity.getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(rectgle);
        sStatusBarHeight = rectgle.top;
        int contentViewTop = window.findViewById(Window.ID_ANDROID_CONTENT)
                .getTop();
        sTitleBarHeight = contentViewTop - sStatusBarHeight;
    }

    public static GLESConfig.Version getGLESVersion(Context context) {
        ActivityManager am = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);

        ConfigurationInfo info = am.getDeviceConfigurationInfo();

        if (info.reqGlEsVersion >= 0x31000) {
            return GLESConfig.Version.GLES_31;
        } else if (info.reqGlEsVersion >= 0x30000) {
            return GLESConfig.Version.GLES_30;
        } else if (info.reqGlEsVersion >= 0x20000) {
            return GLESConfig.Version.GLES_20;
        } else if (info.reqGlEsVersion >= 0x10000) {
            return GLESConfig.Version.GLES_10;
        }
        return GLESConfig.Version.GLES_10;
    }

    public static boolean checkGLESExtension(String extension) {
        String extensions = " " + GLES20.glGetString(GLES20.GL_EXTENSIONS) + " ";
        return extensions.contains(extension);
    }

    public static boolean checkGLError(String prefix) {
        int error = GLES20.glGetError();
        if (error != GLES20.GL_NO_ERROR) {
            Log.d(TAG, "checkGLError() " + prefix + " 0x" + Integer.toHexString(error));
            return false;
        }

        return true;
    }

    public static Bitmap drawTextToBitmap(int x, int y, String str, Paint paint, Bitmap bitmap) {
        Canvas canvas = new Canvas(bitmap);
        bitmap.eraseColor(0xFFFFFFFF);

        canvas.drawText(str, x, y, paint);

        return bitmap;
    }

    public static Bitmap drawTextToBitmap(int x, int y, String str, Paint paint, Bitmap bitmap, boolean needToErase) {
        Canvas canvas = new Canvas(bitmap);

        if (needToErase == true) {
            bitmap.eraseColor(0xFFFFFFFF);
        }

        canvas.drawText(str, x, y, paint);

        return bitmap;
    }

    public static float[] makePositionCoord(float left, float top, float width, float height) {
        float right = left + width;
        float bottom = top - height;

        float[] vertex = {
                left, bottom, 0f,
                right, bottom, 0f,
                left, top, 0f,
                right, top, 0f
        };

        return vertex;
    }

    public static float[] makeTexCoord(float minS, float minT, float maxS, float maxT) {
        float[] texCoord = new float[]{
                minS, maxT,
                maxS, maxT,
                minS, minT,
                maxS, minT
        };

        return texCoord;
    }
}