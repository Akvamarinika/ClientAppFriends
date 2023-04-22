package com.akvamarin.clientappfriends.API.connection;

import android.content.Context;
import android.util.Log;

import com.akvamarin.clientappfriends.API.AuthInterceptor;
import com.akvamarin.clientappfriends.utils.PreferenceManager;
import com.akvamarin.clientappfriends.utils.PropertiesReader;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.Properties;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitService {
    private static final String TAG = "RetrofitService";
    private static RetrofitService instance;
    private Retrofit retrofit;
    private Properties properties;
    private PreferenceManager preferenceManager;

    private RetrofitService(Context context) {
        initRetrofit(context);
    }

    public static RetrofitService getInstance(Context context) {
        if (instance == null) {
            instance = new RetrofitService(context);
        }
        return instance;
    }

    private void initRetrofit(Context context) {
        if (properties == null) {
            properties = PropertiesReader.getInstance(context).getProperties("application.properties");
        }

        String ipAddress = properties.getProperty("server.address");
        String port = properties.getProperty("server.port");
        String hostname = properties.getProperty("server.host");
        Log.d(TAG, "initRetrofit: " + hostname);

        preferenceManager = new PreferenceManager(context);
        OkHttpClient.Builder okHttpbuilder = new OkHttpClient.Builder()
                .addInterceptor(new AuthInterceptor(preferenceManager, context));

        retrofit = new Retrofit.Builder()
                .baseUrl("http://" + ipAddress + ":" + port)
                .client(okHttpbuilder.build()) // for interceptor
                .addConverterFactory(GsonConverterFactory.create(new Gson()))
                .build();
    }

    public Retrofit getRetrofit() {
        return retrofit;
    }
}
