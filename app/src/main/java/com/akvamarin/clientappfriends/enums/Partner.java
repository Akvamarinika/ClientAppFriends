package com.akvamarin.clientappfriends.enums;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.akvamarin.clientappfriends.R;
import com.akvamarin.clientappfriends.dto.Event;
import com.akvamarin.clientappfriends.ui.home.EventViewHolder;

public enum Partner {
    MAN("Парень"),
    GIRL("Девушка"),
    COMPANY("Компания"),
    ALL("Любой"),
    UNKNOWN("Неопределен");

    private String rusName;

    Partner(String rusName) {
        this.rusName = rusName;
    }

    @NonNull
    @Override
    public String toString() {
        return rusName;
    }

    public static void setImagePartnerTextView(TextView textViewPartner, Event event, Context context) {
        switch (event.getPartner()) {
            case MAN -> {
                textViewPartner.setText(R.string.event_info_men);
                textViewPartner.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_add_active_men, 0, 0, 0);
            }
            case GIRL -> {
                textViewPartner.setText(R.string.event_info_girl);
                textViewPartner.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_add_active_girl, 0, 0, 0);
            }
            case COMPANY -> {
                Drawable imgCompany = ContextCompat.getDrawable(context, R.drawable.ic_add_active_group);

                if (imgCompany != null) {
                    imgCompany.setBounds(0, 0, imgCompany.getMinimumWidth(), imgCompany.getMinimumHeight());
                    textViewPartner.setCompoundDrawables(imgCompany, null, null, null);
                }

                textViewPartner.setText(R.string.event_info_company);
                //textViewPartner.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_add_active_group, 0, 0, 0);
            }
            default -> {
                textViewPartner.setText(R.string.event_info_all);
                textViewPartner.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_add_active_all, 0, 0, 0);
            }
        }
    }

    public static void setImagePartnerHolder(EventViewHolder holder, Event event){
        switch (event.getPartner()) {
            case MAN -> holder.getPartnerImage().setImageResource(R.drawable.card_ic_men);
            case GIRL -> holder.getPartnerImage().setImageResource(R.drawable.card_ic_girl);
            case COMPANY -> holder.getPartnerImage().setImageResource(R.drawable.card_ic_group);
            default -> holder.getPartnerImage().setImageResource(R.drawable.card_ic_all);
        }
    }

    public static Partner getEnumValue(String value){
        for (Partner partner : Partner.values()){
            if (partner.rusName.equalsIgnoreCase(value)){
                return partner;
            }
        }

        return Partner.UNKNOWN;
    }
}
