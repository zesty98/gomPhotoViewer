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
public class CategoryDialog extends DialogFragment {
    private static final String CLASS = "CategoryDialog";
    private static final String TAG = PhotoViewerConfig.TAG + "_" + CLASS;
    private static final boolean DEBUG = PhotoViewerConfig.DEBUG;

    private ImageListActivity mActivity = null;
    private int mSelectedIndex = -1;
    private PhotoViewerApplication mApplication = null;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mApplication = (PhotoViewerApplication) getActivity().getApplication();

        final String[] categories = getActivity().getResources().getStringArray(R.array.category);

        String prevCategory = mApplication.getImageProperty(PhotoViewerConfig.ONLY);
        for (int i = 0; i < categories.length; i++) {
            if (prevCategory.compareTo(categories[i]) == 0) {
                mSelectedIndex = i;
                break;
            }
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle("Category")
                .setSingleChoiceItems(R.array.category, mSelectedIndex, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mSelectedIndex = i;

                        String category = categories[mSelectedIndex];

                        if (category.compareTo(mApplication.getImageProperty(PhotoViewerConfig.ONLY)) == 0) {
                            return;
                        }

                        mApplication.clearImageInfo();

                        mApplication.putImageProperty(PhotoViewerConfig.ONLY, category);

                        SharedPreferences pref = getActivity().getSharedPreferences(PhotoViewerConfig.PREF_NAME, 0);
                        SharedPreferences.Editor editor = pref.edit();

                        editor.putString(PhotoViewerConfig.PREF_ONLY, category);
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
