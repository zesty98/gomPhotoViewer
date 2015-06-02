package com.gomdev.gomPhotoViewer;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

/**
 * Created by gomdev on 15. 5. 21..
 */
public class FeatureDialog extends DialogFragment {
    private static final String CLASS = "FeatureDialog";
    private static final String TAG = PhotoViewerConfig.TAG + "_" + CLASS;
    private static final boolean DEBUG = PhotoViewerConfig.DEBUG;

    private ImageListActivity mActivity = null;
    private int mSelectedIndex = -1;
    private PhotoViewerApplication mApplication = null;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mApplication = (PhotoViewerApplication) getActivity().getApplication();

        final String[] features = getActivity().getResources().getStringArray(R.array.feature);

        String prevFeature = mApplication.getImageProperty(PhotoViewerConfig.FEATURES);
        for (int i = 0; i < features.length; i++) {
            if (prevFeature.compareTo(features[i]) == 0) {
                mSelectedIndex = i;
                break;
            }
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle("Feature")
                .setSingleChoiceItems(R.array.feature, mSelectedIndex, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mSelectedIndex = i;

                        String feature = features[mSelectedIndex];

                        if (feature.compareTo(mApplication.getImageProperty(PhotoViewerConfig.FEATURES)) == 0) {
                            return;
                        }

                        mApplication.clearImageInfo();

                        mApplication.putImageProperty(PhotoViewerConfig.FEATURES, feature);

                        SharedPreferences pref = getActivity().getSharedPreferences(PhotoViewerConfig.PREF_NAME, 0);
                        SharedPreferences.Editor editor = pref.edit();

                        editor.putString(PhotoViewerConfig.PREF_FEATURES, feature);
                        editor.commit();

                        Intent intent = new Intent(getActivity(), LoadingActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        getActivity().startActivity(intent);
                    }
                });
        return builder.create();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        mActivity = (ImageListActivity) activity;
    }
}
