package com.gomdev.gomPhotoViewer;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.http.AndroidHttpClient;
import android.util.Log;
import android.widget.ImageView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Created by gomdev on 15. 5. 19..
 */
public class ImageDownloader_backup {
    private static final String CLASS = "ImageDownloader";
    private static final String TAG = PhotoViewerConfig.TAG + "_" + CLASS;
    private static final boolean DEBUG = PhotoViewerConfig.DEBUG;

    private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();

    interface ImageDownloadListener {
        void onImageDownloaded(ImageInfo imageInfo, Bitmap bitmap);
    }

    private static ColorDrawable sDummyDrawable = new ColorDrawable(Color.BLACK);

    private final Context mContext;
    private ArrayList<WeakReference<ImageDownloadListener>> mListeners = new ArrayList<>();

    private PhotoViewerApplication mApplication = null;
    private ThreadPoolExecutor mExecutor = null;

    ImageDownloader_backup(Context context, ImageDownloadListener imageDownloadListener) {
        mContext = context;
        if (imageDownloadListener != null) {
            mListeners.add(new WeakReference<>(imageDownloadListener));
        }

        mExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(CPU_COUNT * 2);
    }

    void cancelAll() {
        Log.d(TAG, "cancleAll() >>> purge()");
        mExecutor.purge();
        mListeners.clear();
    }

    void setApplication(PhotoViewerApplication application) {
        mApplication = application;
    }

    void addDelegate(ImageDownloadListener imageDownloadListener) {
        if (imageDownloadListener != null) {
            mListeners.add(new WeakReference<ImageDownloadListener>(imageDownloadListener));
        }
    }

    void download(ImageInfo imageInfo, ImageView imageView) {
        Log.d(TAG, "download() imageInfo=" + imageInfo);
        // State sanity: url is guaranteed to never be null in DownloadedDrawable and cache keys.
        if (imageInfo == null) {
            imageView.setImageDrawable(sDummyDrawable);
            return;
        }

        String url = null;

        if (imageInfo.mIsDetailView == true) {
            url = imageInfo.mImageUrl[1];
        } else {
            url = imageInfo.mImageUrl[0];
        }

        if (cancelPotentialDownload(url, imageView)) {
            ImageDownloadTask task = new ImageDownloadTask(imageView, imageInfo);
            DownloadedDrawable downloadedDrawable = new DownloadedDrawable(task);
            imageView.setImageDrawable(downloadedDrawable);
            mExecutor.execute(task);
        }
    }

