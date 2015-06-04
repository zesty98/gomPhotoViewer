package com.gomdev.gomPhotoViewer;

import android.content.Context;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import static com.gomdev.gomPhotoViewer.PhotoViewerConfig.CONSUMER_KEY;
import static com.gomdev.gomPhotoViewer.PhotoViewerConfig.FEATURES;
import static com.gomdev.gomPhotoViewer.PhotoViewerConfig.IMAGE_SIZES;
import static com.gomdev.gomPhotoViewer.PhotoViewerConfig.ONLY;
import static com.gomdev.gomPhotoViewer.PhotoViewerConfig.PAGE;
import static com.gomdev.gomPhotoViewer.PhotoViewerConfig.RPP;

/**
 * Created by gomdev on 15. 5. 20..
 */
public class PhotoViewerUtils {
    private static final String CLASS = "PhotoViewerUtils";
    private static final String TAG = PhotoViewerConfig.TAG + "_" + CLASS;
    private static final boolean DEBUG = PhotoViewerConfig.DEBUG;

    private static NetworkBroadcastReceiver sBroadcastReceiver = new NetworkBroadcastReceiver();

    private PhotoViewerUtils() {

    }

    static boolean isNetworkConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

        if (activeNetworkInfo != null && activeNetworkInfo.isConnected() == true) {
            return true;
        }

        return false;
    }

    static void registerReceiver(Context context) {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        intentFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        context.registerReceiver(sBroadcastReceiver, intentFilter);
    }

    static void unregisterReceiver(Context context) {
        context.unregisterReceiver(sBroadcastReceiver);
    }

    static String makeURL(PhotoViewerApplication application, int pageNumber) {
        String urlString = new StringBuilder(PhotoViewerConfig.HOST)
                .append("/photos?")
                .append(FEATURES)
                .append("=")
                .append(application.getImageProperty(FEATURES))
                .append("&")
                .append(ONLY)
                .append("=")
                .append(application.getImageProperty(ONLY))
                .append("&")
                .append(RPP)
                .append("=")
                .append(application.getImageProperty(RPP))
                .append("&")
                .append(IMAGE_SIZES)
                .append("=")
                .append(3)
                .append("&")
                .append(IMAGE_SIZES)
                .append("=")
                .append(application.getImageProperty(IMAGE_SIZES))
                .append("&")
                .append(PAGE)
                .append("=")
                .append(String.valueOf(pageNumber))
                .append("&")
                .append(CONSUMER_KEY)
                .append("=")
                .append(application.getImageProperty(CONSUMER_KEY))
                .toString();

        Log.d(TAG, "makeURL() url=" + urlString);
        return urlString;
    }

    static void parseJSONObjectToImageInfo(PhotoViewerApplication application, JSONObject object, int pageNumber) {
        JSONArray photos = object.optJSONArray("photos");

        for (int i = 0; i < photos.length(); i++) {
            JSONObject photo = photos.optJSONObject(i);

            ImageInfo imageInfo = new ImageInfo();

            imageInfo.mID = photo.optInt("id");
            imageInfo.mName = photo.optString("name");
            imageInfo.mWidth = photo.optInt("width");
            imageInfo.mHeight = photo.optInt("height");

            JSONArray images = photo.optJSONArray("images");

            JSONObject image = images.optJSONObject(0);
            imageInfo.mRequestSize[0] = image.optInt("size");
            imageInfo.mImageUrl[0] = image.optString("url");

            JSONObject image2 = images.optJSONObject(1);
            imageInfo.mRequestSize[1] = image2.optInt("size");
            imageInfo.mImageUrl[1] = image2.optString("url");

            imageInfo.mPageNumber = pageNumber;

            application.addImageInfo(imageInfo);
        }
    }
}
