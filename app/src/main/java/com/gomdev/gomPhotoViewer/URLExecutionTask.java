package com.gomdev.gomPhotoViewer;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;

/**
 * Created by gomdev on 15. 5. 19..
 */
public class URLExecutionTask extends AsyncTask<Object, Void, JSONObject> {
    private static final String CLASS = "URLExecutionTask";
    private static final String TAG = PhotoViewerConfig.TAG + "_" + CLASS;
    private static final boolean DEBUG = PhotoViewerConfig.DEBUG;

    public interface Delegate {
        void onSuccess(JSONObject object);

        void onFail();
    }

    private final Delegate mDelegate;

    public URLExecutionTask(Delegate delegate) {
        mDelegate = delegate;
    }

    @Override
    protected JSONObject doInBackground(Object... params) {
        final String urlString = (String) params[0];

        HttpURLConnection connection = null;
        JSONObject obj = null;
        try {
            URL url = new URL(urlString.replace(" ", "%20"));

            connection = (HttpURLConnection) url.openConnection();

            connection.setConnectTimeout(5000);

            int statusCode = connection.getResponseCode();
            if (statusCode == HttpURLConnection.HTTP_OK) {
                InputStream in = new BufferedInputStream(connection.getInputStream());
                obj = convertInputStreamToJSONObject(in);
            } else {
                Log.e(TAG, "doInBackground() statusCode=" + statusCode + " msg=" + connection.getResponseMessage());
                return null;
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (SocketTimeoutException e) {
            e.printStackTrace();

            Log.e(TAG, "doInBackground() SocketTimeoutException!!");
            return null;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }

        return obj;
    }

    private JSONObject convertInputStreamToJSONObject(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while ((line = bufferedReader.readLine()) != null) {
            result += line;
        }

        /* Close Stream */
        if (null != inputStream) {
            inputStream.close();
        }

        JSONObject object = null;
        try {
            object = new JSONObject(result);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return object;
    }

    @Override
    protected void onPostExecute(JSONObject object) {
        if (object == null) {
            mDelegate.onFail();
        } else {
            mDelegate.onSuccess(object);
        }
    }
}
