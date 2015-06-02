package com.gomdev.gomPhotoViewer;

import java.io.Serializable;

/**
 * Created by gomdev on 15. 5. 18..
 */
public class ImageInfo implements Serializable {
    int mID = -1;
    String mName = null;
    int mWidth = 0;
    int mHeight = 0;
    int mPageNumber = 1;

    boolean mIsDetailView = false;

    int[] mRequestSize = new int[2];
    String[] mImageUrl = new String[2];
}
