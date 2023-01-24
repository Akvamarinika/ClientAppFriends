package com.akvamarin.clientappfriends.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;

import java.io.ByteArrayOutputStream;

public class BitmapConvertor {

    public static String convertToBase64(Bitmap bitmap){
        Bitmap bitmapImage = bitmap;
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        String imageEncoded = Base64.encodeToString(byteArray, Base64.DEFAULT);
        Log.d("Image Log:", imageEncoded);
        return imageEncoded;
    }

    // method for base64 to bitmap
    public static Bitmap convertFromBase64ToBitmap(String encoded){
        byte[] bytes = Base64.decode(encoded, Base64.DEFAULT); //byte[] decodedByte = Base64.decode(encoded, 0);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        return decodedByte; //    return BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
    }
}
