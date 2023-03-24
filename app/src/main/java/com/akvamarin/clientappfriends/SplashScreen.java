package com.akvamarin.clientappfriends;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.akvamarin.clientappfriends.view.AuthorizationActivity;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = new Intent(this, AuthorizationActivity.class);
        startActivity(intent);
        finish();
    }
}