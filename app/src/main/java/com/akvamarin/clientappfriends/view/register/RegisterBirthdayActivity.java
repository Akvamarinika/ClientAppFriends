package com.akvamarin.clientappfriends.view.register;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.DatePicker;

import com.akvamarin.clientappfriends.R;
import com.akvamarin.clientappfriends.domain.dto.UserDTO;
import com.akvamarin.clientappfriends.utils.Utils;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/***
 * Регистрация "Классик", b-day
 *  Шаг 4 из 6
 * **/
public class RegisterBirthdayActivity extends AppCompatActivity {
    private static final String TAG = "Register";
    private static final int AGE_LIMIT = 15;
    private final SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
    private DatePicker datePickerRegAge;
    private Button buttonRegContinueThree;

    private UserDTO user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_birthday);

        Intent intent = getIntent();
        user = (UserDTO) intent.getSerializableExtra("classUser");

        initWidgets();

        buttonRegContinueThree.setOnClickListener(view -> {
            setUserAge();
            openCityPage();
        });
    }

    private void initWidgets() {
        datePickerRegAge = findViewById(R.id.datePickerRegAge);
        datePickerRegAge.init(2000, 0, 1, null); // 2000, January, 1
        buttonRegContinueThree = findViewById(R.id.buttonRegContinueThree);
    }

    private void setUserAge() {
        int age = Utils.getAgeWithCalendar(datePickerRegAge.getYear(), datePickerRegAge.getMonth(), datePickerRegAge.getDayOfMonth()); // age function

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LocalDate dateOfBirth = LocalDate.of(datePickerRegAge.getYear(), datePickerRegAge.getMonth() + 1, datePickerRegAge.getDayOfMonth());
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            String strDateOfBirth = dateFormatter.format(dateOfBirth);
            Log.d(TAG, "Date Birthday: " + strDateOfBirth);
            user.setDateOfBirthday(String.valueOf(dateOfBirth));
        }
    }

    private void openCityPage(){
        Intent intent = new Intent(this, RegisterCityActivity.class);
        intent.putExtra("classUser", user);
        startActivity(intent);
    }


}