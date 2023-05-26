package com.akvamarin.clientappfriends.domain.enums;

import lombok.AllArgsConstructor;

import java.io.Serializable;

@AllArgsConstructor
public enum Alcohol implements Serializable {
    FOR_COMPANY("за компанию"),
    NEVER("никогда"),
    OFTEN("часто"),
    RARELY("редко"),
    NO_ANSWER("нет ответа");

    private final String rusValue;

    @Override
    public String toString() {
        return rusValue;
    }

    public int getNumberValue(){
        return switch (this) {
            case FOR_COMPANY -> 1;
            case OFTEN -> 2;
            case NEVER -> 3;
            case RARELY -> 4;
            default -> 0;
        };
    }

    public static Alcohol findAlcoholByValue(String value) {
        for (Alcohol alcohol : Alcohol.values()) {
            if (alcohol.rusValue.equals(value)) {
                return alcohol;
            }
        }
        return null; // not found
    }
}
