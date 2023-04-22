package com.akvamarin.clientappfriends.domain.enums;

import androidx.annotation.NonNull;

import java.io.Serializable;

public enum DayOfWeek implements Serializable {
    MONDAY("понедельник"),
    TUESDAY("вторник"),
    WEDNESDAY("среда"),
    THURSDAY("четверг"),
    FRIDAY("пятница"),
    SATURDAY("суббота"),
    SUNDAY("воскресенье");

    private String rusName;

    DayOfWeek(String rusName) {
        this.rusName = rusName;
    }

    @NonNull
    @Override
    public String toString() {
        return rusName;
    }
}
