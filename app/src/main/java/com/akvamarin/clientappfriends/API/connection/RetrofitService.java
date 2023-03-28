package com.akvamarin.clientappfriends.API.connection;

import android.content.Context;
import android.util.Log;

import com.akvamarin.clientappfriends.utils.PropertiesReader;
import com.google.gson.Gson;

import java.util.Properties;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitService {
    private static final String TAG = "RetrofitService";
    private static RetrofitService instance;
    private Retrofit retrofit;
    private Properties properties;

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

        String ipAddress = properties.getProperty("server.ip");
        String port = properties.getProperty("server.port");
        String hostname = properties.getProperty("server.host");
        Log.d(TAG, "initRetrofit: " + hostname);

        retrofit = new Retrofit.Builder()
                .baseUrl("http://" + ipAddress + ":" + port)
                .addConverterFactory(GsonConverterFactory.create(new Gson()))
                .build();
    }

    public Retrofit getRetrofit() {
        return retrofit;
    }
}
