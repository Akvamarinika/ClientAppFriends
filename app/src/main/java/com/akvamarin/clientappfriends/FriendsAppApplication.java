package com.akvamarin.clientappfriends;

import android.app.Application;
import android.util.Log;

import com.akvamarin.clientappfriends.view.AuthorizationActivity;
import com.vk.api.sdk.VK;
import com.vk.api.sdk.VKTokenExpiredHandler;

public class FriendsAppApplication extends Application {
    public static String TAG = "Application";

   // private BroadcastReceiver internetModeChangeReceiver = new InternetModeChangeReceiver();


    //когда срок действия токена истек
    private final VKTokenExpiredHandler tokenTracker = () -> {
        Log.d(TAG, "onTokenExpired...");
        AuthorizationActivity.startFrom(FriendsAppApplication.this);
    };

    @Override
    public void onCreate() {
        super.onCreate();
        VK.initialize(this);
        VK.addTokenExpiredHandler(tokenTracker);

    }




}
