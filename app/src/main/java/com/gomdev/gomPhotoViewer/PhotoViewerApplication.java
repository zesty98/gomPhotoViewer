package com.gomdev.gomPhotoViewer;

import android.app.Application;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by gomdev on 15. 5. 20..
 */
public class PhotoViewerApplication extends Application {
    private static final String CLASS = "PhotoViewerApplication";
    private static final String TAG = PhotoViewerConfig.TAG + "_" + CLASS;
    private static final boolean DEBUG = PhotoViewerConfig.DEBUG;

    private NetworkBroadcastReceiver mReceiver = null;
    private ImageCache mImageCache = null;
    private ConcurrentHashMap<String, String> mImageProperties;

    private ImageInfo[] mImageInfos = new ImageInfo[PhotoViewerConfig.MAX_NUM_OF_CONTAINER];
    private int mIndex = 0;
    private int mLoopingCount = 0;

    private String mConsumerKey = null;
    private int mTotalItems = 0;
    private int mTotalPages = 0;
    private int mRequestedPage = 0;
    private int mLastLoadedPage = 0;

    @Override
    public void onCreate() {
        super.onCreate();

        mImageProperties = new ConcurrentHashMap<>();
    }

    public String getImageProperty(String key) {
        return mImageProperties.get(key);
    }

    public void putImageProperty(String key, String value) {
        mImageProperties.put(key, value);
    }

    void clearImageInfo() {
        mIndex = 0;
        mRequestedPage = 1;
        mLoopingCount = 0;

        for (int i = 0; i < PhotoViewerConfig.MAX_NUM_OF_CONTAINER; i++) {
            mImageInfos[i] = null;
        }
    }

    void addImageInfo(ImageInfo imageInfo) {
        if (mIndex >= PhotoViewerConfig.MAX_NUM_OF_CONTAINER) {
            mIndex = 0;
            ++mLoopingCount;
        }

        mImageInfos[mIndex++] = imageInfo;
    }

    ImageInfo getImageInfo(int position) {
        int index = position % PhotoViewerConfig.MAX_NUM_OF_CONTAINER;

        ImageInfo imageInfo = mImageInfos[index];
        if (imageInfo == null) {
            return null;
        }

        return imageInfo;
    }

    void setConsumerKey(String consumerKey) {
        mConsumerKey = consumerKey;
    }

    String getConsumerKey() {
        return mConsumerKey;
    }

    void setTotalItems(int totalItems) {
        mTotalItems = totalItems;
    }

    int getTotalItems() {
        return mTotalItems;
    }

    void setTotalPages(int totalPages) {
        mTotalPages = totalPages;
    }

    int getTotalPages() {
        return mTotalPages;
    }

    void setRequestedPage(int page) {
        mRequestedPage = page;
    }

    int getRequestedPage() {
        return mRequestedPage;
    }

    void setLastLoadedPage(int page) {
        mLastLoadedPage = page;
    }

    int getLastLoadedPage() {
        return mLastLoadedPage;
    }

    void setImageCache(ImageCache imageCache) {
        mImageCache = imageCache;
    }

    ImageCache getImageCache() {
        return mImageCache;
    }
}
