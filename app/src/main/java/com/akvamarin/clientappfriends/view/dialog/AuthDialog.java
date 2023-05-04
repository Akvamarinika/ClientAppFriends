package com.akvamarin.clientappfriends.view.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.akvamarin.clientappfriends.R;
import com.akvamarin.clientappfriends.view.AuthenticationActivity;

public class AuthDialog extends Dialog {
    private final Activity activity;

    public AuthDialog(Activity activity) {
        super(activity);
        this.activity = activity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_auth);

        Button btnAuth = findViewById(R.id.dialog_auth_button);
        Button btnCancel = findViewById(R.id.dialog_auth_cancel_button);

        btnAuth.setOnClickListener(view -> {
            Intent intent = new Intent(activity, AuthenticationActivity.class);
            activity.startActivity(intent);
            dismiss();
        });

        btnCancel.setOnClickListener(v -> this.dismiss());
    }
}
