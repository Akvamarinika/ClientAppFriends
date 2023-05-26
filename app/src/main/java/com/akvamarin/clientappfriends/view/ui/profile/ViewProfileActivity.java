package com.akvamarin.clientappfriends.view.ui.profile;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.akvamarin.clientappfriends.API.presentor.userdata.UserCallback;
import com.akvamarin.clientappfriends.API.presentor.userdata.UserDataApi;
import com.akvamarin.clientappfriends.BaseActivity;
import com.akvamarin.clientappfriends.R;
import com.akvamarin.clientappfriends.domain.dto.CityDTO;
import com.akvamarin.clientappfriends.domain.dto.ViewUserDTO;
import com.akvamarin.clientappfriends.domain.enums.Sex;
import com.akvamarin.clientappfriends.utils.Utils;
import com.akvamarin.clientappfriends.view.dialog.ErrorDialog;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.squareup.picasso.Picasso;

import java.time.LocalDate;

public class ViewProfileActivity extends BaseActivity {
    private static final String TAG = "ViewProfileActivity";
    private ImageView iconImageBack;
    private ImageView imageProfileAvatar;
    private LinearLayout layoutInfoNameAge;
    private TextView textViewInfoUserName;
    private TextView textViewInfoCountryCity;
    private TextView titleTextChips;
    private ChipGroup chipGroupInfo;
    private Chip chipInfoGender;
    private Chip chipInfoAlcohol;
    private Chip chipInfoSmoking;
    private Chip chipInfoPsychotype;
    private TextView tvInfoTitleAbout;
    private TextView tvInfoAboutMe;

    private UserDataApi userDataApi;
    private String loading;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_profile);

        initWidgets();

        Intent intent = getIntent();
        if (intent != null) {
            Long userId = (Long) intent.getSerializableExtra("userId");

            if (userId != null) {
                initUser(userId);
            }
        }
    }

    private void initWidgets() {
        iconImageBack = findViewById(R.id.iconImageBack);
        imageProfileAvatar = findViewById(R.id.profile_picture);
        layoutInfoNameAge = findViewById(R.id.layout_info_name_age);
        textViewInfoUserName = findViewById(R.id.textViewInfoUserName);
        textViewInfoCountryCity = findViewById(R.id.textViewInfoCountryCity);
        titleTextChips = findViewById(R.id.title_text_chips);
        chipGroupInfo = findViewById(R.id.chip_group_info);
        chipInfoGender = findViewById(R.id.chip_info_gender);
        chipInfoAlcohol = findViewById(R.id.chip_info_alcohol);
        chipInfoSmoking = findViewById(R.id.chip_info_smoking);
        chipInfoPsychotype = findViewById(R.id.chip_info_psychotype);
        tvInfoTitleAbout = findViewById(R.id.tv_info_title_about);
        tvInfoAboutMe = findViewById(R.id.tv_info_about_me);

        userDataApi = new UserDataApi(getApplicationContext());
        loading = getApplicationContext().getString(R.string.loading);
    }

    /** Получение данных
     * пользователя по ID
     * */
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void initUser(Long userId){
        showProgressDialog(loading);

        userDataApi.getUserById(userId, new UserCallback() {
            @Override
            public void onUserRetrieved(ViewUserDTO viewUserDTO) {
                dismissProgressDialog();
                setUserData(viewUserDTO);
                Log.d(TAG, "user ID: " + viewUserDTO.getId());
            }

            @Override
            public void onUserRetrievalError(int responseCode) {
                dismissProgressDialog();
                showErrorDialog(responseCode);
            }
        });
    }

    private void setUserData(ViewUserDTO viewUserDTO) {
        LocalDate birthday = LocalDate.parse(viewUserDTO.getDateOfBirthday());
        int age = Utils.getAgeWithCalendar(birthday.getYear(), birthday.getMonthValue(), birthday.getDayOfMonth());
        String nameUser = String.format("%s, %s ", viewUserDTO.getNickname(), age);
        textViewInfoUserName.setText(nameUser);

        CityDTO city = viewUserDTO.getCityDTO();
        String cityCountry = String.format("%s, %s", city.getCountryName(), city.getName());
        textViewInfoCountryCity.setText(cityCountry);

        if (viewUserDTO.getSex() == Sex.MALE) {
            chipInfoGender.setChipIconResource(R.drawable.ic_add_active_men);
        } else {
            chipInfoGender.setChipIconResource(R.drawable.ic_add_active_girl);
        }

        chipInfoGender.setText(viewUserDTO.getSex().toString());
        chipInfoAlcohol.setText(viewUserDTO.getAlcohol().toString());
        chipInfoSmoking.setText(viewUserDTO.getSmoking().toString());
        chipInfoPsychotype.setText(viewUserDTO.getPsychotype().toString());

        if (viewUserDTO.getAboutMe() != null && !viewUserDTO.getAboutMe().isEmpty()) {
            tvInfoTitleAbout.setVisibility(View.VISIBLE);
            tvInfoAboutMe.setVisibility(View.VISIBLE);
            tvInfoAboutMe.setText(viewUserDTO.getAboutMe());
        } else {
            tvInfoTitleAbout.setVisibility(View.GONE);
            tvInfoAboutMe.setVisibility(View.GONE);
        }

        Log.d(TAG, "viewUserDTO.getUrlAvatar() " + viewUserDTO.getUrlAvatar());
        if (!TextUtils.isEmpty(viewUserDTO.getUrlAvatar())) {
            Picasso.get().load(viewUserDTO.getUrlAvatar()) // Load the image from the URL
                    .error(R.drawable.no_avatar)
                    .into(imageProfileAvatar); // Set to ImageView
        } else {  // If no photo URL
            imageProfileAvatar.setImageResource(R.drawable.no_avatar);
        }
    }

    private void showErrorDialog(int responseCode) {
        ErrorDialog dialog = new ErrorDialog(ViewProfileActivity.this, responseCode);
        dialog.show();
    }
}