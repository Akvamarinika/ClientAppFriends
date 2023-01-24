package com.akvamarin.clientappfriends;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
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
import com.vk.api.sdk.auth.VKScope;
import com.vk.api.sdk.requests.VKRequest;
import com.vk.api.sdk.utils.VKUtils;
import com.vk.sdk.api.base.dto.BaseCity;
import com.vk.sdk.api.users.UsersService;
import com.vk.sdk.api.users.dto.NameCaseParam;
import com.vk.sdk.api.users.dto.UsersFields;
import com.vk.sdk.api.users.dto.UsersUserXtrCounters;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
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
    private ImageView imageViewGoogle;
    private ImageView imageViewInsta;
    private ImageView imageViewFacebook;

    private PreferenceManager preferenceManager;
    private VKAccessToken access_token;
    private GoogleSignInClient googleSignInClient;
    private boolean toUpdateAuth;

    private LoginPresenter loginPresenter;
    private LoginInfoProvider loginInfoProvider;
    private static final String TAG = "AuthorizationActivity";

    private RetrofitService retrofitService;
    private UserApi userApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authorization);

//        if (VK.isLoggedIn()) {
//            startMainActivityAllEvents();
//        }

        GoogleSignInOptions options = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(getApplicationContext(), options);






        //AppEventsLogger.activateApp(this);


        initWidgets();
        signInEmailListener();
        createAccountListener();
        socialListeners();
        getAuthorizeData();///////////////////////////////////////////////////////////
    }

    private void initWidgets(){
        buttonSignIn = findViewById(R.id.buttonSignIn);
        buttonCreateAccount = findViewById(R.id.buttonCreateAccount);
        textInputLayoutEmail = findViewById(R.id.textInputLayoutEmail);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        textInputLayoutPassword = findViewById(R.id.textInputLayoutPassword);
        imageViewVK = findViewById(R.id.imageViewVK);
        imageViewGoogle = findViewById(R.id.imageViewGoogle);
        imageViewInsta = findViewById(R.id.imageViewInsta);
        imageViewFacebook = findViewById(R.id.imageViewFacebook);
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

    private void socialListeners(){
        ActivityResultLauncher<Intent> launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Task<GoogleSignInAccount> accountTask = GoogleSignIn.getSignedInAccountFromIntent(result.getData());
                    try {
                        GoogleSignInAccount account = accountTask.getResult();
                        //getParentFragmentManager().setFragmentResult(AUTH_RESULT, new Bundle());
                    } catch (Exception ex) {
                        Toast.makeText(getApplicationContext(), "Auth Failed", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

        /*TODO: авторизация через соц. сети: */
        imageViewVK.setOnClickListener(view -> {
            List<VKScope> permissions = new ArrayList<>();
            permissions.add(VKScope.EMAIL);
  //          permissions.add(VKScope.PHONE);
//            permissions.add(VKScope.PHOTOS);
 //           permissions.add(VKScope.OFFLINE);

            VK.login(this, permissions);
            //Log.d(TAG, "VK Fingerprint: " + Arrays.toString(VKUtils.getCertificateFingerprint(this, this.getPackageName())));
            Log.d(TAG, "VKONTAKTE:  click button VK.login ");
        });

        imageViewGoogle.setOnClickListener(view -> {
            Intent intent = googleSignInClient.getSignInIntent();
            launcher.launch(intent);
        });

    }

    //private void

    /*** Methods check fields: ***/
    private boolean isEmptyEditText(EditText editText) {
        int textLength = editText.getText().toString().trim().length();
        return textLength == 0;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (toUpdateAuth) {
            toUpdateAuth = false;
            startMainActivityAllEvents();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        VKAuthCallback vkCallback = loginPresenter.loginVkontakte(requestCode, resultCode, data);
//data == null ||
        if (!VK.onActivityResult(requestCode, resultCode, data, vkCallback)) { //если нет приложения ВК
            super.onActivityResult(requestCode, resultCode, data);
        }

        //super.onActivityResult(requestCode, resultCode, data);
    }

    private void startMainActivityAllEvents(){
        Intent intent = new Intent(AuthorizationActivity.this, AllEventsActivity.class);
        startActivity(intent);
        finish();
    }


    public static void signOut(Context context) {
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build();
        GoogleSignIn.getClient(context, googleSignInOptions).signOut();

        if (VK.isLoggedIn()) {
            VK.logout();
        }
    }


    public void getAuthorizeData() {
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getApplicationContext());
        if (account != null) {
            String urlPhoto = account.getPhotoUrl().getPath();
            preferenceManager.putBoolean(Constants.KEY_IS_SIGNED_IN, true);
            preferenceManager.putString(Constants.KEY_EMAIL, account.getEmail());
            preferenceManager.putString(Constants.KEY_NAME, account.getDisplayName());
            preferenceManager.putString(Constants.KEY_IMAGE, (urlPhoto != null) ? urlPhoto : "");
            startMainActivityAllEvents();
        }

        if (loginInfoProvider.loadInfoAboutUserInVK()){
            startMainActivityAllEvents();
        }


    }


}