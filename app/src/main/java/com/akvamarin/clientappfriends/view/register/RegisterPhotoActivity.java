package com.akvamarin.clientappfriends.view.register;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;

import com.akvamarin.clientappfriends.API.ErrorResponse;
import com.akvamarin.clientappfriends.API.ErrorUtils;
import com.akvamarin.clientappfriends.API.RetrofitService;
import com.akvamarin.clientappfriends.API.connection.AuthenticationApi;
import com.akvamarin.clientappfriends.API.connection.ImageApi;
import com.akvamarin.clientappfriends.BaseActivity;
import com.akvamarin.clientappfriends.R;
import com.akvamarin.clientappfriends.domain.dto.UserDTO;
import com.akvamarin.clientappfriends.utils.Constants;
import com.akvamarin.clientappfriends.utils.PreferenceManager;
import com.akvamarin.clientappfriends.view.AuthenticationActivity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import lombok.SneakyThrows;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/***
 * Регистрация "Классик", photo
 *  Шаг 6 из 6
 * **/
public class RegisterPhotoActivity extends BaseActivity {
    private static final String TAG = "PhotoRegister";
    private static final int REQUEST_CAMERA = 0;
    private static final int SELECT_FILE = 1;
    private static final int PERMISSION_CALLBACK_CONSTANT = 100;
    private static final int REQUEST_PERMISSION_SETTING = 101;

    private boolean sentToSettings = false;
    private PreferenceManager preferenceManager;

    private ImageView imageViewRegAvatar;
    private Button buttonRegContinueFour;
    private ImageView imageView;

    private final String[] permissionsRequired = new String[]{
            Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    private String imagePath;
    private UserDTO user;
    private RetrofitService retrofitService;
    private AuthenticationApi authenticationApi;
    private ImageApi imageApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_photo);

        Intent intent = getIntent();
        user = (UserDTO) intent.getSerializableExtra("classUser");

        initWidgets();
        requestMultiplePermissions();

        imageViewRegAvatar.setOnClickListener(view -> {
            imageView = imageViewRegAvatar;
            proceedAfterPermission();
        });

