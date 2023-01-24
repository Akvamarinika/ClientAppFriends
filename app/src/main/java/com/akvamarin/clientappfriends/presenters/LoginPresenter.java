package com.akvamarin.clientappfriends.presenters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.akvamarin.clientappfriends.API.UserApi;
import com.akvamarin.clientappfriends.API.connection.RetrofitService;
import com.akvamarin.clientappfriends.AuthorizationActivity;
import com.akvamarin.clientappfriends.dto.User;
import com.akvamarin.clientappfriends.utils.Constants;
import com.akvamarin.clientappfriends.utils.PreferenceManager;
import com.vk.api.sdk.VK;
import com.vk.api.sdk.auth.VKAccessToken;
import com.vk.api.sdk.auth.VKAuthCallback;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginPresenter {
    private static final String TAG = "LoginPresenter";
    private final PreferenceManager preferenceManager;
    private final Context context;



    public LoginPresenter(PreferenceManager preferenceManager, Context context) {
        this.preferenceManager = preferenceManager;
        this.context = context;
    }

    public VKAuthCallback loginVkontakte(int requestCode, int resultCode, @Nullable Intent data){
        VKAuthCallback vkCallback = new VKAuthCallback() {
            @Override
            public void onLogin(@NonNull VKAccessToken vkAccessToken) {     //прошел авторизацию
               //VK.saveAccessToken(context, VK.getUserId(), vkAccessToken.getAccessToken(), null);
                preferenceManager.putString(Constants.KEY_EMAIL, vkAccessToken.getEmail());
                //preferenceManager.putString(Constants.KEY_PHONE, vkAccessToken.getPhone());

                Log.d(TAG, vkAccessToken.getEmail().toString());
                Log.d(TAG, "requestCode " + requestCode);
                Log.d(TAG, "resultCode " + resultCode);
                Log.d(TAG, "data " + data);
              //  Log.d(TAG, vkAccessToken.getPhone());
                //           preferenceManager.putString(Constants.KEY_VK_TOKEN, vkAccessToken.toString());
                Log.d(TAG, "VK Token: " + vkAccessToken.getAccessToken());
                preferenceManager.putString(Constants.KEY_VK_TOKEN, vkAccessToken.getAccessToken());

                sendTokenOnServerTest(vkAccessToken.getAccessToken());

                Toast.makeText(context, "RESULT_OK", Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onLoginFailed(int errorCode) {
                Log.d(TAG, "error " + errorCode);
                Toast.makeText(context, "RESULT_ERROR", Toast.LENGTH_SHORT).show();
            }
        };

        return vkCallback;

    }

    private void sendTokenOnServerTest(String token){
        RetrofitService retrofitService = new RetrofitService(context);
        UserApi userApi  = retrofitService.getRetrofit().create(UserApi.class);

        userApi.sendToken(token).enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                Toast.makeText(context, "Save successful", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                Toast.makeText(context, "Save filed!!!", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "error: " + t.fillInStackTrace());
            }
        });
    }
}
