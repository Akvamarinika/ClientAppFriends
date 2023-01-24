package com.akvamarin.clientappfriends;

import android.app.Application;
import android.content.Intent;
import android.util.Log;

import com.vk.api.sdk.VK;
import com.vk.api.sdk.VKTokenExpiredHandler;
import com.vk.api.sdk.auth.VKScope;

import java.util.ArrayList;
import java.util.List;

public class FriendsAppApplication extends Application {
    public static String TAG = "Application";

   // private BroadcastReceiver internetModeChangeReceiver = new InternetModeChangeReceiver();

    private static final VKTokenExpiredHandler tokenTracker = new VKTokenExpiredHandler() { //когда срок действия токена истек
        @Override
        public void onTokenExpired() {
            Log.d(TAG, "onTokenExpired...");


        }


    };

    @Override
    public void onCreate() {
        super.onCreate();
        VK.initialize(this);
        VK.addTokenExpiredHandler(tokenTracker);

    }




}
