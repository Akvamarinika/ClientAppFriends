package com.akvamarin.clientappfriends.view.register;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.akvamarin.clientappfriends.API.connection.AuthenticationApi;
import com.akvamarin.clientappfriends.API.connection.CityApi;
import com.akvamarin.clientappfriends.API.RetrofitService;
import com.akvamarin.clientappfriends.R;
import com.akvamarin.clientappfriends.domain.dto.CityDTO;
import com.akvamarin.clientappfriends.domain.dto.UserDTO;
import com.akvamarin.clientappfriends.utils.CheckerFields;
import com.akvamarin.clientappfriends.utils.Constants;
import com.akvamarin.clientappfriends.utils.PreferenceManager;
import com.akvamarin.clientappfriends.view.AllEventsActivity;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterCityActivity extends AppCompatActivity {
    private static final String TAG = "CityRegister";
    private List<String> mCities = Arrays.asList(new String[]{"Ивангород", "Иваново", "Ивантеевка", "Ивдель",
            "Игарка", "Ижевск", "Избербаш", "Изобильный", "Иланский", "Ишимбай",
            "Инза", "Инкерман", "Иннополис", "Инсар", "Исилькуль", "Искитим", "Истра", "Ишим",
            "Инта", "Ипатово", "Ирбит", "Иркутск"});
    private static List<CityDTO> citiesList = new ArrayList<>();
    /*TODO: вернуть json  городами*/

    private Button buttonRegContinueFive;
    private TextInputLayout textInputLayoutRegCity;
    private AutoCompleteTextView autoCompleteTextViewCity;
    private ArrayAdapter<CityDTO> mAutoCompleteAdapter;

    private UserDTO user;
    private PreferenceManager preferenceManager;

    private RetrofitService retrofitService;
    private AuthenticationApi userApi;
    private CityApi cityApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_city);

        Intent intent = getIntent();
        user = (UserDTO) intent.getSerializableExtra("classUser");

        initWidgets();

        uploadCityFromServerDatabase();
        autoCompleteTextViewCity.setAdapter(mAutoCompleteAdapter);



        buttonRegContinueFive.setOnClickListener(view -> {
            CheckerFields checkerFields = new CheckerFields(getApplicationContext());
            boolean hasCity = checkerFields.containsCityInList(autoCompleteTextViewCity, textInputLayoutRegCity, citiesList);

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
                android.R.layout.simple_dropdown_item_1line, citiesList);
        preferenceManager = new PreferenceManager(getApplicationContext());
        retrofitService = RetrofitService.getInstance(getApplicationContext());
        userApi = retrofitService.getRetrofit().create(AuthenticationApi.class);
        cityApi = retrofitService.getRetrofit().create(CityApi.class);
    }

    private void uploadCityFromServerDatabase() {
        cityApi.getAllCities().enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<List<CityDTO>> call, @NonNull Response<List<CityDTO>> response) {
                if (response.isSuccessful()) {
                    citiesList = response.body();
                } else {
                    Toast.makeText(getApplicationContext(), "getAllCitiesDTOs() code:" + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<CityDTO>> call, @NonNull Throwable t) {
                Toast.makeText(RegisterCityActivity.this, "Save User filed!!!", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "error: " + t.fillInStackTrace());
            }
        });
    }

    private void sendPassAndUserOnServer() {
        preferenceManager.putBoolean(Constants.KEY_IS_SIGNED_IN, true);
        preferenceManager.putLong(Constants.KEY_USER_ID, 100L);
        //preferenceManager.putString(Constants.KEY_NAME, user.getUserName());
        //preferenceManager.putString(Constants.KEY_IMAGE, "https://android-obzor.com/wp-content/uploads/2022/03/dc715986ea8bc0a25ea8655e6c2c1386.jpg");
        preferenceManager.putString(Constants.KEY_CITY, autoCompleteTextViewCity.getText().toString());

        /*TODO: отправить данные на сервер */

        userApi.registerUser(user).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                Toast.makeText(RegisterCityActivity.this, "Save New User successful", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                Toast.makeText(RegisterCityActivity.this, "Save User filed!!!", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "error: " + t.fillInStackTrace());
            }
        });


        Intent intent = new Intent(this, AllEventsActivity.class);
        intent.putExtra("classUser", user);
        //startActivity(intent);
    }
}