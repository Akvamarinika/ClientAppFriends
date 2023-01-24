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
    private Retrofit retrofit;

    public RetrofitService(Context context) {
        initRetrofit(context);
    }

    private void initRetrofit(Context context){
        Properties propFile = PropertiesReader.getInstance(context).getProperties("application.properties"); //properties
        String ipAddress = propFile.getProperty("ip");
        String port = propFile.getProperty("port");
        String hostname = propFile.getProperty("host");
        Log.d(TAG, "initRetrofit: " + hostname);

        retrofit = new Retrofit.Builder()
        .baseUrl("http://192.168.1.33:9000")    //"http://192.168.1.33:9000"
        .addConverterFactory(GsonConverterFactory.create(new Gson()))
        .build();
    }

    public Retrofit getRetrofit() {
        return retrofit;
    }
}
