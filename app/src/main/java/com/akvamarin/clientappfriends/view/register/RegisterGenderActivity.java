package com.akvamarin.clientappfriends.view.register;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import com.akvamarin.clientappfriends.R;
import com.akvamarin.clientappfriends.domain.dto.User;
import com.akvamarin.clientappfriends.utils.PreferenceManager;

public class RegisterGenderActivity extends AppCompatActivity {
    private static final String MAN_VALUE = "man";
    private static final String WOMAN_VALUE = "woman";

    private Button buttonRegMan;
    private Button buttonRegWoman;
    private Button genderButtonRegContinueOne;

    boolean isMan = true;
    private User user;
    private String password;
    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_gender);

        Intent intent = getIntent();
        user = (User) intent.getSerializableExtra("classUser");
        password = intent.getStringExtra("password");

        initWidgets();

        buttonRegMan.setOnClickListener(view -> manButtonSelected());

        buttonRegWoman.setOnClickListener(view -> womanButtonSelected());

        genderButtonRegContinueOne.setOnClickListener(view -> {
                setSexAndDefaultAvatar();
                openNamePage();
        });

    }

    private void initWidgets(){
        buttonRegMan = findViewById(R.id.buttonRegMan);
        buttonRegWoman = findViewById(R.id.buttonRegWoman);
        genderButtonRegContinueOne = findViewById(R.id.genderButtonRegContinueOne);

        //buttonRegWoman.setAlpha(.5f);
        preferenceManager = new PreferenceManager(getApplicationContext());

    }


    private void manButtonSelected() {
        isMan = true;
        buttonRegMan.setBackgroundColor(getColor(R.color.colorPrimaryDark));
        buttonRegMan.setAlpha(1.0f);
        buttonRegWoman.setAlpha(.5f);
        buttonRegWoman.setTextColor(getColor(R.color.white));
        buttonRegWoman.setBackgroundColor(Color.GRAY);
    }

    private void womanButtonSelected() {
        isMan = false;
        buttonRegWoman.setBackgroundColor(getColor(R.color.colorPrimaryDark));
        buttonRegWoman.setAlpha(1.0f);
        buttonRegMan.setAlpha(.5f);
        buttonRegMan.setBackgroundColor(Color.GRAY);
    }

    private void setSexAndDefaultAvatar(){
        String ownSex = isMan ? MAN_VALUE : WOMAN_VALUE;
        user.setSex(ownSex);

        //set default avatar
        String defaultPhoto = isMan ? "defaultMan" : "defaultWoman";
        user.setUrlAvatar(defaultPhoto);
    }

    public void openNamePage() {
        Intent intent = new Intent(this, RegisterNameActivity.class);
        intent.putExtra("password", password);
        intent.putExtra("classUser", user);
        startActivity(intent);
    }
}