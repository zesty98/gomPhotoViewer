package com.gomdev.gomPhotoViewer;

import org.json.JSONObject;

/**
 * Created by gomdev on 15. 5. 22..
 */
public class ImageListDownloadTask extends URLExecutionTask {
    private static final String CLASS = "ImageListDownloadTask";
    private static final String TAG = PhotoViewerConfig.TAG + "_" + CLASS;
    private static final boolean DEBUG = PhotoViewerConfig.DEBUG;

    private final PhotoViewerApplication mApplication;
    private final int mPageNumber;

    public ImageListDownloadTask(Delegate delegate, PhotoViewerApplication application, int pageNumber) {
        super(delegate);

        mApplication = application;
        mPageNumber = pageNumber;
    }

    @Override
    protected JSONObject doInBackground(Object... params) {
        JSONObject object = super.doInBackground(params);

        PhotoViewerUtils.parseJSONObjectToImageInfo(mApplication, object, mPageNumber);

        mApplication.setLastLoadedPage(mPageNumber);

        return object;
    }
}
