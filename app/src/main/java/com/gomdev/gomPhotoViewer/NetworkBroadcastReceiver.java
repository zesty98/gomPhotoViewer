package com.gomdev.gomPhotoViewer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.widget.Toast;

/**
 * Created by gomdev on 15. 5. 26..
 */
public class NetworkBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if (action.equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {
            NetworkInfo networkInfo =
                    intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
            if (networkInfo.isConnected()) {
                // Wifi is connected
                Toast.makeText(context, "Wifi disconnected!!!", Toast.LENGTH_SHORT).show();
            }
        } else if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
            boolean noConnectivity = intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false);

            if (noConnectivity == true) {
                Toast.makeText(context, "No Connection!!!", Toast.LENGTH_SHORT).show();
            } else {
                NetworkInfo networkInfo =
                        intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);

                int networkType = intent.getIntExtra(ConnectivityManager.EXTRA_NETWORK_TYPE, ConnectivityManager.TYPE_DUMMY);
                if (networkType == ConnectivityManager.TYPE_MOBILE) {
                    if (networkInfo.isConnected() == true) {
                        Toast.makeText(context, "Mobile connected!!!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, "Mobile disconnected!!!", Toast.LENGTH_SHORT).show();
                    }
                } else if (networkType == ConnectivityManager.TYPE_WIFI) {
                    if (networkInfo.isConnected() == true) {
                        Toast.makeText(context, "Wifi connected!!!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, "Wifi disconnected!!!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    }
}
