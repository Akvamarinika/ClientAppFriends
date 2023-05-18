package com.akvamarin.clientappfriends.view.ui.notifications;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.akvamarin.clientappfriends.R;

public class NotificationParticipantViewHolder extends RecyclerView.ViewHolder{

    private final TextView textNotificationTitleParticipant;
    private final TextView textNotificationParticipant;
    private final TextView textNotificationDateTime;
    private final Button buttonHidden;
    private final TextView textNotificationBtnHidden;
    private final ImageView imageAvatarOrganizer;
    private int position;

    public NotificationParticipantViewHolder(@NonNull View itemNotificationView, INotificationParticipantListener notificationListener) {
        super(itemNotificationView);

        /* получаем эл-ты одной типовой карточки: */
        textNotificationTitleParticipant = itemNotificationView.findViewById(R.id.textNotificationTitleParticipant);
        textNotificationParticipant = itemNotificationView.findViewById(R.id.textNotificationParticipant);
        textNotificationDateTime = itemNotificationView.findViewById(R.id.textNotificationDateTime);
        buttonHidden = itemNotificationView.findViewById(R.id.buttonHidden);
        textNotificationBtnHidden = itemNotificationView.findViewById(R.id.textNotificationBtnHidden);
        imageAvatarOrganizer = itemNotificationView.findViewById(R.id.imageAvatarOrganizer);

        // Click listeners:
        buttonHidden.setOnClickListener(v -> {
            if (notificationListener != null) {
                notificationListener.onHiddenButtonClick(position);
            }
        });

        imageAvatarOrganizer.setOnClickListener(v -> {
            if (notificationListener != null) {
                notificationListener.onAvatarOrganizerImageClick(position);
            }
        });
    }

    public void setPosition(int position){
        this.position = position;
    }

    public TextView getTextNotificationTitleParticipant() {
        return textNotificationTitleParticipant;
    }

    public TextView getTextNotificationParticipant() {
        return textNotificationParticipant;
    }

    public TextView getTextNotificationDateTime() {
        return textNotificationDateTime;
    }

    public Button getButtonHidden() {
        return buttonHidden;
    }

    public TextView getTextNotificationBtnHidden() {
        return textNotificationBtnHidden;
    }

    public ImageView getImageAvatarOrganizer() {
        return imageAvatarOrganizer;
    }

}
