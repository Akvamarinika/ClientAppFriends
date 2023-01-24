package com.akvamarin.clientappfriends.receivers;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class CheckInternet {
    public static String getNetworkInfo(Context context){
        String status = null;
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE); //Context.CONNECTIVITY_SERVICE
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()){
            status = "connected";
        } else {
            status = "disconnected";
        }
        return status;
    }
}
