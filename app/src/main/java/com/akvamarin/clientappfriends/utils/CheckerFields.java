package com.akvamarin.clientappfriends.utils;

import android.content.Context;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import com.akvamarin.clientappfriends.R;
import com.akvamarin.clientappfriends.addevent.AddEventActivity;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Calendar;
import java.util.List;

public class CheckerFields {
    private static final String TAG = "CheckerFields";
    private static final int MIN_TEXT_LENGTH = 5;
    private static final String EMPTY_STRING = "";
    private final String EMAIL_PATTERN = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    private final String PASS_PATTERN = "[A-Za-z0-9 ]*";
    private final String NAME_PATTERN = "[A-Za-z]*";
    private final Context context;

    public CheckerFields(Context context) {
        this.context = context;
    }

    public void setTypeErrorInUI(){

    }

    /*** Methods check fields: ***/
    public boolean isTextFieldsNotEmpty(EditText editText, TextInputLayout textInputLayout){
        String error = context.getString(R.string.error_empty_field);
        int textLength = editText.getText().toString().trim().length();

        if (textLength == 0) {
            textInputLayout.setError(error);
            return false;
        } else {
            textInputLayout.setError(EMPTY_STRING);
        }

        return true;
    }

    public boolean checkSymbolsInPassword(EditText editText, TextInputLayout textInputLayout) {
        //passLength > 0 && passLength < MIN_TEXT_LENGTH;
        String error = context.getString(R.string.error_check_pass);
        String password = editText.getText().toString().trim();
        int passLength = password.length();
        boolean hasPassPattern = !password.matches(PASS_PATTERN);
        boolean hasUppercase = !password.equals(password.toLowerCase());
        boolean hasLowercase = !password.equals(password.toUpperCase());
        boolean hasMinLength = passLength >= MIN_TEXT_LENGTH;

        if (hasMinLength && hasLowercase && hasUppercase && hasPassPattern) {
            textInputLayout.setError(error);
            return false;
        }

        textInputLayout.setError(EMPTY_STRING);
        return true;
    }

    public boolean isEmail(EditText editText,  TextInputLayout textInputLayout){
        String error = context.getString(R.string.error_email);
        String email = editText.getText().toString().trim();

        if (!email.matches(EMAIL_PATTERN)) {
            textInputLayout.setError(error);
            return false;
        }

        textInputLayout.setError(EMPTY_STRING);
        return  true;
    }

    public boolean isTwoPassContains(EditText editText, EditText editTextTwo, TextInputLayout textInputLayout){
        String error = context.getString(R.string.error_two_pass);
        String pass = editText.getText().toString().trim();
        String repeatPass = editTextTwo.getText().toString().trim();

        if (!pass.equals(repeatPass)){
            textInputLayout.setError(error);
            return false;
        }

        textInputLayout.setError(EMPTY_STRING);
        return  true;
    }

    public boolean checkSymbolsInName(EditText editText, TextInputLayout textInputLayout) {
        String error = context.getString(R.string.error_check_name);
        String name = editText.getText().toString().trim();
        boolean hasPassPattern = !name.matches(NAME_PATTERN);

        if (hasPassPattern) {
            textInputLayout.setError(error);
            return false;
        }

        textInputLayout.setError(EMPTY_STRING);
        return true;
    }

    public boolean containsCityInList(TextView textView, TextInputLayout textInputLayout, List<String> cities) {
        String error = context.getString(R.string.error_check_city);
        String cityName = textView.getText().toString().trim();

        for (String city : cities) {
            if (cityName.equalsIgnoreCase(city)) {
                textInputLayout.setError(EMPTY_STRING);
                return true;
            }
        }

        textInputLayout.setError(error);
        return false;
    }

    public static int getAge(int year, int month, int day) {
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
