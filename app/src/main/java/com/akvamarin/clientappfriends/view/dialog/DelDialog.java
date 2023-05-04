package com.akvamarin.clientappfriends.view.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.widget.Button;

import com.akvamarin.clientappfriends.R;

public class DelDialog extends Dialog {
    private final Activity activity;
    private final DelDialogListener listener;

    public DelDialog(Activity activity, DelDialogListener listener) {
        super(activity);

        if (activity == null) {
            throw new IllegalArgumentException("Activity cannot be null");
        }

        this.activity = activity;
        this.listener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_del);

        Button btnDel = findViewById(R.id.dialog_del_button);
        Button btnCancel = findViewById(R.id.dialog_del_cancel_button);

        btnDel.setOnClickListener(view -> {
            if (listener != null) {
                listener.onDeleteButtonClick();
            }

            dismiss();
        });

        btnCancel.setOnClickListener(v -> this.dismiss());
    }


}
