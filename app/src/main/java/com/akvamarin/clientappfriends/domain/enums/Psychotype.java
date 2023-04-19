package com.akvamarin.clientappfriends.domain.enums;

import lombok.AllArgsConstructor;

import java.io.Serializable;

@AllArgsConstructor
public enum Psychotype implements Serializable {
    INTROVERT("интроверт"),
    EXTROVERT("экстраверт"),
    MIXED("50 на 50"),
    NO_ANSWER("нет ответа");

    private final String rusValue;

    @Override
    public String toString() {
        return rusValue;
    }

    public int getNumberValue(){
        return switch (this) {
            case INTROVERT -> 1;
            case EXTROVERT -> 2;
            case MIXED -> 3;
            default -> 0;
        };
    }

}
