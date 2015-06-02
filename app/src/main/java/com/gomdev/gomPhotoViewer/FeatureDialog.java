package com.gomdev.gomPhotoViewer;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;

/**
 * Created by gomdev on 15. 5. 21..
 */
public class FeatureDialog extends DialogFragment {
    private static final String CLASS = "FeatureDialog";
    private static final String TAG = PhotoViewerConfig.TAG + "_" + CLASS;
    private static final boolean DEBUG = PhotoViewerConfig.DEBUG;

    interface OnFeatureChangeListener {
        public void onFeatureChanged();
    }

    private MainActivity mActivity = null;
    private OnFeatureChangeListener mListener = null;
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

                        if (mListener != null) {
                            mListener.onFeatureChanged();
                        }

                        dismiss();
                    }
                });
        return builder.create();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        mActivity = (MainActivity) activity;

        try {
            mListener = (OnFeatureChangeListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnFeatureChangeListener");
        }
    }
}
