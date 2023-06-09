package com.akvamarin.clientappfriends.view.register;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.akvamarin.clientappfriends.R;
import com.akvamarin.clientappfriends.domain.dto.UserDTO;
import com.akvamarin.clientappfriends.domain.enums.Sex;
import com.akvamarin.clientappfriends.utils.PreferenceManager;

/***
 * Регистрация "Классик", Gender
 *  Шаг 2 из 6
 * **/

public class RegisterGenderActivity extends AppCompatActivity {
    private Button buttonRegMan;
    private Button buttonRegWoman;
    private Button genderButtonRegContinueOne;

    boolean isMan = true;
    private UserDTO user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_gender);

        Intent intent = getIntent();
        user = (UserDTO) intent.getSerializableExtra("classUser");

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
        Sex ownSex = isMan ? Sex.MALE : Sex.FEMALE;
        user.setSex(ownSex);
    }

    public void openNamePage() {
        Intent intent = new Intent(this, RegisterNameActivity.class);
        intent.putExtra("classUser", user);
        startActivity(intent);
    }
}