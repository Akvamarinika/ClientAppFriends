package com.akvamarin.clientappfriends.domain.enums;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.akvamarin.clientappfriends.R;
import com.akvamarin.clientappfriends.domain.dto.ViewEventDTO;
import com.akvamarin.clientappfriends.view.ui.home.EventViewHolder;

import java.io.Serializable;

public enum Partner implements Serializable {
    MAN("Парень"),
    WOMAN("Девушка"),
    COMPANY("Компания"),
    ANY("Любой"),
    UNKNOWN("Неизвестно");

    private final String rusName;

    Partner(String rusName) {
        this.rusName = rusName;
    }

    @NonNull
    @Override
    public String toString() {
        return rusName;
    }

    public static void setImagePartnerTextView(TextView textViewPartner, ViewEventDTO event, Context context) {
        Partner partner = event.getPartner();

        if (partner == null) {
            return;
        }

        switch (partner) {
            case MAN -> {
                textViewPartner.setText(R.string.event_info_men);
                textViewPartner.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_add_active_men, 0, 0, 0);
            }
            case WOMAN -> {
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

    public static void setImagePartnerHolder(EventViewHolder holder, ViewEventDTO event){
        Partner partner = event.getPartner();

        if (partner == null) {
            return;
        }

        switch (partner) {
            case MAN -> holder.getPartnerImage().setImageResource(R.drawable.card_ic_men);
            case WOMAN -> holder.getPartnerImage().setImageResource(R.drawable.card_ic_girl);
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

    public static int getIdCheckedElement(Partner partner){
        int radioButtonManId = R.id.radioMen;
        int radioButtonGirlId = R.id.radioGirl;
        int radioButtonCompanyId = R.id.radioCompany;
        int radioButtonAnyId = R.id.radioAll;

        return switch (partner) {
            case MAN -> radioButtonManId;
            case WOMAN -> radioButtonGirlId;
            case COMPANY -> radioButtonCompanyId;
            default -> radioButtonAnyId;
        };
    }
}
