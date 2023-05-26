package com.akvamarin.clientappfriends.domain.enums;

import lombok.AllArgsConstructor;

import java.io.Serializable;

@AllArgsConstructor
public enum Smoking implements Serializable {
    NO("нет"),
    I_QUIT("бросаю"),
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
            case NO -> 1;
            case I_QUIT-> 2;
            case OFTEN -> 3;
            case RARELY -> 4;
            default -> 0;
        };
    }

    public static Smoking findSmokingByValue(String value) {
        for (Smoking smoking : Smoking.values()) {
            if (smoking.rusValue.equals(value)) {
                return smoking;
            }
        }
        return null; // not found
    }

}
