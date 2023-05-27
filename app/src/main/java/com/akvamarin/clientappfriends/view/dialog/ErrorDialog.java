package com.akvamarin.clientappfriends.view.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.akvamarin.clientappfriends.R;

public class ErrorDialog extends Dialog {
    private final int responseCode;

    public ErrorDialog(Activity activity, int responseCode) {
        super(activity);
        this.responseCode = responseCode;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_error);

        Button btnCancel = findViewById(R.id.dialog_error_close_button);
        TextView errorDescription = findViewById(R.id.dialog_error_description);

        if (responseCode != 0){
            String error = getErrorMessage(responseCode);
            errorDescription.setText(error);
        }

        btnCancel.setOnClickListener(v -> this.dismiss());
    }

    private String getErrorMessage(int responseCode) {
        return switch (responseCode) {
            case 400 -> "400 Bad Request: что-то пошло не так.";
            case 401 -> "401 Unauthorized: вы неавторизованы для данного действия.";
            case 403 -> "403 Forbidden: у Вас нет доступа к этим ресурсам.";
            case 404 -> "404 Not Found: данный объект не найден.";
            default -> "Проверьте доступность сети Интернет и выполните запрос позже.";
        };
    }
}
