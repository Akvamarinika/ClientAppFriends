package com.akvamarin.clientappfriends.view;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.akvamarin.clientappfriends.API.ErrorResponse;
import com.akvamarin.clientappfriends.API.ErrorUtils;
import com.akvamarin.clientappfriends.API.RetrofitService;
import com.akvamarin.clientappfriends.API.connection.AuthenticationApi;
import com.akvamarin.clientappfriends.BaseActivity;
import com.akvamarin.clientappfriends.R;
import com.akvamarin.clientappfriends.domain.dto.AuthToken;
import com.akvamarin.clientappfriends.domain.dto.AuthUserParamDTO;
import com.akvamarin.clientappfriends.domain.dto.AuthUserSocialDTO;
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

public class AuthenticationActivity extends BaseActivity {
    private static final String TAG = "AuthorizationActivity";
    private Button buttonSignIn;
    private Button buttonCreateAccount;
    private TextInputLayout textInputLayoutEmail;
    private EditText editTextEmail;
    private TextInputLayout textInputLayoutPassword;
    private EditText editTextPassword;
    private ImageView imageViewVK;
    private TextView textViewSeeEvents;

    private PreferenceManager preferenceManager;
    private ActivityResultLauncher<Collection<VKScope>> authLauncherVK;
    private RetrofitService retrofitService;
    private AuthenticationApi authenticationApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        VK.initialize(getApplicationContext());
        preferenceManager = new PreferenceManager(getApplicationContext());

