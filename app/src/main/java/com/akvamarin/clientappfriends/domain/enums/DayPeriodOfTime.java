package com.akvamarin.clientappfriends.domain.enums;

import android.widget.TextView;

import androidx.annotation.NonNull;

import com.akvamarin.clientappfriends.R;
import com.akvamarin.clientappfriends.domain.dto.Event;
import com.akvamarin.clientappfriends.domain.dto.ViewEventDTO;

import java.io.Serializable;

public enum DayPeriodOfTime implements Serializable {
    MORNING("утро"),
    AFTERNOON("день"),
    EVENING("вечер"),
    NIGHT("ночь"),
    UNKNOWN("Неопределен");

    private String rusName;

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
}
