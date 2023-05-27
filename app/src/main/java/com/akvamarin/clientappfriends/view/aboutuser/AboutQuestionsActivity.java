package com.akvamarin.clientappfriends.view.aboutuser;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.akvamarin.clientappfriends.R;
import com.akvamarin.clientappfriends.domain.dto.ViewUserDTO;
import com.akvamarin.clientappfriends.domain.enums.Alcohol;
import com.akvamarin.clientappfriends.domain.enums.Psychotype;
import com.akvamarin.clientappfriends.domain.enums.Smoking;

public class AboutQuestionsActivity extends AppCompatActivity {
    private static final String TAG = "AboutQuestionsActivity";
    private ImageView imageCloseIcon;
    private ImageView imageMain;
    private TextView tvTitle;
    private RadioGroup radioGroup;
    private RadioButton radioButton1;
    private RadioButton radioButton2;
    private RadioButton radioButton3;
    private RadioButton radioButton4;
    private RadioButton radioButton5;
    private ImageView imageRightIcon;
    private ViewUserDTO viewUserDTO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_questions);

        initWidgets();
        checkClassType();
        setupClickListeners();
    }

    private void initWidgets() {
        imageCloseIcon = findViewById(R.id.closeIcon);
        imageMain = findViewById(R.id.imageMain);
        tvTitle = findViewById(R.id.tvTitle);
        radioGroup = findViewById(R.id.radioGroup);
        radioButton1 = findViewById(R.id.radioButton1);
        radioButton2 = findViewById(R.id.radioButton2);
        radioButton3 = findViewById(R.id.radioButton3);
        radioButton4 = findViewById(R.id.radioButton4);
        radioButton5 = findViewById(R.id.radioButton5);
        imageRightIcon = findViewById(R.id.imageRightIcon);
    }

    private void checkClassType() {
        Intent intent = getIntent();

        if (intent != null) {
            Object objectEnum = intent.getSerializableExtra("objectEnum");
            viewUserDTO = intent.getParcelableExtra("viewUserDTO");
            Log.d(TAG, "checkClassType(): viewUserDTO " + viewUserDTO);

            if (objectEnum instanceof Alcohol) {
                Alcohol alcohol = (Alcohol) objectEnum;
                tvTitle.setText(getString(R.string.question_alcohol));
                radioButton5.setVisibility(View.VISIBLE);

                setupRadioButtons(alcohol, Alcohol.values());

            } else if (objectEnum instanceof Smoking) {
                Smoking smoking = (Smoking) objectEnum;
                tvTitle.setText(getString(R.string.question_smoking));
                radioButton5.setVisibility(View.VISIBLE);

                setupRadioButtons(smoking, Smoking.values());

            } else if (objectEnum instanceof Psychotype) {
                Psychotype psychotype = (Psychotype) objectEnum;
                tvTitle.setText(getString(R.string.question_psychotype));
                radioButton5.setVisibility(View.GONE);

                setupRadioButtons(psychotype, Psychotype.values());
            }
        }
    }

    private <T extends Enum<T>> void setupRadioButtons(T selectedValue, T[] options) {
        for (int i = 0; i < options.length; i++) {
            T option = options[i];
            RadioButton radioButton = getRadioButton(i + 1);
            radioButton.setText(option.toString());
            radioButton.setTag(option);
            if (selectedValue == option) {
                radioButton.setChecked(true);
            }
        }
    }

    private RadioButton getRadioButton(int index) {
        return switch (index) {
            case 1 -> radioButton1;
            case 2 -> radioButton2;
            case 3 -> radioButton3;
            case 4 -> radioButton4;
            case 5 -> radioButton5;
            default -> throw new IllegalArgumentException("Invalid RadioButton index: " + index);
        };
    }

    private void setupClickListeners() {
        imageCloseIcon.setOnClickListener(v -> finish());

        imageRightIcon.setOnClickListener(v -> sendSelectedObjectOnEditProfileActivity());
    }

    private void sendSelectedObjectOnEditProfileActivity(){
        Object selectedObject = getSelectedObject();
        Intent resultIntent = new Intent();
        Log.d(TAG, "sendSelectedObjectOnEditProfileActivity: " + selectedObject);

        if (selectedObject instanceof Smoking) {
            viewUserDTO.setSmoking((Smoking) selectedObject);
        } else if (selectedObject instanceof Alcohol) {
            viewUserDTO.setAlcohol((Alcohol) selectedObject);
        } else if (selectedObject instanceof Psychotype) {
            viewUserDTO.setPsychotype((Psychotype) selectedObject);
        }

        Log.d(TAG, "sendSelectedObjectOnEditProfileActivity: viewUserDTO " + viewUserDTO);
        resultIntent.putExtra("updateUser", (Parcelable) viewUserDTO);
        setResult(RESULT_OK, resultIntent);
        finish();
    }


    private Object getSelectedObject() {
        Object selectedObject = null;
        int selectedRadioButtonId = radioGroup.getCheckedRadioButtonId();
        RadioButton selectedRadioButton = findViewById(selectedRadioButtonId);

        if (selectedRadioButton != null) {
            selectedObject = selectedRadioButton.getTag();
        }

        return selectedObject;
    }



}
