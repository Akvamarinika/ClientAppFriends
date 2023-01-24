package com.akvamarin.clientappfriends.register;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.akvamarin.clientappfriends.R;
import com.akvamarin.clientappfriends.dto.User;
import com.akvamarin.clientappfriends.utils.CheckerFields;
import com.akvamarin.clientappfriends.utils.Constants;
import com.akvamarin.clientappfriends.utils.PreferenceManager;
import com.google.android.material.textfield.TextInputLayout;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class RegisterBirthdayActivity extends AppCompatActivity {
    private static final int AGE_LIMIT = 15;
    private SimpleDateFormat dateFormatter = new SimpleDateFormat("MM-dd-yyyy");
    private DatePicker datePickerRegAge;
    private Button buttonRegContinueThree;

    private User user;
    private String password;
    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_birthday);

        Intent intent = getIntent();
        user = (User) intent.getSerializableExtra("classUser");
        password = intent.getStringExtra("password");

        initWidgets();

        buttonRegContinueThree.setOnClickListener(view -> {
            setUserAge();
            openPhotoPage();
        });
    }

    private void initWidgets() {
        datePickerRegAge = findViewById(R.id.datePickerRegAge);
        buttonRegContinueThree = findViewById(R.id.buttonRegContinueThree);
        preferenceManager = new PreferenceManager(getApplicationContext());
    }

    private void setUserAge() {
        int age = CheckerFields.getAge(datePickerRegAge.getYear(), datePickerRegAge.getMonth(), datePickerRegAge.getDayOfMonth()); // age function
        preferenceManager.putString(Constants.KEY_AGE, String.valueOf(age)); ////////////////////////////////////pref

        // converting date to string
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, datePickerRegAge.getYear());
        calendar.set(Calendar.MONTH, datePickerRegAge.getMonth());
        calendar.set(Calendar.DAY_OF_MONTH, datePickerRegAge.getDayOfMonth());
        Date dateOfBirth = calendar.getTime();
        String strDateOfBirth = dateFormatter.format(dateOfBirth);

        user.setDateOfBirthday(strDateOfBirth);
    }

    private void openPhotoPage(){
        Intent intent = new Intent(this, RegisterPhotoActivity.class);
        intent.putExtra("password", password);
        intent.putExtra("classUser", user);
        startActivity(intent);
    }


}