package com.akvamarin.clientappfriends.view.register;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.DatePicker;

import com.akvamarin.clientappfriends.R;
import com.akvamarin.clientappfriends.domain.dto.UserDTO;
import com.akvamarin.clientappfriends.utils.Constants;
import com.akvamarin.clientappfriends.utils.PreferenceManager;
import com.akvamarin.clientappfriends.utils.Utils;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/***
 * Регистрация "Классик", b-day
 *  Шаг 4 из 6
 * **/
public class RegisterBirthdayActivity extends AppCompatActivity {
    private static final int AGE_LIMIT = 15;
    private final SimpleDateFormat dateFormatter = new SimpleDateFormat("MM-dd-yyyy", Locale.ENGLISH);
    private DatePicker datePickerRegAge;
    private Button buttonRegContinueThree;

    private UserDTO user;
    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_birthday);

        Intent intent = getIntent();
        user = (UserDTO) intent.getSerializableExtra("classUser");

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
        int age = Utils.getAgeWithCalendar(datePickerRegAge.getYear(), datePickerRegAge.getMonth(), datePickerRegAge.getDayOfMonth()); // age function
        preferenceManager.putString(Constants.KEY_AGE, String.valueOf(age)); ////////////////////////////////////pref

        // converting date to string
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, datePickerRegAge.getYear());
        calendar.set(Calendar.MONTH, datePickerRegAge.getMonth());
        calendar.set(Calendar.DAY_OF_MONTH, datePickerRegAge.getDayOfMonth());
        Date dateOfBirth = calendar.getTime();
        String strDateOfBirth = dateFormatter.format(dateOfBirth);

        user.setDateOfBirthday(LocalDate.parse(strDateOfBirth));
    }

    private void openPhotoPage(){
        Intent intent = new Intent(this, RegisterPhotoActivity.class);
        intent.putExtra("classUser", user);
        startActivity(intent);
    }


}