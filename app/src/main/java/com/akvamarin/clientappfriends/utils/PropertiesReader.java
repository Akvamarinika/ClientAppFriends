package com.akvamarin.clientappfriends.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesReader {
    private final Context context;
    private final Properties properties;
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

    public static PropertiesReader getInstance(Context context){
        if(prReader == null)
            return new PropertiesReader(context);
        else
            return prReader;
    }
}
