package com.akvamarin.clientappfriends.view.ui.home.filter;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CheckBoxItem {
    private Long id;
    private String text;
    private boolean checked;

    public CheckBoxItem(String text) {
        this.text = text;
        this.checked = false;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }
}