        if (VK.isLoggedIn() || (preferenceManager != null &&
                preferenceManager.getString(Constants.KEY_APP_TOKEN) != null)) { // already logged in
            Log.d(TAG, "Login VK: " + VK.isLoggedIn());
            Log.d(TAG, "KEY_APP_TOKEN: " + preferenceManager.getString(Constants.KEY_APP_TOKEN));
            AllEventsActivity.startFrom(this);
            finish();
        } else {
            setContentView(R.layout.activity_authorization);
            initVKLauncher();
            initWidgets();
            setListeners();

            if (preferenceManager != null && preferenceManager.getString(Constants.KEY_APP_TOKEN) == null
                    && !preferenceManager.getString(Constants.KEY_LOGIN).equals("login")
                    && !preferenceManager.getString(Constants.KEY_PASSWORD).equals("pass")){

                showProgressDialog("Loading");
                String login = preferenceManager.getString(Constants.KEY_LOGIN);
                String pass = preferenceManager.getString(Constants.KEY_PASSWORD);
                Log.d(TAG, "Login user: " + login);
                requestTokenForClassicRegFromServer(new AuthUserParamDTO(login, pass));
            }
        }
    }

    private void initWidgets(){
        buttonSignIn = findViewById(R.id.buttonSignIn);
        buttonCreateAccount = findViewById(R.id.buttonCreateAccount);
        textInputLayoutEmail = findViewById(R.id.textInputLayoutEmail);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        textInputLayoutPassword = findViewById(R.id.textInputLayoutPassword);
        imageViewVK = findViewById(R.id.imageViewVK);
        textViewSeeEvents = findViewById(R.id.textViewSeeEvents);
        retrofitService = RetrofitService.getInstance(getApplicationContext());
        authenticationApi = retrofitService.getRetrofit().create(AuthenticationApi.class);
    }

    private void setListeners() {
        imageViewVK.setOnClickListener((view) -> { // Слушатель на кнопке ВК
            authLauncherVK.launch(getVKPermissions());
            Log.d(TAG, "VKONTAKTE:  click button VK.login ");
        });

        textViewSeeEvents.setOnClickListener(view -> {
            startAllEventsActivity();
            Log.d(TAG, "click button textViewSeeEvents");
        });

        signInEmailListener();
        createAccountListener();
    }

    private void initVKLauncher() { //запуск активности ВК
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
                Log.d(TAG, "uuid: " + uuid);
                Log.d(TAG, "email: " + email);

                AuthUserSocialDTO authUserSocialDTO = AuthUserSocialDTO.builder()
                        .socialToken(token.getAccessToken())
                        .vkId(uuid)
                        .email(email)
                        .username(uuid)
                        .roles(new HashSet<>(Collections.singleton(Role.USER)))
                        .build();

                requestUsers(authUserSocialDTO);

            } else if (result instanceof VKAuthenticationResult.Failed) {
                onLoginFailed(((VKAuthenticationResult.Failed) result).getException());
            }
        });
    }

    private void signInEmailListener(){
        buttonSignIn.setOnClickListener(view -> {
            CheckerFields checkerFields = new CheckerFields(this);
            boolean isNotEmptyEmail = checkerFields.isTextFieldsNotEmpty(editTextEmail, textInputLayoutEmail);
            boolean isNotEmptyPassword = checkerFields.isTextFieldsNotEmpty(editTextPassword, textInputLayoutPassword);

            if (isNotEmptyEmail && isNotEmptyPassword) {
                String email = editTextEmail.getText().toString();
                String password = editTextPassword.getText().toString();
                AuthUserParamDTO user = new AuthUserParamDTO(email, password);

                showProgressDialog("Loading");
                requestTokenForClassicRegFromServer(user);

            }
        });
    }

    /***
     * Переход на Регистрацию
     * "Классик"
     * **/
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
        return permissions;
    }

    /***
     * Когда только что аутентифицировался пользователь / режим просмотр
     * Перенаправить в AllEventsActivity
     * **/
    private void startAllEventsActivity() {
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

            // retry button to re-initiate the authentication process with VKScope values
            builder.setPositiveButton(R.string.vk_retry, (dialog, which) -> {
                authLauncherVK.launch(getVKPermissions());  // launch the authentication process with VKScope values
            });

            // cancel button to dismiss the alert dialog box
            builder.setNegativeButton(android.R.string.cancel, (dialog, which) -> dialog.dismiss());

            builder.show();
        }
    }

    /** Все поля для VK API
     * https://vk.com/dev.php?method=user&prefix=objects
     * Получить инфу о пользователе от ВК API
     * */
    private void requestUsers(AuthUserSocialDTO authUserSocialDTO) {
        VK.execute(new VKUsersCommand(new int[0]), new VKApiCallback<>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void success(List<VKUser> result) {
                if (!isFinishing() && !result.isEmpty()) {
                    VKUser user = result.get(0);
                    updateAuthUserSocialDTO(user, authUserSocialDTO);
                    saveAuthToken(authUserSocialDTO);
                }
            }

            @Override
            public void fail(@NonNull Exception error) {
                dismissProgressDialog();
                Log.e(TAG, error.toString());
            }
        });
        showProgressDialog("Loading...");
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void updateAuthUserSocialDTO(VKUser user, AuthUserSocialDTO authUserSocialDTO) {
        authUserSocialDTO.setFirstName(user.firstName);
        authUserSocialDTO.setLastName(user.lastName);
        authUserSocialDTO.setPhoto(user.photo);
        authUserSocialDTO.setDateOfBirth(user.dateOfBirth);
        authUserSocialDTO.setCity(user.cityTitle);
        authUserSocialDTO.setCountry(user.countryTitle);
        authUserSocialDTO.setSex(Sex.convertVKInAppValue(user.sex));
        Log.d(TAG, String.format("authUserSocialDTO = %s%n", authUserSocialDTO));
    }

    private void saveAuthToken(AuthUserSocialDTO authUserSocialDTO) {
        if (!isFinishing() && preferenceManager != null && preferenceManager.getString(Constants.KEY_APP_TOKEN) == null) {
            preferenceManager.putString(Constants.KEY_LOGIN, authUserSocialDTO.getUsername()); // login
            Log.d(TAG, String.format("method saveAuthToken() authUserSocialDTO = %s%n", authUserSocialDTO));
            Log.d(TAG, String.format("method saveAuthToken() authUserSocialDTO = %s%n", authUserSocialDTO.getUsername()));
            String login = preferenceManager.getString(Constants.KEY_LOGIN);
            Log.d(TAG, "Login user: " + login);

            authenticationApi.authUserWithSocial(authUserSocialDTO).enqueue(new Callback<>() {
                @Override
                public void onResponse(@NonNull Call<AuthToken> call, @NonNull Response<AuthToken> response) {
                    dismissProgressDialog();

                    if (!isFinishing()) {
                        handleAuthTokenResponse(response);
                    }
                }

                @Override
                public void onFailure(@NonNull Call<AuthToken> call, @NonNull Throwable t) {
                    dismissProgressDialog();
                    handleAuthTokenFailure(t);
                }
            });
        }
    }

    private void handleAuthTokenResponse(Response<AuthToken> response) {
        dismissProgressDialog();
        Log.d(TAG, String.format("Response: %s %s%n", response.code(), response));
        Log.d(TAG, String.format("Response token: %s%n", response.body()));

        if(response.isSuccessful()){
            AuthToken serverToken = response.body();
            assert serverToken != null;
            preferenceManager.putString(Constants.KEY_APP_TOKEN, serverToken.getToken()); //токен
            startAllEventsActivity(); //перейти на основную активность, когда прошла отправка на сервер
        } else {
            ErrorResponse error = ErrorUtils.parseError(response, retrofitService);
            Log.d(TAG, response.code() + " " + error.getMessage());
        }
    }

    private void handleAuthTokenFailure(Throwable t) {
        if (!isFinishing()) {
            dismissProgressDialog();
            Toast.makeText(AuthenticationActivity.this, "onFailure!!!", Toast.LENGTH_SHORT).show();
            preferenceManager.putString(Constants.KEY_LOGIN, "login"); // login delete
            Log.d(TAG, "error: " + t.fillInStackTrace());
        }
    }

    /*** request Token for Classic register
     * from Server:
     * ***/
    private void requestTokenForClassicRegFromServer(AuthUserParamDTO authUserParamDTO){
        String error = this.getString(R.string.error_login_or_pass);

        authenticationApi.authUser(authUserParamDTO).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<AuthToken> call, @NonNull Response<AuthToken> response) {
                Log.d(TAG, String.format("Response login classic: %s %s%n", response.code(), response));
                dismissProgressDialog();
                handleAuthTokenResponse(response);

                if (response.code() == 401){
                    textInputLayoutPassword.setError(error);
                    textInputLayoutEmail.setError(" ");
                } else {
                    textInputLayoutPassword.setError("");
                    textInputLayoutEmail.setError("");
                }

                if (preferenceManager.getString(Constants.KEY_LOGIN).equals("login")){
                    preferenceManager.putString(Constants.KEY_LOGIN, authUserParamDTO.getUsername()); // login save
                    preferenceManager.putString(Constants.KEY_PASSWORD, authUserParamDTO.getPassword());
                }
            }

            @Override
            public void onFailure(@NonNull Call<AuthToken> call, @NonNull Throwable t) {
                dismissProgressDialog();
                Log.d(TAG, "error login classic: " + t.fillInStackTrace());
            }
        });
    }

    /** запускает AuthenticationActivity из текущего Контекста
     * очистит все существующие действия в верхней части стека, перед запуском нового
     * **/
    public static void startFrom(Context context) {
        Intent intent = new Intent(context, AuthenticationActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);
    }

}