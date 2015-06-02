package com.gomdev.gomPhotoViewer;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.ListFragment;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

public class MainActivity extends Activity implements LoadingFragment.OnLoadingListener, CategoryDialog.OnCategoryChangeListener, FeatureDialog.OnFeatureChangeListener {
    private static final String CLASS = "MainActivity";
    private static final String TAG = PhotoViewerConfig.TAG + "_" + CLASS;
    private static final boolean DEBUG = PhotoViewerConfig.DEBUG;

    private PhotoViewerApplication mApplication = null;
    private ProgressBar mProgressBar = null;

    private Fragment mLoadingFragment = null;
    private Fragment mListFragment = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (DEBUG) {
            Log.d(TAG, "onCreate()");
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            mLoadingFragment = new LoadingFragment();
            mListFragment = new ImageListFragment();

            getFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, mLoadingFragment)
                    .commit();
        }

        mApplication = (PhotoViewerApplication) getApplication();

        mProgressBar = (ProgressBar) findViewById(R.id.detail_progressbar);
        mProgressBar.bringToFront();
        showProgressBar();
    }

    void showProgressBar() {
        mProgressBar.setVisibility(View.VISIBLE);
        mProgressBar.bringToFront();
    }

    void hideProgressBar() {
        mProgressBar.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void onResume() {
        if (DEBUG) {
            Log.d(TAG, "onResume()");
        }

        super.onResume();

        PhotoViewerUtils.registerReceiver(this);
    }

    @Override
    protected void onPause() {
        if (DEBUG) {
            Log.d(TAG, "onPause()");
        }

        PhotoViewerUtils.unregisterReceiver(this);

        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_image_list, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case R.id.action_category:
                CategoryDialog categoryDialog = new CategoryDialog();
                categoryDialog.show(getFragmentManager(), null);
                return true;
            case R.id.action_feature:
                FeatureDialog featureDialog = new FeatureDialog();
                featureDialog.show(getFragmentManager(), null);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onLoadingSuccess() {
        if (DEBUG) {
            Log.d(TAG, "onLoadingSuccess()");
        }

        hideProgressBar();

        getFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, mListFragment)
                .commit();
    }

    @Override
    public void onLoadingFail() {
        if (DEBUG) {
            Log.d(TAG, "onLoadingFail()");
        }
    }

    @Override
    public void onCategoryChanged() {
        if (DEBUG) {
            Log.d(TAG, "onCategoryChanged()");
        }

        getFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, mLoadingFragment)
                .commit();

        updateActionBarTitle();
    }

    @Override
    public void onFeatureChanged() {
        if (DEBUG) {
            Log.d(TAG, "onFeatureChanged()");
        }

        getFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, mLoadingFragment)
                .commit();

        updateActionBarTitle();
    }

    void updateActionBarTitle() {
        ActionBar actionBar = getActionBar();
        String title = "500px\t" +
                mApplication.getImageProperty(PhotoViewerConfig.FEATURES) +
                " - " +
                mApplication.getImageProperty(PhotoViewerConfig.ONLY);

        actionBar.setTitle(title);
    }
}
