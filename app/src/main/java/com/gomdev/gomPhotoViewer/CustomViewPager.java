package com.gomdev.gomPhotoViewer;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by gomdev on 15. 5. 28..
 */
public class CustomViewPager extends ViewPager {
    private static final String CLASS = "CustomImageView";
    private static final String TAG = PhotoViewerConfig.TAG + "_" + CLASS;
    private static final boolean DEBUG = PhotoViewerConfig.DEBUG;

    private PhotoViewerApplication mApplication = null;
    private Context mContext = null;

//    private boolean mIsScrollEnabled = true;

    public CustomViewPager(Context context) {
        super(context);

        init(context);
    }

    public CustomViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);

        init(context);
    }

    private void init(Context context) {
        mContext = context;
    }

    @Override
    protected boolean canScroll(View v, boolean checkV, int dx, int x, int y) {
        if (v instanceof CustomImageView) {
            return ((CustomImageView) v).canScroll(-dx);
        }
        return super.canScroll(v, checkV, dx, x, y);
    }
}
