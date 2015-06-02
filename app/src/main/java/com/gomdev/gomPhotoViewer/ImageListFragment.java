package com.gomdev.gomPhotoViewer;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.gomdev.gles.GLESUtils;

import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.LinkedList;

public class ImageListFragment extends Fragment implements ImageDownloader.ImageDownloadListener {
    private static final String CLASS = "ImageListFragment";
    private static final String TAG = PhotoViewerConfig.TAG + "_" + CLASS;
    private static final boolean DEBUG = PhotoViewerConfig.DEBUG;

    private static final int SPACING = 1;   // dp

    private PhotoViewerApplication mApplication = null;

    private ImageCache mImageCache = null;
    private ImageDownloader mImageDownloader = null;
    private PhotoGridAdapter mAdapter = null;
    private GridView mGridView = null;

    private int mNumOfColumns = 3;
    private int mSpacing = 0;
    private int mWidth = 0;

    private volatile boolean mIsBlockLoading = false;

    private int mCurrentLoadingPage = 0;

    private LinkedList<WeakReference<ImageListDownloadTask>> mTasks = new LinkedList<>();

    void cancelAll() {
        for (WeakReference<ImageListDownloadTask> taskRef : mTasks) {
            ImageListDownloadTask task = taskRef.get();
            if (task != null) {
                task.cancel(true);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (DEBUG) {
            Log.d(TAG, "onCreateView()");
        }

        mApplication = (PhotoViewerApplication) getActivity().getApplication();
        mImageCache = ImageCache.getInstance(getFragmentManager(), PhotoViewerConfig.CACHE_SIZE_IN_KB);
        mApplication.setImageCache(mImageCache);
        mImageDownloader = new ImageDownloader(getActivity());

        mImageDownloader.setApplication(mApplication);

        View rootView = inflater.inflate(R.layout.fragment_photolist, container,
                false);

        final Activity activity = getActivity();

        mSpacing = GLESUtils.getPixelFromDpi(activity, SPACING);
        mWidth = activity.getResources().getDisplayMetrics().widthPixels;

        mAdapter = new PhotoGridAdapter(activity);

        mGridView = (GridView) rootView.findViewById(R.id.gridview);
        mGridView.setAdapter(mAdapter);
        mGridView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (mIsBlockLoading == true) {
                    return true;
                }

                return false;
            }
        });

        int columnWidth = (mWidth - mSpacing * (mNumOfColumns + 1)) / mNumOfColumns;

        mGridView.setColumnWidth(columnWidth);
        mAdapter.setItemHeight(columnWidth);

        mGridView.setOnItemClickListener(mOnItemClickListener);

        return rootView;
    }

    @Override
    public void onResume() {
        if (DEBUG) {
            Log.d(TAG, "onResume()");
        }

        super.onResume();
    }

    @Override
    public void onPause() {
        if (DEBUG) {
            Log.d(TAG, "onPause()");
        }

        cancelAll();
        mImageDownloader.cancelAll();

        super.onPause();
    }

    @Override
    public void onImageDownloaded(ImageInfo imageInfo, Bitmap bitmap) {
        if (mImageCache != null) {
            String key = String.valueOf(imageInfo.mID);
            mImageCache.addBitmapToCache(key, new BitmapDrawable(getActivity().getResources(), bitmap));
        }
    }

    public class PhotoGridAdapter extends BaseAdapter {
        static final String CLASS = "PhotoGridAdapter";
        static final String TAG = PhotoViewerConfig.TAG + "_" + CLASS;
        static final boolean DEBUG = PhotoViewerConfig.DEBUG;

        private final LayoutInflater mInflater;

        private final int mActionBarHeight;
        private int mItemHeight = 0;
        private FrameLayout.LayoutParams mImageViewLayoutParams;

        public PhotoGridAdapter(Context context) {
            mInflater = LayoutInflater.from(context);

            mActionBarHeight = GLESUtils.getTitleBarHeight((Activity) context);

            mImageViewLayoutParams = new FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        }

        @Override
        public int getCount() {
            return mApplication.getTotalItems();
        }

        @Override
        public Object getItem(int position) {
            Object object = mApplication.getImageInfo(position);

            return object;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        // create a new ImageView for each item referenced by the Adapter
        public View getView(int position, View convertView, ViewGroup parent) {
            FrameLayout layout;

            if (convertView == null) {
                layout = (FrameLayout) mInflater.inflate(
                        R.layout.grid_item_image,
                        parent, false);
            } else {
                layout = (FrameLayout) convertView;
            }

            ImageView imageView = (ImageView) layout
                    .findViewById(R.id.image);

            imageView.setLayoutParams(mImageViewLayoutParams);

            ImageInfo imageInfo = mApplication.getImageInfo(position);

            if (imageInfo != null) {
                int pageNumber = imageInfo.mPageNumber;

                if (mApplication.getRequestedPage() < (pageNumber + 1)) {
                    loadNextPage(pageNumber + 1);
                }

                String key = String.valueOf(imageInfo.mID);
                BitmapDrawable bitmapDrawable = mImageCache.getBitmapFromMemCache(key);
                if (bitmapDrawable != null) {
                    imageView.setImageBitmap(bitmapDrawable.getBitmap());
                } else {
                    mImageDownloader.download(imageInfo, imageView, ImageListFragment.this);
                }
            }

            if (position + mNumOfColumns < mApplication.getTotalItems()) {
                ImageInfo nextRowImageInfo = mApplication.getImageInfo(position + mNumOfColumns);
                if (nextRowImageInfo == null) {
                    synchronized (this) {
                        mIsBlockLoading = true;
                        ((ImageListActivity) getActivity()).showProgressBar();
                    }
                    mGridView.smoothScrollBy(0, 0);

                    mCurrentLoadingPage = ((position + mNumOfColumns) / PhotoViewerConfig.NUM_OF_ITEMS_IN_PAGE) + 1;
                }
            }

            return layout;
        }

        public void setItemHeight(int height) {
            if (height == mItemHeight) {
                return;
            }
            mItemHeight = height;
            mImageViewLayoutParams =
                    new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, mItemHeight);
            notifyDataSetChanged();
        }
    }

    private void loadNextPage(final int pageNumber) {
        String urlString = PhotoViewerUtils.makeURL(mApplication, pageNumber);

        mApplication.setRequestedPage(pageNumber);

        ImageListDownloadTask task = new ImageListDownloadTask(new URLExecutionTask.Delegate() {

            @Override
            public void onSuccess(JSONObject object) {
                Activity activity = getActivity();
                if (activity != null) {
                    Toast.makeText(activity, "onSuccess() pageNumber=" + pageNumber, Toast.LENGTH_SHORT).show();

                    if (pageNumber == mCurrentLoadingPage) {
                        synchronized (this) {
                            mIsBlockLoading = false;
                            ((ImageListActivity) getActivity()).hideProgressBar();
                        }
                    }
                }
            }

            @Override
            public void onFail() {
                Toast.makeText(getActivity(), "onFail() pageNumber=" + pageNumber, Toast.LENGTH_SHORT).show();
            }
        }, mApplication, pageNumber);
        task.execute(urlString);

        mTasks.add(new WeakReference<>(task));
    }

    private OnItemClickListener mOnItemClickListener = new OnItemClickListener() {
        public void onItemClick(AdapterView<?> parent, View v,
                                int position, long id) {
            Intent intent = new Intent(getActivity(), DetailViewActivity.class);
            intent.putExtra(PhotoViewerConfig.DETAIL_VIEW_POSITION, position);
            startActivity(intent);
        }
    };
}
