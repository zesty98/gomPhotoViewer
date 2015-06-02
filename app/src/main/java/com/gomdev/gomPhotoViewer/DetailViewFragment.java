package com.gomdev.gomPhotoViewer;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.LinkedList;
import java.util.concurrent.Future;

/**
 * Created by gomdev on 15. 5. 25..
 */
public class DetailViewFragment extends Fragment implements ImageDownloader.ImageDownloadListener {
    private static final String CLASS = "DetailViewFragment";
    private static final String TAG = PhotoViewerConfig.TAG + "_" + CLASS;
    private static final boolean DEBUG = PhotoViewerConfig.DEBUG;

    private static final String IMAGE_DATA_EXTRA = "extra_image_data";

    private static LinkedList<Future<Bitmap>> sFutureMap = new LinkedList<>();

    private ImageInfo mImageInfo;
    private CustomImageView mImageView;
    private ProgressBar mProgressBar;
    private ImageDownloader mImageDownloader = null;

    private volatile boolean mIsBlockLoading = false;

    public static DetailViewFragment newInstance(ImageInfo imageInfo) {
        if (DEBUG) {
            Log.d(TAG, "newInstance() imageInfo=" + imageInfo.mID);
        }

        final DetailViewFragment f = new DetailViewFragment();

        final Bundle args = new Bundle();
        args.putSerializable(IMAGE_DATA_EXTRA, imageInfo);
        f.setArguments(args);

        return f;
    }

    public DetailViewFragment() {
    }

    void setImageDownloader(ImageDownloader imageDownloader) {
        mImageDownloader = imageDownloader;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mImageInfo = getArguments() != null ? (ImageInfo) getArguments().getSerializable(IMAGE_DATA_EXTRA) : null;

        if (DEBUG) {
            Log.d(TAG, "onCreate() imageInfo=" + mImageInfo.mID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (DEBUG) {
            Log.d(TAG, "onCreateView() imageInfo=" + mImageInfo.mID);
        }

        // Inflate and locate the main ImageView
        final View v = inflater.inflate(R.layout.fragment_detail_view, container, false);
        mImageView = (CustomImageView) v.findViewById(R.id.detail);
        mImageView.setTag("");

        mProgressBar = (ProgressBar) v.findViewById(R.id.detail_progressbar);
        mProgressBar.setVisibility(View.VISIBLE);

        blockTouchEventIfDownloadingImageList();

        download();

        downloadImageListIfNeeeded();


        // Pass clicks on the ImageView to the parent activity to handle
        if (View.OnClickListener.class.isInstance(getActivity())) {
            mImageView.setOnClickListener((View.OnClickListener) getActivity());
        }

        return v;
    }

    private void downloadImageListIfNeeeded() {
        PhotoViewerApplication application = (PhotoViewerApplication) getActivity().getApplication();
        int loadedPageNumber = application.getLastLoadedPage();
        int nextPageNumber = mImageInfo.mPageNumber + 1;
        if (nextPageNumber > loadedPageNumber) {
            loadNextPage(nextPageNumber);
            mIsBlockLoading = true;
        }
    }

    private void blockTouchEventIfDownloadingImageList() {
        DetailViewActivity activity = (DetailViewActivity) getActivity();
        ViewPager pager = (ViewPager) activity.findViewById(R.id.pager);
        pager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                int action = motionEvent.getAction();
                if (mIsBlockLoading == true && action == MotionEvent.ACTION_DOWN) {
                    return true;
                }
                return false;
            }
        });
    }

    private void download() {
        if (mImageInfo != null) {
            mImageInfo.mIsDetailView = true;
        }

        Future<Bitmap> future = mImageDownloader.download(mImageInfo, mImageView, this);

        if (future != null) {
            sFutureMap.offer(future);
            if (sFutureMap.size() > 5) {
                Future<Bitmap> f = sFutureMap.poll();
                if (f != null) {
                    f.cancel(true);
                }
            }
        }

        if (mImageInfo != null) {
            mImageInfo.mIsDetailView = false;
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        if (DEBUG) {
            Log.d(TAG, "onActivityCreated() imageInfo=" + mImageInfo.mID);
        }

        super.onActivityCreated(savedInstanceState);

        // Pass clicks on the ImageView to the parent activity to handle
        Activity activity = getActivity();
        if (View.OnClickListener.class.isInstance(activity)) {
            mImageView.setOnClickListener((View.OnClickListener) activity);
        }
    }

    private void loadNextPage(final int pageNumber) {
        PhotoViewerApplication application = (PhotoViewerApplication) getActivity().getApplication();

        String urlString = PhotoViewerUtils.makeURL(application, pageNumber);

        application.setRequestedPage(pageNumber);

        ImageListDownloadTask task = new ImageListDownloadTask(new URLExecutionTask.Delegate() {

            @Override
            public void onSuccess(JSONObject object) {
                Activity activity = getActivity();
                if (activity != null) {
                    Toast.makeText(activity, "onSuccess() pageNumber=" + pageNumber, Toast.LENGTH_SHORT).show();
                    synchronized (this) {
                        mIsBlockLoading = false;
                    }
                }
            }

            @Override
            public void onFail() {
                Log.d(TAG, "onFail()");
            }

        }, application, pageNumber);
        task.execute(urlString);
    }

    @Override
    public void onResume() {
        if (DEBUG) {
            Log.d(TAG, "onResume() imageInfo=" + mImageInfo.mID);
        }

        super.onResume();
    }

    @Override
    public void onPause() {
        if (DEBUG) {
            Log.d(TAG, "onPause() imageInfo=" + mImageInfo.mID);
        }

        super.onPause();
    }

    @Override
    public void onImageDownloaded(ImageInfo imageInfo, Bitmap bitmap) {
        if (mProgressBar != null) {
            mProgressBar.setVisibility(View.INVISIBLE);
        }
    }
}
