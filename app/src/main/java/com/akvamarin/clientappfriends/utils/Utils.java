package com.akvamarin.clientappfriends.utils;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;

public class Utils {

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static int getUserAgeVK(String dateOfBirth){
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d.M.yyyy");
        LocalDate birthDate = LocalDate.parse(dateOfBirth, formatter);
        Period years = Period.between(birthDate, today);
        int age = years.getYears();
        return age;
    }

    public static int getAgeWithCalendar(int year, int month, int day) {
        Calendar dateOfBirth = Calendar.getInstance();
        Calendar today = Calendar.getInstance();

        dateOfBirth.set(year, month, day);

        int age = today.get(Calendar.YEAR) - dateOfBirth.get(Calendar.YEAR);

        if (today.get(Calendar.DAY_OF_YEAR) < dateOfBirth.get(Calendar.DAY_OF_YEAR)) {
            age--;
        }

        return age;
    }
}
