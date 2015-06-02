package com.gomdev.gomPhotoViewer;

import android.app.ActionBar;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

public class DetailViewActivity extends FragmentActivity implements OnClickListener {
    private static final String CLASS = "DetailViewActivity";
    private static final String TAG = PhotoViewerConfig.TAG + "_" + CLASS;
    private static final boolean DEBUG = PhotoViewerConfig.DEBUG;

    private PhotoViewerApplication mApplication = null;
    private ImageDownloader mImageDownloader = null;

    private ImagePagerAdapter mAdapter;
    private CustomViewPager mPager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_detail_view);

        mApplication = (PhotoViewerApplication) getApplication();
        int numOfItems = mApplication.getTotalItems();

        mImageDownloader = new ImageDownloader(this);
        mImageDownloader.setApplication((PhotoViewerApplication) getApplication());

        // Set up ViewPager and backing adapter
        mAdapter = new ImagePagerAdapter(getSupportFragmentManager(), numOfItems);
        mPager = (CustomViewPager) findViewById(R.id.pager);
        mPager.setAdapter(mAdapter);
        // Page 간 간격을 설정한다.
        mPager.setPageMargin((int) getResources().getDimension(R.dimen.horizontal_page_margin));
        // 미리 준비하는 Page의 갯수
        // default는 1이다.
        mPager.setOffscreenPageLimit(2);

        // Set up activity to go full screen
//        getWindow().addFlags(LayoutParams.FLAG_FULLSCREEN);

        // Enable some additional newer visibility and ActionBar features to create a more
        // immersive photo viewing experience


        final ActionBar actionBar = getActionBar();
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        mPager.setOnSystemUiVisibilityChangeListener(
                new View.OnSystemUiVisibilityChangeListener() {
                    @Override
                    public void onSystemUiVisibilityChange(int vis) {
                        if ((vis & View.SYSTEM_UI_FLAG_LOW_PROFILE) != 0) {
                            actionBar.hide();
                        } else {
                            actionBar.show();
                        }
                    }
                });

        mPager.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
        actionBar.hide();

        mPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);

                ImageInfo imageInfo = mApplication.getImageInfo(position);
                actionBar.setTitle(imageInfo.mName);
            }
        });

        // Set the current item based on the extra passed in to this activity
        final int extraCurrentItem = getIntent().getIntExtra(PhotoViewerConfig.DETAIL_VIEW_POSITION, -1);
        if (extraCurrentItem != -1) {
            mPager.setCurrentItem(extraCurrentItem);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        PhotoViewerUtils.registerReceiver(this);
    }

    @Override
    protected void onPause() {
        PhotoViewerUtils.unregisterReceiver(this);
        mImageDownloader.cancelAll();

        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private class ImagePagerAdapter extends FragmentStatePagerAdapter {
        private final int mSize;

        public ImagePagerAdapter(FragmentManager fm, int size) {
            super(fm);
            mSize = size;
        }

        @Override
        public int getCount() {
            return mSize;
        }

        @Override
        public Fragment getItem(int position) {
            ImageInfo imageInfo = mApplication.getImageInfo(position);
            DetailViewFragment fragment = DetailViewFragment.newInstance(imageInfo);
            fragment.setImageDownloader(mImageDownloader);
            return fragment;
        }
    }

    @Override
    public void onClick(View v) {
        Log.d(TAG, "onClick() v=" + v);
        final int vis = mPager.getSystemUiVisibility();
        if ((vis & View.SYSTEM_UI_FLAG_LOW_PROFILE) != 0) {
            mPager.setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
        } else {
            mPager.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
        }
    }
}