        buttonRegContinueFour.setOnClickListener(view -> {
            String userId = preferenceManager.getString(Constants.KEY_USER_ID);
            Log.d(TAG, "imagePath: " + imagePath);

            if (imagePath != null && !userId.equals("id")){
                showProgressDialog("Upload avatar");
                sendPhotoOnServer(imagePath, userId);
            }
        });
    }

    private void initWidgets() {
        imageViewRegAvatar = findViewById(R.id.imageViewRegAvatar);
        buttonRegContinueFour = findViewById(R.id.buttonRegContinueFour);
        retrofitService = RetrofitService.getInstance(getApplicationContext());
        authenticationApi = retrofitService.getRetrofit().create(AuthenticationApi.class);
        imageApi = retrofitService.getRetrofit().create(ImageApi.class);
        preferenceManager = new PreferenceManager(getApplicationContext());
    }

    /***
     * Получение разрешений
     * к камере, местоположению
     * **/
    private void requestMultiplePermissions() {
        int permissionStatus = ActivityCompat.checkSelfPermission(RegisterPhotoActivity.this, permissionsRequired[0])
                + ActivityCompat.checkSelfPermission(RegisterPhotoActivity.this, permissionsRequired[1])
                + ActivityCompat.checkSelfPermission(RegisterPhotoActivity.this, permissionsRequired[2]);

        if (permissionStatus != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(RegisterPhotoActivity.this, permissionsRequired[0])
                    || ActivityCompat.shouldShowRequestPermissionRationale(RegisterPhotoActivity.this, permissionsRequired[1])
                    || ActivityCompat.shouldShowRequestPermissionRationale(RegisterPhotoActivity.this, permissionsRequired[2])) {
                //Показать информацию о том, зачем нужно разрешение:
                new AlertDialog.Builder(RegisterPhotoActivity.this)
                        .setTitle("Требуется несколько разрешений")
                        .setMessage("Этому приложению требуются права доступа к камере и местоположению.")
                        .setPositiveButton("Разрешить", (dialog, which) -> {
                            dialog.cancel();
                            ActivityCompat.requestPermissions(RegisterPhotoActivity.this, permissionsRequired, PERMISSION_CALLBACK_CONSTANT);
                        })
                        .setNegativeButton("Отмена", (dialog, which) -> dialog.cancel())
                        .show();
            } else if (ActivityCompat.shouldShowRequestPermissionRationale(RegisterPhotoActivity.this, permissionsRequired[0])) {
                //Ранее запрос на разрешение был отменен с помощью 'Dont Ask Again',
                // Перенаправить в настройки после показа информации о том, зачем нам нужно разрешение
                new AlertDialog.Builder(RegisterPhotoActivity.this)
                        .setTitle("Требуется несколько разрешений")
                        .setMessage("Этому приложению требуются права доступа к камере.")
                        .setPositiveButton("Разрешить", (dialog, which) -> {
                            dialog.cancel();
                            sentToSettings = true;
                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            Uri uri = Uri.fromParts("package", getPackageName(), null);
                            intent.setData(uri);
                            startActivityForResult(intent, REQUEST_PERMISSION_SETTING);
                            Log.d(TAG, "Перейдите в раздел «Разрешения для предоставления камеры и местоположения».");
                        })
                        .setNegativeButton("Отмена", (dialog, which) -> dialog.cancel())
                        .show();
            } else {
                //запросить разрешения
                ActivityCompat.requestPermissions(RegisterPhotoActivity.this, permissionsRequired, PERMISSION_CALLBACK_CONSTANT);
            }
        }
    }

    /***
     * Создание диалогового окна
     * после получения необх. разрешений
     *  См. options array
     * **/
    private void proceedAfterPermission() {
        final CharSequence[] options = {"Сделать фото", "Загрузить фото из галереи", "Отмена"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle("Добавление основной фотографии: ")
                .setItems(options, (dialog, item) -> {
                    switch (item) {
                        case 0 -> cameraIntent();
                        case 1 -> galleryIntent();
                        case 2 -> dialog.dismiss();
                    }
                });

        builder.show();
    }

    private void galleryIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);//
        startActivityForResult(Intent.createChooser(intent, "Выбрать файл"), SELECT_FILE);
    }

    private void cameraIntent() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_PERMISSION_SETTING) {
            if (ActivityCompat.checkSelfPermission(RegisterPhotoActivity.this, permissionsRequired[0]) == PackageManager.PERMISSION_GRANTED) {
                proceedAfterPermission(); // когда получены разрешения
            }
        }

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_FILE)
                onSelectFromGalleryResult(data);
            else if (requestCode == REQUEST_CAMERA)
                onCaptureImageResult(data);
        }
    }

    /*** Get photo from camera:
     * ***/
    private void onCaptureImageResult(Intent data) {
        Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.PNG, 100, outputStream);

        File destination = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                System.currentTimeMillis() + ".png");
        imagePath = destination.getAbsolutePath();

        try (FileOutputStream fileOutputStream = new FileOutputStream(destination)) {
            fileOutputStream.write(outputStream.toByteArray());
            imageView.setImageBitmap(thumbnail);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /*** получить выбранное
     * изображение (bitmap) из галереи:
     * ***/
    @SneakyThrows
    private void onSelectFromGalleryResult(Intent data) {
        Bitmap bitmap = null;

        if (data != null) {
            try {
                Uri uri = data.getData();
                bitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), uri);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (bitmap != null) {
            File destination = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                    System.currentTimeMillis() + ".png");
            OutputStream outputStream = new FileOutputStream(destination);
            bitmap.compress(Bitmap.CompressFormat.PNG, 80, outputStream);
            outputStream.flush();
            outputStream.close();

            imagePath = destination.getAbsolutePath();

            imageView.setImageBitmap(bitmap);
        }
    }


    /*** UserDTO send on Server:
     * ***/
    private void sendPhotoOnServer(String imagePath, String userId){
        File file = new File(imagePath);
        MultipartBody.Part filePart = MultipartBody.Part.createFormData("file", file.getName(),
                RequestBody.create(MediaType.parse("image/*"), file));
        Log.d(TAG, "USER login: " + user.getUsername());

        imageApi.uploadNewAvatar(filePart, userId).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                dismissProgressDialog();
                Log.d(TAG, String.format("Response upload photo: %s %s%n", response.code(), response));

                if(response.isSuccessful()){
                    Log.d(TAG, response.code() + " complete upload photo ");
                    startAuthenticationActivity();
                } else {
                    ErrorResponse error = ErrorUtils.parseError(response, retrofitService);
                    Log.d(TAG, error.getStatusCode() + " " + error.getMessage());
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                dismissProgressDialog();
                Log.d(TAG, "error upload photo: " + t.fillInStackTrace());
            }
        });
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        if (sentToSettings) {
            if (ActivityCompat.checkSelfPermission(RegisterPhotoActivity.this, permissionsRequired[0]) == PackageManager.PERMISSION_GRANTED) {
                proceedAfterPermission(); // когда получены разрешения
            }
        }
    }

    /***
     * Когда только что зарегистрировался пользователь
     * Перенаправить в AuthenticationActivity
     * **/
    private void startAuthenticationActivity() {
        RegisterPhotoActivity.startFrom(getApplicationContext());
        finish();
    }

    /** запускает RegisterPhotoActivity из текущего Контекста
     * очистит все существующие действия в верхней части стека, перед запуском нового
     * **/
    public static void startFrom(Context context) {
        Intent intent = new Intent(context, AuthenticationActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);
    }

}
