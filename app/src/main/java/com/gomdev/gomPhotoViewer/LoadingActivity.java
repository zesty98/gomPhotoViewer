package com.gomdev.gomPhotoViewer;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import org.json.JSONObject;

import static com.gomdev.gomPhotoViewer.PhotoViewerConfig.CONSUMER_KEY;
import static com.gomdev.gomPhotoViewer.PhotoViewerConfig.DEFAULT_IMAGE_SIZE;
import static com.gomdev.gomPhotoViewer.PhotoViewerConfig.FEATURES;
import static com.gomdev.gomPhotoViewer.PhotoViewerConfig.FEATURES_EDITORS;
import static com.gomdev.gomPhotoViewer.PhotoViewerConfig.IMAGE_SIZES;
import static com.gomdev.gomPhotoViewer.PhotoViewerConfig.NUM_OF_ITEMS_IN_PAGE;
import static com.gomdev.gomPhotoViewer.PhotoViewerConfig.ONLY;
import static com.gomdev.gomPhotoViewer.PhotoViewerConfig.RPP;

/**
 * Created by gomdev on 15. 5. 21..
 */
public class LoadingActivity extends Activity implements URLExecutionTask.Delegate {
    private static final String CLASS = "LoadingActivity";
    private static final String TAG = PhotoViewerConfig.TAG + "_" + CLASS;
    private static final boolean DEBUG = PhotoViewerConfig.DEBUG;

    private PhotoViewerApplication mApplication = null;
    private NetworkBroadcastReceiver mReceiver = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        boolean isConnected = PhotoViewerUtils.isNetworkConnected(this);
        Toast.makeText(this, "Network is connnected!!!", Toast.LENGTH_SHORT).show();

        mApplication = (PhotoViewerApplication) getApplication();

        initImageProperties();

        String consumerKey = getString(R.string.px_consumer_key);
        mApplication.setConsumerKey(consumerKey);

        String urlString = PhotoViewerUtils.makeURL(mApplication, 1);

        new URLExecutionTask(this).execute(urlString);
    }

    private void initImageProperties() {
        String consumerKey = getString(R.string.px_consumer_key);
        mApplication.setConsumerKey(consumerKey);

        String[] categories = getResources().getStringArray(R.array.category);
        String category = categories[PhotoViewerConfig.Category.URBAN_EXPLORATION.getIndex()];

        SharedPreferences pref = getSharedPreferences(PhotoViewerConfig.PREF_NAME, 0);
        String features = pref.getString(PhotoViewerConfig.PREF_FEATURES, null);
        if (features == null) {
            mApplication.putImageProperty(FEATURES, FEATURES_EDITORS);
            mApplication.putImageProperty(RPP, String.valueOf(NUM_OF_ITEMS_IN_PAGE));
            mApplication.putImageProperty(IMAGE_SIZES, DEFAULT_IMAGE_SIZE);
            mApplication.putImageProperty(ONLY, category);
            mApplication.putImageProperty(CONSUMER_KEY, consumerKey);

            SharedPreferences.Editor editor = pref.edit();
            editor.putString(PhotoViewerConfig.PREF_FEATURES, FEATURES_EDITORS);
            editor.putString(PhotoViewerConfig.PREF_RPP, String.valueOf(NUM_OF_ITEMS_IN_PAGE));
            editor.putString(PhotoViewerConfig.PREF_IMAGE_SIZE, DEFAULT_IMAGE_SIZE);
            editor.putString(PhotoViewerConfig.PREF_ONLY, category);
            editor.putString(PhotoViewerConfig.PREF_CONSUMER_KEY, consumerKey);
            editor.commit();
        } else {
            mApplication.putImageProperty(FEATURES, features);

            String rpp = pref.getString(PhotoViewerConfig.PREF_RPP, null);
            mApplication.putImageProperty(RPP, rpp);

            String imageSize = pref.getString(PhotoViewerConfig.PREF_IMAGE_SIZE, null);
            mApplication.putImageProperty(IMAGE_SIZES, imageSize);

            String only = pref.getString(PhotoViewerConfig.PREF_ONLY, null);
            mApplication.putImageProperty(ONLY, only);

            mApplication.putImageProperty(CONSUMER_KEY, consumerKey);
        }
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
    public void onSuccess(JSONObject object) {
        Toast.makeText(this, "Connection success!!!", Toast.LENGTH_SHORT).show();

        int totalItems = object.optInt("total_items");
        int totalPages = object.optInt("total_pages");

        mApplication.setTotalItems(totalItems);
        mApplication.setTotalPages(totalPages);
        mApplication.setRequestedPage(1);

        PhotoViewerUtils.parseJSONObjectToImageInfo(mApplication, object, 1);

        Intent intent = new Intent(this, ImageListActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);

        finish();
    }

    @Override
    public void onFail() {
        Toast.makeText(this, "Network connection fail!!!", Toast.LENGTH_SHORT).show();

        finish();
    }
}
