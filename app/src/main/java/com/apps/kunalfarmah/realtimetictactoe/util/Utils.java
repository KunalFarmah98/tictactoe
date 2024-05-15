package com.apps.kunalfarmah.realtimetictactoe.util;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.StrictMode;

import java.net.InetAddress;

public class Utils {
    // checks if it is connceted to a network
    private static boolean isNetworkAvailable(Activity activity) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null;
    }

    // this checks if connection has internet access

    public static boolean hasActiveInternetConnection(Activity activity) {
        if (isNetworkAvailable(activity)) {

            try {
                // forcefully using network on main thread
                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

                StrictMode.setThreadPolicy(policy);
            }
            catch (Exception e){
                e.printStackTrace();
            }

            try {
                InetAddress ipAddr = InetAddress.getByName("google.com");
                //You can replace it with your name
                return !ipAddr.equals("");

            } catch (Exception e) {
                return false;
            }
        } else {
            //Log.d(LOG_TAG, "No network available!");
        }
        return false;
    }
}
