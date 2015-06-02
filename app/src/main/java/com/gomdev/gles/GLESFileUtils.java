package com.gomdev.gles;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public final class GLESFileUtils {
    static final String CLASS = "GLESFileUtils";
    static final String TAG = GLESConfig.TAG + "_" + CLASS;
    static final boolean DEBUG = GLESConfig.DEBUG;

    public static boolean isExternalStorageWriable() {
        boolean writable = false;

        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            writable = true;
        } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            writable = false;
        } else {
            writable = false;
        }

        return writable;
    }

    public static boolean write(String path, String str) {
        String parentDirPath = getParentDirectoryPath(path);
        File dir = new File(parentDirPath);
        dir.mkdir();

        File file = new File(path);

        try {
            FileOutputStream fos = new FileOutputStream(file);
            byte[] data = str.getBytes();
            fos.write(data, 0, data.length);
            fos.close();
        } catch (FileNotFoundException e) {
            Log.e(TAG, "write() File is not existed!!!");
            e.printStackTrace();

            return false;
        } catch (IOException e) {
            Log.d(TAG, "write() write fails");
            e.printStackTrace();

            return false;
        }

        return true;
    }

    public static String getParentDirectoryPath(String path) {
        int index = path.lastIndexOf(File.separator);
        String parentDirectory = path.substring(0, index);
        return parentDirectory;
    }

    public static String read(String path) {
        byte[] data = null;

        try {
            FileInputStream fis = new FileInputStream(path);
            int size = fis.available();
            data = new byte[size];
            while (fis.read(data) != -1) {
                ;
            }
            fis.close();
        } catch (FileNotFoundException e) {
            Log.d(TAG, "read() file is not existed!!!");
            e.printStackTrace();

            return null;
        } catch (IOException e) {
            Log.d(TAG, "read() read fails");
            e.printStackTrace();

            return null;
        }

        return new String(data);
    }

    public static boolean isExist(String path) {
        File file = new File(path);
        return file.exists();
    }

    public static void delete(String path) {
        File file = new File(new String(path));
        if (file.exists() == true) {
            file.delete();
        }
    }

    public static boolean saveImageFile(String path, CompressFormat format,
                                        Bitmap bitmap) {
        File file = new File(path);
        FileOutputStream out = null;

        try {
            out = new FileOutputStream(file);

            if (bitmap.compress(format, 100, out)) {
                out.close();
                return false;
            }

            out.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return true;
    }
}
