package com.akvamarin.clientappfriends.view.register;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.akvamarin.clientappfriends.API.ErrorResponse;
import com.akvamarin.clientappfriends.API.ErrorUtils;
import com.akvamarin.clientappfriends.API.connection.AuthenticationApi;
import com.akvamarin.clientappfriends.API.connection.CityApi;
import com.akvamarin.clientappfriends.API.RetrofitService;
import com.akvamarin.clientappfriends.BaseActivity;
import com.akvamarin.clientappfriends.R;
import com.akvamarin.clientappfriends.domain.dto.AuthToken;
import com.akvamarin.clientappfriends.domain.dto.AuthUserSocialDTO;
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

/***
 * Регистрация "Классик", photo
 *  Шаг 5 из 6
 * **/
public class RegisterCityActivity extends BaseActivity {
    private static final String TAG = "CityRegister";
    private List<CityDTO> citiesList = new ArrayList<>();

    private Button buttonRegContinueFive;
    private TextInputLayout textInputLayoutRegCity;
    private AutoCompleteTextView autoCompleteTextViewCity;
    private ArrayAdapter<CityDTO> mAutoCompleteAdapter;

    private UserDTO user;

    private RetrofitService retrofitService;
    private CityApi cityApi;
    private AuthenticationApi authenticationApi;
    private PreferenceManager preferenceManager;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_city);

        Intent intent = getIntent();
        user = (UserDTO) intent.getSerializableExtra("classUser");

        initWidgets();

        showProgressDialog("Loading city");
        uploadCityFromServerDatabase();

        autoCompleteTextViewCity.setOnItemClickListener((parent, view, position, id) -> {
            CityDTO selectedCity = (CityDTO) parent.getItemAtPosition(position);
            autoCompleteTextViewCity.setTooltipText(selectedCity.getName());
            user.setCityID(selectedCity.getId());
            Log.d(TAG, "Selected city: " + selectedCity.getName());
        });


        buttonRegContinueFive.setOnClickListener(view -> {
            CheckerFields checkerFields = new CheckerFields(getApplicationContext());
            boolean hasCity = checkerFields.containsCityInList(autoCompleteTextViewCity, textInputLayoutRegCity, citiesList);

            if (hasCity){
                showProgressDialog("Loading...");
                classicRegisterSendUserDtoToServer();
            }
        });
    }

    private void initWidgets() {
        textInputLayoutRegCity = findViewById(R.id.textInputLayoutRegCity);
        autoCompleteTextViewCity = findViewById(R.id.autoCompleteTextViewCity);
        buttonRegContinueFive = findViewById(R.id.buttonRegContinueFive);
        mAutoCompleteAdapter = new ArrayAdapter<>(RegisterCityActivity.this,
                android.R.layout.simple_dropdown_item_1line, citiesList);
        retrofitService = RetrofitService.getInstance(getApplicationContext());
        cityApi = retrofitService.getRetrofit().create(CityApi.class);
        authenticationApi = retrofitService.getRetrofit().create(AuthenticationApi.class);
        preferenceManager = new PreferenceManager(getApplicationContext());
    }

    private void uploadCityFromServerDatabase() {
        if (citiesList.isEmpty()) {
            cityApi.getAllCities().enqueue(new Callback<>() {
                @Override
                public void onResponse(@NonNull Call<List<CityDTO>> call, @NonNull Response<List<CityDTO>> response) {
                    dismissProgressDialog();

                    if (response.isSuccessful()) {
                        citiesList = response.body();
                        mAutoCompleteAdapter = new ArrayAdapter<>(RegisterCityActivity.this,
                                android.R.layout.simple_dropdown_item_1line, citiesList);
                        autoCompleteTextViewCity.setAdapter(mAutoCompleteAdapter);
                    } else {
                        Toast.makeText(getApplicationContext(), "getAllCitiesDTOs() code:" + response.code(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<List<CityDTO>> call, @NonNull Throwable t) {
                    dismissProgressDialog();
                    Toast.makeText(RegisterCityActivity.this, "Что-то пошло не так! Города не загрузились.", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "error: " + t.fillInStackTrace());
                }
            });
        }
    }

    private void classicRegisterSendUserDtoToServer() {
        Log.d(TAG, String.format("method classicRegisterUser() user login = %s%n", user.getUsername()));
        Log.d(TAG, String.format("method classicRegisterUser() dateOfBirthday = %s%n", user.getDateOfBirthday()));

        authenticationApi.registerUser(user).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                dismissProgressDialog();

                if(response.isSuccessful()){
                    Log.d(TAG, "Response save classic user " + response.headers());
                    String url = response.headers().get("Location");

                    if (url != null) {
                        String id = url.substring(url.lastIndexOf("/") + 1);
                        preferenceManager.putLong(Constants.KEY_USER_ID, Long.valueOf(id)); // id
                    }

                    openPagePhoto();
                } else {
                    ErrorResponse error = ErrorUtils.parseError(response, retrofitService);
                    Log.d(TAG, error.getStatusCode() + " " + error.getMessage());
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                dismissProgressDialog();
                Log.d(TAG, "error onFailure: " + t.fillInStackTrace());
            }
        });
    }

    private void openPagePhoto(){
        Intent intent = new Intent(this, RegisterPhotoActivity.class);
        intent.putExtra("classUser", user);
        startActivity(intent);
    }
}