package com.akvamarin.clientappfriends.view;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.akvamarin.clientappfriends.API.AuthenticationApi;
import com.akvamarin.clientappfriends.API.ErrorResponse;
import com.akvamarin.clientappfriends.API.ErrorUtils;
import com.akvamarin.clientappfriends.API.connection.RetrofitService;
import com.akvamarin.clientappfriends.R;
import com.akvamarin.clientappfriends.domain.dto.AuthToken;
import com.akvamarin.clientappfriends.domain.dto.AuthUserSocialDTO;
import com.akvamarin.clientappfriends.domain.dto.UserSignInDTO;
import com.akvamarin.clientappfriends.domain.enums.Role;
import com.akvamarin.clientappfriends.domain.enums.Sex;
import com.akvamarin.clientappfriends.utils.CheckerFields;
import com.akvamarin.clientappfriends.utils.Constants;
import com.akvamarin.clientappfriends.utils.PreferenceManager;
import com.akvamarin.clientappfriends.view.register.RegisterActivity;
import com.akvamarin.clientappfriends.vk.models.VKUser;
import com.akvamarin.clientappfriends.vk.requests.VKUsersCommand;
import com.google.android.material.textfield.TextInputLayout;
import com.vk.api.sdk.VK;
import com.vk.api.sdk.VKApiCallback;
import com.vk.api.sdk.auth.VKAccessToken;
import com.vk.api.sdk.auth.VKAuthenticationResult;
import com.vk.api.sdk.auth.VKScope;
import com.vk.api.sdk.exceptions.VKAuthException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthenticationActivity extends AppCompatActivity {
    private static final String TAG = "AuthorizationActivity";
    private Button buttonSignIn;
    private Button buttonCreateAccount;
    private TextInputLayout textInputLayoutEmail;
    private EditText editTextEmail;
    private TextInputLayout textInputLayoutPassword;
    private EditText editTextPassword;
    private ImageView imageViewVK;

    private PreferenceManager preferenceManager;
    private ActivityResultLauncher<Collection<VKScope>> authLauncherVK;
    private RetrofitService retrofitService;
    private AuthenticationApi authenticationApi;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        VK.initialize(getApplicationContext());

        if (VK.isLoggedIn()) {  // уже был ранее залогенен пользователь в ВК
            AllEventsActivity.startFrom(this);
            finish();
            return;
        }

        setContentView(R.layout.activity_authorization);
        initWidgets();

        authLauncherVK = VK.login(this, result -> {
            if (result instanceof VKAuthenticationResult.Success) {
                VKAccessToken token = ((VKAuthenticationResult.Success) result).getToken();
                String uuid = token.getUserId().toString();
                String email = null;

                try {
                    email = token.getEmail();
                } catch (RuntimeException ex) {
                    Log.e(TAG, "Error getting email: " + ex.getMessage());
                }

                Log.d(TAG, "token: " + token.getAccessToken());
                Log.d(TAG, "token Expired: " + token.getExpiresInSec()); //86400
                Log.d(TAG, "token Secret: " + token.getSecret());
                Log.d(TAG, "uuid: " + uuid);
                Log.d(TAG, "email: " + email);

                AuthUserSocialDTO authUserSocialDTO = AuthUserSocialDTO.builder()
                        .socialToken(token.getAccessToken())
                        .vkId(uuid)
                        .email(email)
                        .username(email)
                        .roles(new HashSet<>(Collections.singleton(Role.USER)))
                        .build();


                requestUsers(authUserSocialDTO);

            } else if (result instanceof VKAuthenticationResult.Failed) {
                onLoginFailed(((VKAuthenticationResult.Failed) result).getException());
            }
        });


        imageViewVK.setOnClickListener(view -> {    // Слушатель на кнопке ВК
            authLauncherVK.launch(getVKPermissions());
            //Log.d(TAG, "VK Fingerprint: " + Arrays.toString(VKUtils.getCertificateFingerprint(this, this.getPackageName())));
            Log.d(TAG, "VKONTAKTE:  click button VK.login ");
        });

        //AppEventsLogger.activateApp(this);


        signInEmailListener();
        createAccountListener();
      //  getAuthorizeData();///////////////////////////////////////////////////////////
    }

    private void initWidgets(){
        buttonSignIn = findViewById(R.id.buttonSignIn);
        buttonCreateAccount = findViewById(R.id.buttonCreateAccount);
        textInputLayoutEmail = findViewById(R.id.textInputLayoutEmail);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        textInputLayoutPassword = findViewById(R.id.textInputLayoutPassword);
        imageViewVK = findViewById(R.id.imageViewVK);
        preferenceManager = new PreferenceManager(getApplicationContext());
        retrofitService = RetrofitService.getInstance(getApplicationContext());
        authenticationApi = retrofitService.getRetrofit().create(AuthenticationApi.class);
    }

    private void signInEmailListener(){
        buttonSignIn.setOnClickListener(view -> {
            CheckerFields checkerFields = new CheckerFields(this);
            boolean isNotEmptyEmail = checkerFields.isTextFieldsNotEmpty(editTextEmail, textInputLayoutEmail);
            boolean isNotEmptyPassword = checkerFields.isTextFieldsNotEmpty(editTextPassword, textInputLayoutPassword);

            if (isNotEmptyEmail && isNotEmptyPassword) {
                String email = editTextEmail.getText().toString();
                String password = editTextPassword.getText().toString();
                UserSignInDTO user = new UserSignInDTO(email, password);

                /*TODO:
                   1. email & password отправить на сервер и получить ответ
                *  2. Выдать token
                */
             /*   userApi.getUser(user).enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(Call<User> call, Response<User> response) {
                        Toast.makeText(AuthenticationActivity.this, "Save successful", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(Call<User> call, Throwable t) {
                        Toast.makeText(AuthenticationActivity.this, "Save filed!!!", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "error: " + t.fillInStackTrace());
                    }
                });*/

                /*Intent intent = new Intent(this, AllEventsActivity.class);
                startActivity(intent);
                finish();*/

            }
        });

    }

    private void createAccountListener(){
        buttonCreateAccount.setOnClickListener(view -> {
            Intent intent = new Intent(this, RegisterActivity.class);
            startActivity(intent);
        });
    }


    /***
     * Возвращает список необходимых разрешений
     * для соц. сети ВКонтакте
     * **/
    private List<VKScope> getVKPermissions() {
        List<VKScope> permissions = new ArrayList<>();
        permissions.add(VKScope.EMAIL);
        //permissions.add(VKScope.PHONE);
        //permissions.add(VKScope.PHOTOS);
        //permissions.add(VKScope.OFFLINE);
        return permissions;
    }

    /***
     * Когда только что аутентифицировался пользователь
     * Перенаправить в AllEventsActivity
     * **/
    private void onLogin() {
        AllEventsActivity.startFrom(AuthenticationActivity.this);
        finish();
    }

    private void onLoginFailed(VKAuthException exception) {
        if (!exception.isCanceled()) {
            int descriptionResource;

            if (exception.getWebViewError() == WebViewClient.ERROR_HOST_LOOKUP) {
                descriptionResource = R.string.message_connection_error;
            } else {
                descriptionResource = R.string.message_unknown_error;
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(AuthenticationActivity.this);
            builder.setMessage(descriptionResource);

            builder.setPositiveButton(R.string.vk_retry, new DialogInterface.OnClickListener() {  // retry button to re-initiate the authentication process with VKScope values
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    authLauncherVK.launch(getVKPermissions());  // launch the authentication process with VKScope values
                }
            });

            builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() { // cancel button to dismiss the alert dialog box
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss(); // dismiss the alert dialog box
                }
            });

            builder.show();
        }
    }


    /** Все поля для VK API
     * https://vk.com/dev.php?method=user&prefix=objects
     * */
    private void requestUsers(AuthUserSocialDTO authUserSocialDTO) {
        showProgressDialog("Loading...");

        VK.execute(new VKUsersCommand(new int[0]), new VKApiCallback<>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void success(List<VKUser> result) {

                if (!isFinishing() && !result.isEmpty()) {
                    VKUser user = result.get(0);
                    authUserSocialDTO.setFirstName(user.firstName);
                    authUserSocialDTO.setLastName(user.lastName);
                    authUserSocialDTO.setPhoto(user.photo);
                    authUserSocialDTO.setDateOfBirth(user.dateOfBirth);
                    authUserSocialDTO.setCity(user.cityTitle);
                    authUserSocialDTO.setCountry(user.countryTitle);
                    authUserSocialDTO.setSex(Sex.convertVKInAppValue(user.sex));
                    Log.d(TAG, String.format("authUserSocialDTO = %s%n", authUserSocialDTO));

                    if (!isFinishing() && preferenceManager != null && preferenceManager.getString(Constants.KEY_APP_TOKEN) == null) {
                        authenticationApi.authUserWithSocial(authUserSocialDTO).enqueue(new Callback<>() {
                            @Override
                            public void onResponse(@NonNull Call<AuthToken> call, @NonNull Response<AuthToken> response) {
                                dismissProgressDialog();
                                Toast.makeText(AuthenticationActivity.this, "Save successful", Toast.LENGTH_SHORT).show();
                                Log.d(TAG, String.format("Response: %s %s%n", response.code(), response));
                                Log.d(TAG, String.format("Response token: %s%n", response.body()));

                                if(response.isSuccessful()){
                                    AuthToken serverToken = response.body();
                                    assert serverToken != null;
                                    preferenceManager.putString(Constants.KEY_APP_TOKEN, serverToken.getToken()); //токен
                                    onLogin(); //перейти на основную активность, когда прошла отправка на сервер
                                } else {
                                    ErrorResponse error = ErrorUtils.parseError(response, retrofitService);
                                    Log.d(TAG, error.getStatusCode() + " " + error.getMessage());
                                }
                            }

                            @Override
                            public void onFailure(@NonNull Call<AuthToken> call, @NonNull Throwable t) {
                                dismissProgressDialog();
                                Toast.makeText(AuthenticationActivity.this, "onFailure!!!", Toast.LENGTH_SHORT).show();
                                Log.d(TAG, "error: " + t.fillInStackTrace());
                            }
                        });
                    }
                }
            }

            @Override
            public void fail(@NonNull Exception error) {
                dismissProgressDialog();
                Log.e(TAG, error.toString());
            }
        });
    }





    /** запускает AuthorizationActivity из текущего Контекста
     * очистит все существующие действия в верхней части стека, перед запуском нового
     * **/
    public static void startFrom(Context context) {
        Intent intent = new Intent(context, AuthenticationActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);
    }


    private void showProgressDialog(String message) {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(AuthenticationActivity.this);
            progressDialog.setCancelable(false);
        }
        progressDialog.setMessage(message);
        progressDialog.show();
    }

    private void dismissProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        progressDialog = null;
    }




    /*** Methods check fields: ***/
    private boolean isEmptyEditText(EditText editText) {
        int textLength = editText.getText().toString().trim().length();
        return textLength == 0;
    }


    private void startMainActivityAllEvents(){
        Intent intent = new Intent(AuthenticationActivity.this, AllEventsActivity.class);
        startActivity(intent);
        finish();
    }



   /* public void getAuthorizeData() {
     if (loginInfoProvider.loadInfoAboutUserInVK()){
            startMainActivityAllEvents();
        }
    }*/

}