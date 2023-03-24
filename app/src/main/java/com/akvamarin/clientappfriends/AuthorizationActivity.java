package com.akvamarin.clientappfriends;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.akvamarin.clientappfriends.API.UserApi;
import com.akvamarin.clientappfriends.API.connection.RetrofitService;
import com.akvamarin.clientappfriends.dto.User;
import com.akvamarin.clientappfriends.dto.UserSignInDTO;
import com.akvamarin.clientappfriends.presenters.LoginPresenter;
import com.akvamarin.clientappfriends.providers.LoginInfoProvider;
import com.akvamarin.clientappfriends.register.RegisterActivity;
import com.akvamarin.clientappfriends.register.RegisterCityActivity;
import com.akvamarin.clientappfriends.utils.CheckerFields;
import com.akvamarin.clientappfriends.utils.Constants;
import com.akvamarin.clientappfriends.utils.PreferenceManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.squareup.picasso.Picasso;
import com.vk.api.sdk.VK;
import com.vk.api.sdk.VKApiCallback;
import com.vk.api.sdk.auth.VKAccessToken;
import com.vk.api.sdk.auth.VKAuthCallback;
import com.vk.api.sdk.auth.VKAuthenticationResult;
import com.vk.api.sdk.auth.VKScope;
import com.vk.api.sdk.exceptions.VKAuthException;
import com.vk.api.sdk.requests.VKRequest;
import com.vk.api.sdk.utils.VKUtils;


import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthorizationActivity extends AppCompatActivity {
    private Button buttonSignIn;
    private Button buttonCreateAccount;
    private TextInputLayout textInputLayoutEmail;
    private EditText editTextEmail;
    private TextInputLayout textInputLayoutPassword;
    private EditText editTextPassword;
    private ImageView imageViewVK;

    private PreferenceManager preferenceManager;
    private VKAccessToken access_token;
    private GoogleSignInClient googleSignInClient;
    private boolean toUpdateAuth;

    private LoginPresenter loginPresenter;
    private LoginInfoProvider loginInfoProvider;
    private ActivityResultLauncher<Collection<VKScope>> authLauncherVK;
    private static final String TAG = "AuthorizationActivity";

    private RetrofitService retrofitService;
    private UserApi userApi;

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
                String email = token.getEmail();
                Log.d(TAG, "token: " + token.getAccessToken());
                Log.d(TAG, "token Expired: " + token.getExpiresInSec()); //86400
                Log.d(TAG, "token Secret: " + token.getSecret());
                Log.d(TAG, "uuid: " + uuid);
                Log.d(TAG, "email: " + email);



                onLogin();
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
        loginPresenter = new LoginPresenter(preferenceManager, getApplicationContext());
        loginInfoProvider = new LoginInfoProvider(preferenceManager, getApplicationContext());
        retrofitService = new RetrofitService(getApplicationContext());
        userApi = retrofitService.getRetrofit().create(UserApi.class);
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
                userApi.getUser(user).enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(Call<User> call, Response<User> response) {
                        Toast.makeText(AuthorizationActivity.this, "Save successful", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(Call<User> call, Throwable t) {
                        Toast.makeText(AuthorizationActivity.this, "Save filed!!!", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "error: " + t.fillInStackTrace());
                    }
                });

                Intent intent = new Intent(this, AllEventsActivity.class);
                startActivity(intent);
                finish();

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
        //          permissions.add(VKScope.PHONE);
//            permissions.add(VKScope.PHOTOS);
 //      permissions.add(VKScope.OFFLINE);
        return permissions;
    }

    /***
     * Когда только что аутентифицировался пользователь
     * Перенаправить в AllEventsActivity
     * **/
    private void onLogin() {
        AllEventsActivity.startFrom(AuthorizationActivity.this);
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

            AlertDialog.Builder builder = new AlertDialog.Builder(AuthorizationActivity.this);
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


    /*** Methods check fields: ***/
    private boolean isEmptyEditText(EditText editText) {
        int textLength = editText.getText().toString().trim().length();
        return textLength == 0;
    }

    @Override
    protected void onResume() {
        super.onResume();
       /* if (toUpdateAuth) {
            toUpdateAuth = false;
            startMainActivityAllEvents();
        }*/
    }


    /**
     * Запуск активности VK SDK
     * ***/
 /*   @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        VKAuthCallback vkCallback = loginPresenter.loginVkontakte(requestCode, resultCode, data);
//data == null ||
        if (!VK.onActivityResult(requestCode, resultCode, data, vkCallback)) { //если нет приложения ВК
            super.onActivityResult(requestCode, resultCode, data);
        }

        //super.onActivityResult(requestCode, resultCode, data);
    }*/

    private void startMainActivityAllEvents(){
        Intent intent = new Intent(AuthorizationActivity.this, AllEventsActivity.class);
        startActivity(intent);
        finish();
    }


    public static void signOut(Context context) {

        if (VK.isLoggedIn()) {
            VK.logout();
        }
    }


    public void getAuthorizeData() {
     if (loginInfoProvider.loadInfoAboutUserInVK()){
            startMainActivityAllEvents();
        }
    }



    /** запускает AuthorizationActivity из текущего Контекста
     * очистит все существующие действия в верхней части стека, перед запуском нового
     * **/
    public static void startFrom(Context context) {
        Intent intent = new Intent(context, AuthorizationActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);
    }

}