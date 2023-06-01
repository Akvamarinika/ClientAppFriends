package com.akvamarin.clientappfriends.view.ui.profile;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.RequiresApi;

import com.akvamarin.clientappfriends.api.presentor.BaseCallback;
import com.akvamarin.clientappfriends.api.presentor.locationdata.CityCallback;
import com.akvamarin.clientappfriends.api.presentor.locationdata.LocationDataApi;
import com.akvamarin.clientappfriends.api.presentor.userdata.UserCallback;
import com.akvamarin.clientappfriends.api.presentor.userdata.UserDataApi;
import com.akvamarin.clientappfriends.BaseActivity;
import com.akvamarin.clientappfriends.R;
import com.akvamarin.clientappfriends.domain.dto.CityDTO;
import com.akvamarin.clientappfriends.domain.dto.UserDTO;
import com.akvamarin.clientappfriends.domain.dto.ViewUserDTO;
import com.akvamarin.clientappfriends.domain.enums.Sex;
import com.akvamarin.clientappfriends.utils.CheckerFields;
import com.akvamarin.clientappfriends.utils.PreferenceManager;
import com.akvamarin.clientappfriends.view.aboutuser.AboutQuestionsActivity;
import com.akvamarin.clientappfriends.view.dialog.ErrorDialog;
import com.akvamarin.clientappfriends.view.register.RegisterPhotoActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class EditProfileActivity extends BaseActivity {
    private static final String TAG = "EditProfileActivity";
    private View includeCustomToolbar;
    private TextInputLayout editProfileTextInputLayoutUserName;
    private TextInputEditText editTextName;
    private RadioGroup editProfileRadioGroupGender;
    private RadioButton editProfilerRadioMen;
    private RadioButton editProfilerRadioGirl;
    private TextInputLayout textInputLayoutRegCity;
    private TextInputLayout textInputLayoutAboutMeDescription;
    private TextInputEditText editTextAboutMeDescription;
    private ImageView editProfileProfileAvatar;
    private ImageView imageIconEditAvatar;
    private ImageView iconImageBack;
    private LinearLayout layoutAlcohol;
    private LinearLayout layoutSmoking;
    private LinearLayout layoutPsychotype;
    private TextView tvAlcohol;
    private TextView tvSmoking;
    private TextView tvPsychotype;
    private MaterialButton buttonUpdateUserProfile;

    private AutoCompleteTextView autoCompleteTextViewCity;
    private ArrayAdapter<CityDTO> mAutoCompleteAdapter;
    private List<CityDTO> citiesList = new ArrayList<>();
    private ActivityResultLauncher<Intent> aboutQuestionsLauncher;
    private PreferenceManager preferenceManager;
    private UserDataApi userDataApi;
    private LocationDataApi locationDataApi;
    private ViewUserDTO viewUserDTO;
    private Long cityId;
    private String loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        initWidgets();
        initComponents();
        setupClickListeners();
        loadCities();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && viewUserDTO == null) {
            initFormFields();
        }

        //init ActivityResultLauncher
        aboutQuestionsLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null) {
                            viewUserDTO = data.getParcelableExtra("updateUser");
                            Log.d(TAG, "onActivityResult: viewUserDTO " + viewUserDTO);

                            tvAlcohol.setText(viewUserDTO.getAlcohol().toString());
                            tvSmoking.setText(viewUserDTO.getSmoking().toString());
                            tvPsychotype.setText(viewUserDTO.getPsychotype().toString());
                        }
                    }
                }
        );
    }

    private void initWidgets() {
        includeCustomToolbar = findViewById(R.id.includeCustomToolbar);

        editProfileTextInputLayoutUserName = findViewById(R.id.editProfileTextInputLayoutUserName);
        editTextName = findViewById(R.id.editTextName);

        editProfileRadioGroupGender = findViewById(R.id.editProfileRadioGroupGender);
        editProfilerRadioMen = findViewById(R.id.editProfilerRadioMen);
        editProfilerRadioGirl = findViewById(R.id.editProfilerRadioGirl);

        textInputLayoutRegCity = findViewById(R.id.textInputLayoutRegCity);
        autoCompleteTextViewCity = findViewById(R.id.autoCompleteTextViewCity);

        textInputLayoutAboutMeDescription = findViewById(R.id.textInputLayoutAboutMeDescription);
        editTextAboutMeDescription = findViewById(R.id.editTextAboutMeDescription);

        editProfileProfileAvatar = findViewById(R.id.editProfileProfileAvatar);
        imageIconEditAvatar = findViewById(R.id.image_edit_avatar);
        iconImageBack = findViewById(R.id.iconImageBack);

        layoutAlcohol = findViewById(R.id.layout_alcohol);
        layoutSmoking = findViewById(R.id.layout_smoking);
        layoutPsychotype = findViewById(R.id.layout_psychotype);

        tvAlcohol = findViewById(R.id.tv_alcohol);
        tvSmoking = findViewById(R.id.tv_smoking);
        tvPsychotype = findViewById(R.id.tv_psychotype);

        buttonUpdateUserProfile = findViewById(R.id.buttonUpdateUserProfile);
    }

    private void initComponents(){
        mAutoCompleteAdapter = new ArrayAdapter<>(EditProfileActivity.this,
                android.R.layout.simple_dropdown_item_1line, citiesList);
        preferenceManager = new PreferenceManager(getApplicationContext());
        userDataApi = new UserDataApi(getApplicationContext());
        locationDataApi = new LocationDataApi(getApplicationContext());
        loading = getApplicationContext().getString(R.string.loading);
    }

    @SuppressLint("NewApi")
    private void setupClickListeners() {
        layoutAlcohol.setOnClickListener(v -> openAboutQuestionsActivity(viewUserDTO.getAlcohol()));
        layoutSmoking.setOnClickListener(v -> openAboutQuestionsActivity(viewUserDTO.getSmoking()));
        layoutPsychotype.setOnClickListener(v -> openAboutQuestionsActivity(viewUserDTO.getPsychotype()));

        autoCompleteTextViewCity.setOnItemClickListener((parent, view, position, id) -> {
            CityDTO selectedCity = (CityDTO) parent.getItemAtPosition(position);
            autoCompleteTextViewCity.setTooltipText(selectedCity.getName());
            cityId = selectedCity.getId();
            Log.d(TAG, "Selected city: " + selectedCity.getName());
        });

        imageIconEditAvatar.setOnClickListener(v -> {
            if (viewUserDTO != null) {
                Intent intent = new Intent(this, RegisterPhotoActivity.class);
                intent.putExtra("urlAvatar", viewUserDTO.getUrlAvatar());
                startActivity(intent);
            }
        });

        buttonUpdateUserProfile.setOnClickListener(v -> {
            if (checkFields()) {
                UserDTO userDTO = createUserDTO();

                if (cityId != null) {
                    userDTO.setCityID(cityId);
                } else if (viewUserDTO != null) {
                    userDTO.setCityID(viewUserDTO.getCityDTO().getId());
                }

                sendUserOnServerForUpdate(userDTO);
            }
        });

        iconImageBack.setOnClickListener(v -> finish());
    }

    private void openAboutQuestionsActivity(Enum<?> objectEnum) {
        if (viewUserDTO != null) {
            Intent intent = new Intent(getApplicationContext(), AboutQuestionsActivity.class);
            intent.putExtra("objectEnum", objectEnum);
            intent.putExtra("viewUserDTO", (Parcelable) viewUserDTO);
            aboutQuestionsLauncher.launch(intent);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void initFormFields(){
        showProgressDialog(loading);
        userDataApi.getUserByLogin(new UserCallback() {
            @Override
            public void onUserRetrieved(ViewUserDTO user) {
                dismissProgressDialog();
                viewUserDTO = user;
                editTextName.setText(user.getNickname());
                autoCompleteTextViewCity.setText(user.getCityDTO().getName());
                editTextAboutMeDescription.setText(user.getAboutMe());

                switch (user.getSex()) {
                    case MALE -> editProfilerRadioMen.setChecked(true);
                    case FEMALE -> editProfilerRadioGirl.setChecked(true);
                }

                if (!TextUtils.isEmpty(user.getUrlAvatar())) {
                    Picasso.get().load(user.getUrlAvatar())
                            .error(R.drawable.no_avatar)
                            .into(editProfileProfileAvatar);
                } else {
                    editProfileProfileAvatar.setImageResource(R.drawable.no_avatar);
                }
            }

            @Override
            public void onUserRetrievalError(int responseCode) {
                dismissProgressDialog();
                showErrorDialog(responseCode);
            }
        });
    }

    private void loadCities(){
        showProgressDialog(loading);
        if (citiesList.isEmpty()) {
            locationDataApi.loadCityFromServer(new CityCallback() {
                @Override
                public void onCityRetrieved(List<CityDTO> cities) {
                    dismissProgressDialog();
                    citiesList = cities;
                    mAutoCompleteAdapter = new ArrayAdapter<>(EditProfileActivity.this,
                            android.R.layout.simple_dropdown_item_1line, citiesList);
                    autoCompleteTextViewCity.setAdapter(mAutoCompleteAdapter);
                }

                @Override
                public void onCityRetrievalError(int responseCode) {
                    dismissProgressDialog();
                    showErrorDialog(responseCode);
                }
            });
        }
    }

    private boolean checkFields(){
        CheckerFields checkerFields = new CheckerFields(getApplicationContext());
        boolean isNotEmptyName = checkerFields.isTextFieldsNotEmpty(editTextName, editProfileTextInputLayoutUserName);
        boolean hasPatternName = checkerFields.checkSymbolsInName(editTextName, editProfileTextInputLayoutUserName);
        boolean hasCity = checkerFields.containsCityInList(autoCompleteTextViewCity, textInputLayoutRegCity, citiesList);

        return isNotEmptyName && hasPatternName && hasCity;
    }

    /*** Создание dto-объекта при
     * методе Update:
     * ***/
    @RequiresApi(api = Build.VERSION_CODES.O)
    private UserDTO createUserDTO() {
        Sex sex = getSelectedGender();
        String nickname = editTextName.getText() != null ? editTextName.getText().toString().trim() : "";
        String aboutMe = editTextAboutMeDescription.getText() != null ? editTextAboutMeDescription.getText().toString().trim() : "";

        viewUserDTO.setSex(sex);
        viewUserDTO.setNickname(nickname);
        viewUserDTO.setAboutMe(aboutMe);

        return UserDTO.builder()
                .id(viewUserDTO.getId())
                .username(viewUserDTO.getUsername())
                .email(viewUserDTO.getEmail())
                .nickname(nickname)
                .sex(sex)
                .aboutMe(aboutMe)
                .alcohol(viewUserDTO.getAlcohol())
                .smoking(viewUserDTO.getSmoking())
                .psychotype(viewUserDTO.getPsychotype())
                .build();
    }

    private Sex getSelectedGender() {
        int selectedRadioButtonId = editProfileRadioGroupGender.getCheckedRadioButtonId();
        RadioButton selectedRadioButton = findViewById(selectedRadioButtonId);

        return switch (selectedRadioButton.getId()) {
            case R.id.editProfilerRadioMen -> Sex.MALE;
            case R.id.editProfilerRadioGirl -> Sex.FEMALE;
            default -> Sex.NO_ANSWER;
        };
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    private void sendUserOnServerForUpdate(UserDTO userDTO){
        showProgressDialog(loading);
        if (userDTO != null) {
            userDataApi.updateUserProfile(userDTO, new BaseCallback() {
                @Override
                public void onRetrieved() {
                    dismissProgressDialog();
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("viewUserDTO", (Parcelable) viewUserDTO);
                    setResult(RESULT_OK, resultIntent);
                    finish();
                }

                @Override
                public void onRetrievalError(int responseCode) {
                    dismissProgressDialog();
                    showErrorDialog(responseCode);
                }
            });
        }
    }

    private void showErrorDialog(int responseCode) {
        ErrorDialog dialog = new ErrorDialog(EditProfileActivity.this, responseCode);
        dialog.show();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onResume() {
        super.onResume();
        if (viewUserDTO == null) {
            initFormFields();
        }
    }

}
