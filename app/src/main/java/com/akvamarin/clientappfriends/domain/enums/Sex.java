package com.akvamarin.clientappfriends.domain.enums;


import androidx.annotation.NonNull;

import java.io.Serializable;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum Sex implements Serializable {
    MALE("парень"),
    FEMALE("девушка"),
    NO_ANSWER("нет ответа");

    private final String rusValue;

    @NonNull
    @Override
    public String toString() {
        return rusValue;
    }

    public static Sex convertVKInAppValue(int val){
        return switch (val) {
            case 1 -> Sex.FEMALE;
            case 2 -> Sex.MALE;
            default -> Sex.NO_ANSWER;
        };
    }

    public int getNumberValue(){
        return switch (this) {
            case MALE -> 1;
            case FEMALE -> 2;
            default -> 0;
        };
    }
}
