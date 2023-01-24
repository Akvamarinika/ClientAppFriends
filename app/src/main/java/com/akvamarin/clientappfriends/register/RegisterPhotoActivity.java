package com.akvamarin.clientappfriends.register;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.view.textclassifier.ConversationActions;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.akvamarin.clientappfriends.R;
import com.akvamarin.clientappfriends.dto.User;
import com.akvamarin.clientappfriends.utils.BitmapConvertor;
import com.akvamarin.clientappfriends.utils.Constants;
import com.akvamarin.clientappfriends.utils.PreferenceManager;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class RegisterPhotoActivity extends AppCompatActivity {
    private static final int REQUEST_CAMERA = 0, SELECT_FILE = 1;
    private static final int PERMISSION_CALLBACK_CONSTANT = 100;
    private static final int REQUEST_PERMISSION_SETTING = 101;

    private User user;
    private String password;

    private ImageView imageViewRegAvatar;
    private Button buttonRegContinueFour;
    private ImageView imageView;
    private Uri mImageUri;

    private final String[] permissionsRequired = new String[]{Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private SharedPreferences permissionStatus;
    private boolean sentToSettings = false;

    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_photo);

        Intent intent = getIntent();
        user = (User) intent.getSerializableExtra("classUser");
        password = intent.getStringExtra("password");

        initWidgets();

        permissionStatus = getSharedPreferences("permissionStatus", MODE_PRIVATE);
        requestMultiplePermissions();

        imageViewRegAvatar.setOnClickListener(view -> {
            imageView = imageViewRegAvatar;
            proceedAfterPermission();
        });

        buttonRegContinueFour.setOnClickListener(view -> {
            if (user.getUrlAvatar() != null){
                openPageCity();
            }
        });
    }

    private void initWidgets() {
        imageViewRegAvatar = findViewById(R.id.imageViewRegAvatar);
        buttonRegContinueFour = findViewById(R.id.buttonRegContinueFour);
        preferenceManager = new PreferenceManager(getApplicationContext());
    }

    private void openPageCity(){
        Intent intent = new Intent(this, RegisterCityActivity.class);
        intent.putExtra("password", password);
        intent.putExtra("classUser", user);
        startActivity(intent);
    }

    private void requestMultiplePermissions() {
        if (ActivityCompat.checkSelfPermission(RegisterPhotoActivity.this, permissionsRequired[0]) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(RegisterPhotoActivity.this, permissionsRequired[1]) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(RegisterPhotoActivity.this, permissionsRequired[2]) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(RegisterPhotoActivity.this, permissionsRequired[0])
                    || ActivityCompat.shouldShowRequestPermissionRationale(RegisterPhotoActivity.this, permissionsRequired[1])
                    || ActivityCompat.shouldShowRequestPermissionRationale(RegisterPhotoActivity.this, permissionsRequired[2])) {
                //Show Information about why you need the permission
                AlertDialog.Builder builder = new AlertDialog.Builder(RegisterPhotoActivity.this);
                builder.setTitle("Need Multiple Permissions");
                builder.setMessage("This app needs Camera and Location permissions.");
                builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        ActivityCompat.requestPermissions(RegisterPhotoActivity.this, permissionsRequired, PERMISSION_CALLBACK_CONSTANT);
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
            } else if (permissionStatus.getBoolean(permissionsRequired[0], false)) {
                //Previously Permission Request was cancelled with 'Dont Ask Again',
                // Redirect to Settings after showing Information about why you need the permission
                AlertDialog.Builder builder = new AlertDialog.Builder(RegisterPhotoActivity.this);
                builder.setTitle("Требуется несколько разрешений");
                builder.setMessage("Этому приложению требуются права доступа к камере.");
                builder.setPositiveButton("Разрешить", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        sentToSettings = true;
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", getPackageName(), null);
                        intent.setData(uri);
                        startActivityForResult(intent, REQUEST_PERMISSION_SETTING);
                        Toast.makeText(getBaseContext(), "Go to Permissions to Grant  Camera and Location", Toast.LENGTH_LONG).show();
                    }
                });
                builder.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
            } else {
                //just request the permission
                ActivityCompat.requestPermissions(RegisterPhotoActivity.this, permissionsRequired, PERMISSION_CALLBACK_CONSTANT);
            }

            // txtPermissions.setText("Permissions Required");

            SharedPreferences.Editor editor = permissionStatus.edit();
            editor.putBoolean(permissionsRequired[0], true);
            editor.commit();
        } else {
            //You already have the permission, just go ahead.
            //proceedAfterPermission();
        }
    }

    private void proceedAfterPermission() {
        final CharSequence[] options = {"Сделать фото", "Загрузить фото из галереи", "Отмена"};

        /* создание диалогового окна доб. фото */
        AlertDialog.Builder builder = new AlertDialog.Builder(RegisterPhotoActivity.this);

        builder.setTitle("Добавление основной фотографии: ");

        builder.setItems(options, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int item) {

                if (options[item].equals("Сделать фото")) {

                    cameraIntent();

                } else if (options[item].equals("Загрузить фото из галереи")) {

                    galleryIntent();

                } else if (options[item].equals("Отмена")) {

                    dialog.dismiss();

                }

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
                //Got Permission
                proceedAfterPermission();
            }
        }
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_FILE)
                onSelectFromGalleryResult(data);
            else if (requestCode == REQUEST_CAMERA)
                onCaptureImageResult(data);
        }
    }

    private void onCaptureImageResult(Intent data) {
        Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.PNG, 100, bytes);

        File destination = new File(Environment.getExternalStorageDirectory(),
                System.currentTimeMillis() + ".png");

        FileOutputStream fileOutputStream;
        try {
            destination.createNewFile();
            fileOutputStream = new FileOutputStream(destination);
            fileOutputStream.write(bytes.toByteArray());
            fileOutputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        imageView.setImageBitmap(thumbnail);
        preferenceManager.putString(Constants.KEY_IMAGE_BASE64, BitmapConvertor.convertToBase64(thumbnail)); //preference
        user.setUrlAvatar(destination.getAbsolutePath());
    }


    private void onSelectFromGalleryResult(Intent data) {
        Bitmap bitmap = null;

        if (data != null) {
            try {
                //user.setUrlAvatar(data.getData().getPath());
               // preferenceManager.putString(Constants.KEY_IMAGE, data.getData().getPath()); ////////////////////////////////////pref
                bitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), data.getData());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        assert bitmap != null;
        preferenceManager.putString(Constants.KEY_IMAGE_BASE64, BitmapConvertor.convertToBase64(bitmap)); //preference
        imageView.setImageBitmap(bitmap);
    }


    @Override
    protected void onPostResume() {
        super.onPostResume();
        if (sentToSettings) {
            if (ActivityCompat.checkSelfPermission(RegisterPhotoActivity.this, permissionsRequired[0]) == PackageManager.PERMISSION_GRANTED) {
                //Got Permission
                proceedAfterPermission();
            }
        }
    }




}