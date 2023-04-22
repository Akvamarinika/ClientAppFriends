package com.akvamarin.clientappfriends.view;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.akvamarin.clientappfriends.R;
import com.akvamarin.clientappfriends.domain.dto.CityDTO;
import com.akvamarin.clientappfriends.domain.dto.ViewEventDTO;
import com.akvamarin.clientappfriends.domain.dto.ViewUserSlimDTO;
import com.akvamarin.clientappfriends.domain.enums.DayOfWeek;
import com.akvamarin.clientappfriends.domain.enums.DayPeriodOfTime;
import com.akvamarin.clientappfriends.domain.enums.Partner;
import com.akvamarin.clientappfriends.utils.BitmapConvertor;
import com.akvamarin.clientappfriends.utils.Constants;
import com.akvamarin.clientappfriends.utils.PreferenceManager;
import com.squareup.picasso.Picasso;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import de.hdodenhof.circleimageview.CircleImageView;

public class InfoEventActivity extends AppCompatActivity {
    private static final String DATE_PATTERN = "d/MM/uuuu";

    private Toolbar toolbar;
    private LinearLayout linearLayoutUserInfo;

    private CircleImageView circleAvatarBig;
    private TextView textViewUserName;
    private TextView textViewCountryCity;
    private TextView textViewEventTitle;
    private TextView textViewPartner;
    private TextView textViewDate;
    private TextView textViewDayOfWeek;
    private TextView textViewTwentyFourHours;
    private TextView textViewDescription;

    private PreferenceManager preferenceManager;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_event);

        initWidgets();
        setValues();

        linearLayoutUserInfo.setOnClickListener(view -> {   //TODO переход в профиль
            Intent intent = new Intent(this, AllEventsActivity.class);
            startActivity(intent);
        });
    }


    private void initWidgets(){
        toolbar = findViewById(R.id.top_toolbar);
        linearLayoutUserInfo = findViewById(R.id.layoutUserInfo);
        circleAvatarBig = findViewById(R.id.imageEventInfoAvatarBig);
        textViewUserName = findViewById(R.id.textViewEventInfoUserName);
        textViewCountryCity = findViewById(R.id.textViewEventInfoCountryCity);
        textViewEventTitle = findViewById(R.id.eventInfoTitle);
        textViewPartner = findViewById(R.id.textViewEventInfoPartner);
        textViewDate = findViewById(R.id.textViewEventInfoDate);
        textViewDayOfWeek = findViewById(R.id.textViewEventInfoDayOfWeek);
        textViewTwentyFourHours = findViewById(R.id.textViewEventInfoTwentyFourHours);
        textViewDescription = findViewById(R.id.textViewEventInfoDescription);

        preferenceManager = new PreferenceManager(getApplicationContext());
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setValues(){
        ViewEventDTO event = (ViewEventDTO) getIntent().getSerializableExtra("current_event"); // Parcelable ?
        ViewUserSlimDTO user = event.getUserOwner();
        CityDTO city = user.getCityDTO();

        if (user.getUrlAvatar().isEmpty()){              //TODO вынести блок
            circleAvatarBig.setImageResource(R.drawable.no_avatar); //R.drawable.no_avatar
        } else {
            Picasso.get()
                    .load(user.getUrlAvatar())
                    .fit()
                    .error(R.drawable.error_loading_image)
                    .into(circleAvatarBig);
        }

        // костыль.....

       /*
        String imgBase64 = preferenceManager.getString(Constants.KEY_IMAGE_BASE64);
        String email = preferenceManager.getString(Constants.KEY_EMAIL);
        String userEmail = event.getUser().getEmail();
        if (imgBase64 != null && userEmail != null && !imgBase64.equalsIgnoreCase("image")
                && (event.getUser().getEmail().equalsIgnoreCase(email))){
            Bitmap bitmap = BitmapConvertor.convertFromBase64ToBitmap(preferenceManager.getString(Constants.KEY_IMAGE_BASE64));
            circleAvatarBig.setImageBitmap(bitmap);
        }
        */

        String cityCountry = String.format("%s, %s", city.getCountryName(), city.getName());
        textViewCountryCity.setText(cityCountry);

        textViewUserName.setText(user.getNickname());
        textViewEventTitle.setText(event.getName());

        LocalDate eventDate = LocalDate.parse(event.getDate());
        DateTimeFormatter formatters = DateTimeFormatter.ofPattern(DATE_PATTERN);
        String textDate = eventDate.format(formatters);   // формат даты для польз-ля
        textViewDate.setText(textDate);

        textViewDayOfWeek.setText(DayOfWeek.valueOf(eventDate.getDayOfWeek().name()).toString());
        textViewDescription.setText(event.getDescription());

        Partner.setImagePartnerTextView(textViewPartner, event, this);

        textViewTwentyFourHours.setText(event.getPeriodOfTime().toString());
        DayPeriodOfTime.setImageTwentyFourHoursInTextView(textViewTwentyFourHours, event);

    }
}