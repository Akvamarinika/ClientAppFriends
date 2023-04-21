package com.akvamarin.clientappfriends.view.register;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.akvamarin.clientappfriends.R;
import com.akvamarin.clientappfriends.domain.dto.UserDTO;
import com.akvamarin.clientappfriends.utils.CheckerFields;
import com.akvamarin.clientappfriends.utils.PreferenceManager;
import com.google.android.material.textfield.TextInputLayout;

/***
 * Регистрация "Классик", email
 *  Шаг 1 из 6
 * **/

public class RegisterActivity extends AppCompatActivity {
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

               UserDTO user = new UserDTO();
               user.setEmail(email);
               user.setPassword(password);

   //            preferenceManager.putString(Constants.KEY_EMAIL, email);
   //            preferenceManager.putString(Constants.KEY_PASSWORD, password);

               Intent intent = new Intent(this, RegisterGenderActivity.class);
               intent.putExtra("classUser", user);
               startActivity(intent);
               //finish();
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
}