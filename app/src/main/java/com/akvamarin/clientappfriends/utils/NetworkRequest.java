package com.akvamarin.clientappfriends.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Scanner;

public class NetworkRequest {
    public static final String SERVER_API_BASE_URL = "";
    public static final String MY_URL = "";
    public static final String MY_PARAM = "";

    public static URL generateURL(String category) throws MalformedURLException {
        Uri uri = Uri.parse(SERVER_API_BASE_URL + MY_URL)
                .buildUpon()
                .appendQueryParameter(MY_PARAM, category)
                .build();

        return new URL(uri.toString());
    }

    public static boolean isErrorRequest(final URL url) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        int code = connection.getResponseCode();

        return code >= 400 && code <= 499;
    }

    public static String getResponseFromServer(final URL url) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        InputStream inputStream = connection.getInputStream();

        if (isErrorRequest(url)){
            throw new IOException("Server: Bad Request!");
        }

        Scanner scanner = new Scanner(inputStream);
        scanner.useDelimiter("\\A");
        boolean hasData = scanner.hasNext();

        return hasData ? scanner.next() : null;
    }

    public static Bitmap getBitmapFromServer(final URL url) throws IOException {
        Bitmap bitmap = null;
        URLConnection urlConnection = url.openConnection();
        urlConnection.connect();
        InputStream inputStream = urlConnection.getInputStream();
        BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
        bitmap = BitmapFactory.decodeStream(bufferedInputStream);
        bufferedInputStream.close();
        inputStream.close();
        return bitmap;
    }

    public static void hasInternetConnection(){

    }
}