    private static Bitmap downloadBitmap(String url) {
        final int IO_BUFFER_SIZE = 4 * 1024;

        // AndroidHttpClient is not allowed to be used from the main thread
        final HttpClient client = AndroidHttpClient.newInstance("Android");
        final HttpGet getRequest = new HttpGet(url);

        try {
            HttpResponse response = client.execute(getRequest);
            final int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != HttpStatus.SC_OK) {
                Log.w("ImageDownloader", "Error " + statusCode +
                        " while retrieving bitmap from " + url);
                return null;
            }

            final HttpEntity entity = response.getEntity();
            if (entity != null) {
                InputStream inputStream = null;
                try {
                    inputStream = entity.getContent();
                    // return BitmapFactory.decodeStream(inputStream);
                    // Bug on slow connections, fixed in future release.
                    Bitmap bitmap = null;
                    synchronized (ImageDownloader_backup.class) {
                        bitmap = BitmapFactory.decodeStream(new FlushedInputStream(inputStream));
                    }
                    return bitmap;
                } finally {
                    if (inputStream != null) {
                        inputStream.close();
                    }
                    entity.consumeContent();
                }
            }
        } catch (IOException e) {
            getRequest.abort();
            Log.w(TAG, "I/O error while retrieving bitmap from " + url, e);
        } catch (IllegalStateException e) {
            getRequest.abort();
            Log.w(TAG, "Incorrect URL: " + url);
        } catch (Exception e) {
            getRequest.abort();
            Log.w(TAG, "Error while retrieving bitmap from " + url, e);
        } finally {
            if ((client instanceof AndroidHttpClient)) {
                ((AndroidHttpClient) client).close();
            }
        }
        return null;
    }

    private boolean cancelPotentialDownload(String url, ImageView imageView) {
        ImageDownloadTask bitmapDownloaderTask = getImageDownloadTask(imageView);

        if (bitmapDownloaderTask != null) {
            String bitmapUrl = bitmapDownloaderTask.mURL;
            if ((bitmapUrl == null) || (!bitmapUrl.equals(url))) {
                mExecutor.remove(bitmapDownloaderTask);
            } else {
                // The same URL is already being downloaded.
                return false;
            }
        }
        return true;
    }

    private static ImageDownloadTask getImageDownloadTask(ImageView imageView) {
        if (imageView != null) {
            Drawable drawable = imageView.getDrawable();
            if (drawable instanceof DownloadedDrawable) {
                DownloadedDrawable downloadedDrawable = (DownloadedDrawable) drawable;
                return downloadedDrawable.getBitmapDownloaderTask();
            }
        }
        return null;
    }

    static class FlushedInputStream extends FilterInputStream {
        public FlushedInputStream(InputStream inputStream) {
            super(inputStream);
        }

        @Override
        public long skip(long n) throws IOException {
            long totalBytesSkipped = 0L;
            while (totalBytesSkipped < n) {
                long bytesSkipped = in.skip(n - totalBytesSkipped);
                if (bytesSkipped == 0L) {
                    int b = read();
                    if (b < 0) {
                        break;  // we reached EOF
                    } else {
                        bytesSkipped = 1; // we read one byte
                    }
                }
                totalBytesSkipped += bytesSkipped;
            }
            return totalBytesSkipped;
        }
    }

    class ImageDownloadTask implements Runnable {

        private final WeakReference<ImageView> mImageViewReference;
        private final ImageInfo mImageInfo;
        private String mURL;

        ImageDownloadTask(ImageView imageView, ImageInfo imageInfo) {
            mImageViewReference = new WeakReference<>(imageView);
            mImageInfo = imageInfo;

            if (mImageInfo.mIsDetailView == true) {
                mURL = mImageInfo.mImageUrl[1];
            } else {
                mURL = mImageInfo.mImageUrl[0];
            }
        }

        @Override
        public void run() {
            final Bitmap bitmap = downloadBitmap(mURL);

            if (bitmap != null && mListeners.size() != 0) {
                synchronized (ImageDownloader_backup.class) {
                    ((Activity) mContext).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            int size = mListeners.size();
                            for (int i = 0; i < size; i++) {
                                ImageDownloadListener imageDownloadListener = mListeners.get(i).get();
                                if (imageDownloadListener != null) {
                                    imageDownloadListener.onImageDownloaded(mImageInfo, bitmap);
                                }
                            }
                        }
                    });
                }
            }

            if (mImageViewReference != null && bitmap != null) {
                final ImageView imageView = mImageViewReference.get();
                ImageDownloadTask imageDownloadTask = getImageDownloadTask(imageView);
                // Change bitmap only if this process is still associated with it
                // Or if we don't use any bitmap to task association (NO_DOWNLOADED_DRAWABLE mode)
                if (this == imageDownloadTask) {
                    ((Activity) mContext).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            imageView.setImageBitmap(bitmap);
                        }
                    });

                }
            }
        }
    }

    static class DownloadedDrawable extends ColorDrawable {
        private final WeakReference<ImageDownloadTask> mImageDownloadTaskRef;

        public DownloadedDrawable(ImageDownloadTask imageDownloadTask) {
            super(Color.BLACK);
            mImageDownloadTaskRef =
                    new WeakReference<>(imageDownloadTask);
        }

        public ImageDownloadTask getBitmapDownloaderTask() {
            return mImageDownloadTaskRef.get();
        }
    }
}
