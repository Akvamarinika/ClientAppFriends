package com.akvamarin.clientappfriends.view.register;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import com.akvamarin.clientappfriends.R;
import com.akvamarin.clientappfriends.domain.dto.User;
import com.akvamarin.clientappfriends.utils.CheckerFields;
import com.akvamarin.clientappfriends.utils.Constants;
import com.akvamarin.clientappfriends.utils.PreferenceManager;
import com.google.android.material.textfield.TextInputLayout;

public class RegisterNameActivity extends AppCompatActivity {
    private TextInputLayout textInputRegName;
    private EditText editTextRegName;
    private Button buttonRegContinueTwo;

    private User user;
    private String password;

    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rigister_name);

        Intent intent = getIntent();
        user = (User) intent.getSerializableExtra("classUser");
        password = intent.getStringExtra("password");

        initWidgets();

        buttonRegContinueTwo.setOnClickListener(view -> {
            setUserNameAndNext();
        });


    }

    private void initWidgets(){
        textInputRegName = findViewById(R.id.textInputRegName);
        editTextRegName = findViewById(R.id.editTextRegName);
        buttonRegContinueTwo = findViewById(R.id.buttonRegContinueTwo);
        preferenceManager = new PreferenceManager(getApplicationContext());

    }

    private void setUserNameAndNext(){
        CheckerFields checkerFields = new CheckerFields(getApplicationContext());
        boolean isNotEmptyName = checkerFields.isTextFieldsNotEmpty(editTextRegName, textInputRegName);
        boolean hasPatternName = checkerFields.checkSymbolsInName(editTextRegName, textInputRegName);

        if (isNotEmptyName && hasPatternName){
            String name = editTextRegName.getText().toString().trim();
            user.setName(name);
            preferenceManager.putString(Constants.KEY_NAME, name); ////////////////////////////////////pref
            openPageBirthday();
        }
    }

   private void openPageBirthday(){
       Intent intent = new Intent(this, RegisterBirthdayActivity.class);
       intent.putExtra("password", password);
       intent.putExtra("classUser", user);
       startActivity(intent);
   }
}