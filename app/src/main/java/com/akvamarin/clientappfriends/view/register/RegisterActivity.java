package com.akvamarin.clientappfriends.view.register;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;

import com.akvamarin.clientappfriends.API.ErrorResponse;
import com.akvamarin.clientappfriends.API.ErrorUtils;
import com.akvamarin.clientappfriends.API.RetrofitService;
import com.akvamarin.clientappfriends.API.connection.UserApi;
import com.akvamarin.clientappfriends.BaseActivity;
import com.akvamarin.clientappfriends.R;
import com.akvamarin.clientappfriends.domain.dto.UserDTO;
import com.akvamarin.clientappfriends.domain.enums.Role;
import com.akvamarin.clientappfriends.utils.CheckerFields;
import com.akvamarin.clientappfriends.utils.Constants;
import com.akvamarin.clientappfriends.utils.PreferenceManager;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Collections;
import java.util.HashSet;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/***
 * Регистрация "Классик", email
 *  Шаг 1 из 6
 * **/

public class RegisterActivity extends BaseActivity {
    private static final String TAG = "RegisterActivity";

    private TextInputLayout textInputLayoutRegEmail;
    private EditText editTextRegEmail;

    private TextInputLayout textInputLayoutRegPassword;
    private EditText editTextRegPassword;

    private TextInputLayout textInputLayoutRegRepeatPassword;
    private EditText editTextRegRepeatPassword;

    private Button buttonRegContinue;

    private CheckerFields checkerFields;
    private PreferenceManager preferenceManager;

    private RetrofitService retrofitService;
    private UserApi userApi;
    private UserDTO user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initUI();
        initToolsObjects();

        buttonRegContinue.setOnClickListener(view -> {
           if (checkEmptyFields() && checkInputEmailPass()){
               String email = editTextRegEmail.getText().toString();
               String password = editTextRegPassword.getText().toString();

               user = new UserDTO();
               user.setEmail(email);
               user.setUsername(email);
               user.setPassword(password);
               user.setRoles(new HashSet<>(Collections.singleton(Role.USER)));

               preferenceManager.putString(Constants.KEY_APP_TOKEN, null);
               preferenceManager.putString(Constants.KEY_LOGIN, user.getUsername()); // login
               preferenceManager.putString(Constants.KEY_PASSWORD, user.getPassword()); // pass
               Log.d(TAG, "Login user: " + user.getUsername());

               showProgressDialog("Проверяем email..");
               checkUsernameOnServer(user.getUsername());
           }
        });
    }

    private void initUI(){
        textInputLayoutRegEmail = findViewById(R.id.textInputLayoutRegEmail);
        editTextRegPassword = findViewById(R.id.editTextRegPassword);
        editTextRegEmail = findViewById(R.id.editTextRegEmail);
        textInputLayoutRegPassword = findViewById(R.id.textInputLayoutRegPassword);
        textInputLayoutRegRepeatPassword = findViewById(R.id.textInputLayoutRegRepeatPassword);
        editTextRegRepeatPassword = findViewById(R.id.editTextRegRepeatPassword);
        buttonRegContinue = findViewById(R.id.buttonRegContinue);
    }

    private void initToolsObjects(){
        checkerFields = new CheckerFields(getApplicationContext());
        preferenceManager = new PreferenceManager(getApplicationContext());
        retrofitService = RetrofitService.getInstance(getApplicationContext());
        userApi = retrofitService.getRetrofit().create(UserApi.class);
    }

    private boolean checkEmptyFields(){
        boolean isNotEmptyEmail = checkerFields.isTextFieldsNotEmpty(editTextRegEmail,  textInputLayoutRegEmail);
        boolean isNotEmptyPass = checkerFields.isTextFieldsNotEmpty(editTextRegPassword,  textInputLayoutRegPassword);
        return isNotEmptyEmail && isNotEmptyPass;
    }

    private boolean checkInputEmailPass(){
        boolean isEmail = checkerFields.isEmail(editTextRegEmail, textInputLayoutRegEmail);
        boolean isContainsTwoPass = checkerFields.isTwoPassContains(editTextRegPassword,
                editTextRegRepeatPassword, textInputLayoutRegRepeatPassword);
        boolean isTruePass = checkerFields.checkSymbolsInPassword(editTextRegPassword, textInputLayoutRegPassword);

        return isEmail && isTruePass && isContainsTwoPass;
    }

    private void checkUsernameOnServer(String username){
        userApi.checkUsername(username).enqueue(new Callback<>()  {

            @Override
            public void onResponse(@NonNull Call<Boolean> call, @NonNull Response<Boolean> response) {
                dismissProgressDialog();
                Log.d(TAG,  "isTaken " + response.body());

                if (response.isSuccessful() && response.body() != null) {
                    boolean isTaken = response.body();

                    if (isTaken) {
                        String error = getApplicationContext().getString(R.string.error_duplicate_email);
                        textInputLayoutRegEmail.setError(error);
                    } else {
                        textInputLayoutRegEmail.setError("");
                        Intent intent = new Intent(getApplicationContext(), RegisterGenderActivity.class);
                        intent.putExtra("classUser", user);
                        startActivity(intent);
                        //finish();
                    }
                } else {
                    ErrorResponse error = ErrorUtils.parseError(response, retrofitService);
                    Log.d(TAG, error.getStatusCode() + " " + error.getMessage());
                }
            }

            @Override
            public void onFailure(@NonNull Call<Boolean> call, @NonNull Throwable t) {
                dismissProgressDialog();
                Log.d(TAG, "Error checkUsernameOnServer() --- Fail: " + t.fillInStackTrace());
            }
        });
    }

}