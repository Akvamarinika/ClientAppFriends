package com.akvamarin.clientappfriends.domain.enums;

import android.widget.TextView;

import androidx.annotation.NonNull;

import com.akvamarin.clientappfriends.R;
import com.akvamarin.clientappfriends.domain.dto.ViewEventDTO;

import java.io.Serializable;

public enum DayPeriodOfTime implements Serializable {
    MORNING("утро"),
    AFTERNOON("день"),
    EVENING("вечер"),
    NIGHT("ночь"),
    UNKNOWN("Неопределен");

    private final String rusName;

    DayPeriodOfTime(String rusName) {
        this.rusName = rusName;
    }

    @NonNull
    @Override
    public String toString() {
        return rusName;
    }

    public static void setImageTwentyFourHoursInTextView(TextView textViewTwentyFourHours, ViewEventDTO event){

        switch (event.getPeriodOfTime()) {
            case MORNING -> textViewTwentyFourHours.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_add_hours_morning_active, 0, 0, 0);
            case AFTERNOON -> textViewTwentyFourHours.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_add_hours_day_active, 0, 0, 0);
            case EVENING -> textViewTwentyFourHours.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_add_hours_evening_active, 0, 0, 0);
            default -> textViewTwentyFourHours.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_add_hours_night_active, 0, 0, 0);
        }
    }

    public static DayPeriodOfTime getEnumValue(String value){
        for (DayPeriodOfTime dayPeriodOfTime : DayPeriodOfTime.values()){
            if (dayPeriodOfTime.rusName.equalsIgnoreCase(value)){
                return dayPeriodOfTime;
            }
        }

        return DayPeriodOfTime.UNKNOWN;
    }

    public static int getIdCheckedElement(DayPeriodOfTime dayPeriodOfTime){
        int radioButtonMorningId = R.id.radioMorning;
        int radioButtonDayId = R.id.radioDay;
        int radioButtonEveningId = R.id.radioEvening;
        int radioButtonNightId = R.id.radioNight;

        return switch (dayPeriodOfTime) {
            case MORNING -> radioButtonMorningId;
            case AFTERNOON -> radioButtonDayId;
            case NIGHT -> radioButtonNightId;
            default -> radioButtonEveningId;
        };
    }
}
