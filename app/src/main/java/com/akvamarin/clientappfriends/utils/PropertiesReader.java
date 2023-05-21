package com.akvamarin.clientappfriends.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.AssetManager;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.Properties;

public class PropertiesReader {
    private final Context context;
    private final Properties properties;
    @SuppressLint("StaticFieldLeak")
    private static PropertiesReader prReader;

    public PropertiesReader(Context context) {
        this.context = context;
        properties = new Properties();
    }

    public Properties getProperties(String FileName) {
        try {
            AssetManager am = context.getAssets(); //access to the folder ‘assets’
            InputStream inputStream = am.open(FileName);    //opening the file
            properties.load(inputStream);   //loading of the properties
        }
        catch (IOException e) {
            Log.e("PropertiesReader", e.toString());
        }
        return properties;
    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    public static PropertiesReader getInstance(Context context){
        return Objects.requireNonNullElseGet(prReader, () -> new PropertiesReader(context));
    }
}
