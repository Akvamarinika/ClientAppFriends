package com.akvamarin.clientappfriends.view.ui.home;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.akvamarin.clientappfriends.R;

public class EventViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    private final IEventRecyclerListener eventListener;

    private final TextView textViewUserName;
    private final TextView textViewAge;
    private final TextView textViewCategory;
    private final TextView textViewEventName;
    private final TextView textViewDate;
    private final ImageView partnerImage;
    private final ImageView userAvatar;
    private int index;

    public EventViewHolder(@NonNull View itemEventView, IEventRecyclerListener eventListener) {
        super(itemEventView);
        this.eventListener = eventListener;

        /* получаем эл-ты одной типовой карточки: */
        textViewUserName = itemEventView.findViewById(R.id.textViewUserName);
        textViewAge = itemEventView.findViewById(R.id.textViewAge);
        textViewCategory = itemEventView.findViewById(R.id.textViewCategory);
        textViewEventName = itemEventView.findViewById(R.id.textViewEventName);
        textViewDate = itemEventView.findViewById(R.id.textViewDate);
        partnerImage = itemEventView.findViewById(R.id.imageCompany);
        userAvatar = itemEventView.findViewById(R.id.imageCircleAvatar);

        /* нажатие по карточке:  this == OnClickListener elem */
        itemEventView.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        eventListener.onClickRecyclerEventSelected(index);
    }

    public void setIndex(int position){
        index = position;
    }

    public TextView getTextViewUserName() {
        return textViewUserName;
    }

    public TextView getTextViewAge() {
        return textViewAge;
    }

    public TextView getTextViewCategory() {
        return textViewCategory;
    }

    public TextView getTextViewEventName() {
        return textViewEventName;
    }

    public TextView getTextViewDate() {
        return textViewDate;
    }

    public ImageView getPartnerImage() {
        return partnerImage;
    }

    public ImageView getUserAvatar() {
        return userAvatar;
    }
}
