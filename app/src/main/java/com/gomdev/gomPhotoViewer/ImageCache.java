package com.gomdev.gomPhotoViewer;

import android.annotation.TargetApi;
import android.app.Fragment;
import android.app.FragmentManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.LruCache;

class ImageCache {
    static final String CLASS = "ImageCache";
    static final String TAG = PhotoViewerConfig.TAG + "_" + CLASS;
    static final boolean DEBUG = PhotoViewerConfig.DEBUG;

    // Constants to easily toggle various caches
    private LruCache<String, BitmapDrawable> mMemoryCache;

    private int mCacheSize = 0;

    private ImageCache(int cacheSize) {
        mCacheSize = cacheSize;

        initMemoryCache();
    }

    static ImageCache getInstance(FragmentManager fragmentManager, int cacheSize) {

        // Search for, or create an instance of the non-UI RetainFragment
        final RetainFragment mRetainFragment = findOrCreateRetainFragment(fragmentManager);

        // See if we already have an ImageCache stored in RetainFragment
        ImageCache imageCache = (ImageCache) mRetainFragment.getObject();

        // No existing ImageCache, create one and store it in RetainFragment
        if (imageCache == null) {
            imageCache = new ImageCache(cacheSize);
            mRetainFragment.setObject(imageCache);
        }

        return imageCache;
    }

    /**
     * Locate an existing instance of this Fragment or if not found, create and
     * add it using FragmentManager.
     *
     * @param fm The FragmentManager manager to use.
     * @return The existing instance of the Fragment or the new instance if just
     * created.
     */
    private static RetainFragment findOrCreateRetainFragment(FragmentManager fm) {
        //BEGIN_INCLUDE(find_create_retain_fragment)
        // Check to see if we have retained the worker fragment.
        RetainFragment mRetainFragment = (RetainFragment) fm.findFragmentByTag(TAG);

        // If not retained (or first time running), we need to create and add it.
        if (mRetainFragment == null) {
            mRetainFragment = new RetainFragment();
            fm.beginTransaction().add(mRetainFragment, TAG).commitAllowingStateLoss();
        }

        return mRetainFragment;
        //END_INCLUDE(find_create_retain_fragment)
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private static int getBitmapSize(BitmapDrawable value) {
        Bitmap bitmap = value.getBitmap();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            return bitmap.getAllocationByteCount();
        }

        return bitmap.getByteCount();
    }

    private void initMemoryCache() {
        mMemoryCache = new LruCache<String, BitmapDrawable>(mCacheSize) {
            @Override
            protected int sizeOf(String key, BitmapDrawable value) {
                return getBitmapSize(value) / 1024;
            }

            @Override
            protected void entryRemoved(boolean evicted, String key, BitmapDrawable oldValue, BitmapDrawable newValue) {
                super.entryRemoved(evicted, key, oldValue, newValue);

                Bitmap bitmap = oldValue.getBitmap();
                if (bitmap != null) {
//                    bitmap.recycle();
                }
            }
        };
    }


    void addBitmapToCache(String key, BitmapDrawable value) {
        if (key == null || value == null) {
            return;
        }

        if (mMemoryCache != null) {
            mMemoryCache.put(key, value);
        }
    }

    BitmapDrawable getBitmapFromMemCache(String key) {
        BitmapDrawable value = null;

        if (mMemoryCache != null) {
            value = mMemoryCache.get(key);
        }

        return value;
    }

    /**
     * A simple non-UI Fragment that stores a single Object and is retained over configuration
     * changes. It will be used to retain the ImageCache object.
     */
    public static class RetainFragment extends Fragment {
        private Object mObject;

        /**
         * Empty constructor as per the Fragment documentation
         */
        public RetainFragment() {
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            // Make sure this Fragment is retained over a configuration change
            setRetainInstance(true);
        }

        /**
         * Get the stored object.
         *
         * @return The stored object
         */
        public Object getObject() {
            return mObject;
        }

        /**
         * Store a single object in this Fragment.
         *
         * @param object The object to store
         */
        public void setObject(Object object) {
            mObject = object;
        }
    }
}
