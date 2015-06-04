package com.gomdev.gomPhotoViewer;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.json.JSONObject;

import static com.gomdev.gomPhotoViewer.PhotoViewerConfig.CONSUMER_KEY;
import static com.gomdev.gomPhotoViewer.PhotoViewerConfig.DEFAULT_IMAGE_SIZE;
import static com.gomdev.gomPhotoViewer.PhotoViewerConfig.FEATURES;
import static com.gomdev.gomPhotoViewer.PhotoViewerConfig.FEATURES_EDITORS;
import static com.gomdev.gomPhotoViewer.PhotoViewerConfig.IMAGE_SIZES;
import static com.gomdev.gomPhotoViewer.PhotoViewerConfig.NUM_OF_ITEMS_IN_PAGE;
import static com.gomdev.gomPhotoViewer.PhotoViewerConfig.ONLY;
import static com.gomdev.gomPhotoViewer.PhotoViewerConfig.PREF_VERSION_NAME;
import static com.gomdev.gomPhotoViewer.PhotoViewerConfig.RPP;

/**
 * Created by gomdev on 15. 6. 2..
 */
public class LoadingFragment extends Fragment implements URLExecutionTask.Delegate {
    private static final String CLASS = "LoadingFragment";
    private static final String TAG = PhotoViewerConfig.TAG + "_" + CLASS;
    private static final boolean DEBUG = PhotoViewerConfig.DEBUG;

    interface OnLoadingListener {
        public void onLoadingSuccess();

        public void onLoadingFail();
    }

    private PhotoViewerApplication mApplication = null;
    private OnLoadingListener mListener = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (DEBUG) {
            Log.d(TAG, "onCreate()");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (DEBUG) {
            Log.d(TAG, "onCreateView()");
        }
        View view = inflater.inflate(R.layout.fragment_loading, container, false);

        Activity activity = getActivity();

        boolean isConnected = PhotoViewerUtils.isNetworkConnected(activity);
        Toast.makeText(activity, "Network is connnected!!!", Toast.LENGTH_SHORT).show();

        mApplication = (PhotoViewerApplication) activity.getApplication();

        initImageProperties();

        String consumerKey = getString(R.string.px_consumer_key);
        mApplication.setConsumerKey(consumerKey);

        String urlString = PhotoViewerUtils.makeURL(mApplication, 1);

        new URLExecutionTask(this).execute(urlString);

        return view;
    }

    private void initImageProperties() {
        String consumerKey = getString(R.string.px_consumer_key);
        mApplication.setConsumerKey(consumerKey);

        Activity activity = getActivity();

        String[] categories = getResources().getStringArray(R.array.category);
        String category = categories[PhotoViewerConfig.Category.URBAN_EXPLORATION.getIndex()];

        PackageInfo pinfo = null;
        String versionName = null;
        try {
            pinfo = activity.getPackageManager().getPackageInfo(activity.getPackageName(), 0);
            versionName = pinfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();

            String msg = "initImageProperties() package name is not found!!!";
            Log.e(TAG, msg);
            Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show();
        }

        SharedPreferences pref = activity.getSharedPreferences(PhotoViewerConfig.PREF_NAME, 0);
        String features = pref.getString(PhotoViewerConfig.PREF_FEATURES, null);
        String versionNameInPref = pref.getString(PhotoViewerConfig.PREF_VERSION_NAME, null);
        if (features == null || versionNameInPref == null || (versionNameInPref.compareTo(versionName) != 0)) {
            mApplication.putImageProperty(FEATURES, FEATURES_EDITORS);
            mApplication.putImageProperty(RPP, String.valueOf(NUM_OF_ITEMS_IN_PAGE));
            mApplication.putImageProperty(IMAGE_SIZES, DEFAULT_IMAGE_SIZE);
            mApplication.putImageProperty(ONLY, category);
            mApplication.putImageProperty(CONSUMER_KEY, consumerKey);

            SharedPreferences.Editor editor = pref.edit();
            editor.putString(PhotoViewerConfig.PREF_VERSION_NAME, versionName);
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

        ((MainActivity)activity).updateActionBarTitle();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnLoadingListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnLoadingListener");
        }
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

        super.onPause();
    }

    @Override
    public void onSuccess(JSONObject object) {
        Toast.makeText(getActivity(), "Connection success!!!", Toast.LENGTH_SHORT).show();

        int totalItems = object.optInt("total_items");
        int totalPages = object.optInt("total_pages");

        mApplication.setTotalItems(totalItems);
        mApplication.setTotalPages(totalPages);
        mApplication.setRequestedPage(1);

        PhotoViewerUtils.parseJSONObjectToImageInfo(mApplication, object, 1);

        if (mListener != null) {
            mListener.onLoadingSuccess();
        }
    }

    @Override
    public void onFail() {
        Toast.makeText(getActivity(), "Network connection fail!!!", Toast.LENGTH_SHORT).show();

        if (mListener != null) {
            mListener.onLoadingFail();
        }
    }
}
