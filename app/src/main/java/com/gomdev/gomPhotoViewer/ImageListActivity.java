package com.gomdev.gomPhotoViewer;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;


public class ImageListActivity extends Activity {
    private static final String CLASS = "ImageListActivity";
    private static final String TAG = PhotoViewerConfig.TAG + "_" + CLASS;
    private static final boolean DEBUG = PhotoViewerConfig.DEBUG;

    private PhotoViewerApplication mApplication = null;
    private ProgressBar mProgressBar = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mApplication = (PhotoViewerApplication) getApplication();
        mProgressBar = (ProgressBar) findViewById(R.id.detail_progressbar);
        mProgressBar.bringToFront();
        hideProgressBar();

        String title = mApplication.getImageProperty(PhotoViewerConfig.FEATURES) +
                " / " +
                mApplication.getImageProperty(PhotoViewerConfig.ONLY);
        getActionBar().setTitle(title);
    }

    void showProgressBar() {
        mProgressBar.setVisibility(View.VISIBLE);
    }

    void hideProgressBar() {
        mProgressBar.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void onResume() {
        super.onResume();

        PhotoViewerUtils.registerReceiver(this);
    }

    @Override
    protected void onPause() {
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

}
