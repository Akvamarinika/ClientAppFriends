package com.akvamarin.clientappfriends.register;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import com.akvamarin.clientappfriends.AllEventsActivity;
import com.akvamarin.clientappfriends.R;
import com.akvamarin.clientappfriends.API.UserApi;
import com.akvamarin.clientappfriends.dto.User;
import com.akvamarin.clientappfriends.utils.CheckerFields;
import com.akvamarin.clientappfriends.utils.Constants;
import com.akvamarin.clientappfriends.utils.PreferenceManager;
import com.akvamarin.clientappfriends.API.connection.RetrofitService;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterCityActivity extends AppCompatActivity {
    private static final String TAG = "POST";
    private List<String> mCities = Arrays.asList(new String[]{"Ивангород", "Иваново", "Ивантеевка", "Ивдель",
            "Игарка", "Ижевск", "Избербаш", "Изобильный", "Иланский", "Ишимбай",
            "Инза", "Инкерман", "Иннополис", "Инсар", "Исилькуль", "Искитим", "Истра", "Ишим",
            "Инта", "Ипатово", "Ирбит", "Иркутск"});
    /*TODO: вернуть json  городами*/

    private Button buttonRegContinueFive;
    private TextInputLayout textInputLayoutRegCity;
    private AutoCompleteTextView autoCompleteTextViewCity;
    private ArrayAdapter<String> mAutoCompleteAdapter;

    private User user;
    private String password;
    private PreferenceManager preferenceManager;

    private RetrofitService retrofitService;
    private UserApi userApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_city);

        Intent intent = getIntent();
        user = (User) intent.getSerializableExtra("classUser");
        password = intent.getStringExtra("password");

        initWidgets();

        autoCompleteTextViewCity.setAdapter(mAutoCompleteAdapter);



        buttonRegContinueFive.setOnClickListener(view -> {
            CheckerFields checkerFields = new CheckerFields(getApplicationContext());
            boolean hasCity = checkerFields.containsCityInList(autoCompleteTextViewCity, textInputLayoutRegCity, mCities);

            if (hasCity){
                sendPassAndUserOnServer();
            }
        });

    }

    private void initWidgets() {
        textInputLayoutRegCity = findViewById(R.id.textInputLayoutRegCity);
        autoCompleteTextViewCity = findViewById(R.id.autoCompleteTextViewCity);
        buttonRegContinueFive = findViewById(R.id.buttonRegContinueFive);
        mAutoCompleteAdapter = new ArrayAdapter<>(RegisterCityActivity.this,
                android.R.layout.simple_dropdown_item_1line, mCities);
        preferenceManager = new PreferenceManager(getApplicationContext());
        retrofitService = new RetrofitService(getApplicationContext());
        userApi = retrofitService.getRetrofit().create(UserApi.class);

    }

    private void sendPassAndUserOnServer() {

        preferenceManager.putBoolean(Constants.KEY_IS_SIGNED_IN, true);
        preferenceManager.putLong(Constants.KEY_USER_ID, 100L);
        //preferenceManager.putString(Constants.KEY_NAME, user.getUserName());
        //preferenceManager.putString(Constants.KEY_IMAGE, "https://android-obzor.com/wp-content/uploads/2022/03/dc715986ea8bc0a25ea8655e6c2c1386.jpg");
        preferenceManager.putString(Constants.KEY_CITY, autoCompleteTextViewCity.getText().toString());

        /*TODO: отправить данные на сервер */
        userApi.save(user).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                Toast.makeText(RegisterCityActivity.this, "Save successful", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(RegisterCityActivity.this, "Save filed!!!", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "error: " + t.fillInStackTrace());
            }
        });


        Intent intent = new Intent(this, AllEventsActivity.class);
        intent.putExtra("password", password);
        intent.putExtra("classUser", user);
        //startActivity(intent);
    }
}