package com.akvamarin.clientappfriends.view.ui.notifications;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.akvamarin.clientappfriends.R;

public class NotificationOrganizerViewHolder extends RecyclerView.ViewHolder {

    private final TextView textNotificationTitle;
    private final TextView textNotification;
    private final TextView textNotificationDateTime;
    private final Button buttonReject;
    private final Button buttonApprove;
    private final TextView textNotificationBtnHidden;
    private final ImageView imageAvatarParticipant;
    private int position;

    public NotificationOrganizerViewHolder(@NonNull View itemNotificationView, INotificationOrganizerListener notificationListener) {
        super(itemNotificationView);

        /* получаем эл-ты одной типовой карточки: */
        textNotificationTitle = itemNotificationView.findViewById(R.id.textNotificationTitle);
        textNotification = itemNotificationView.findViewById(R.id.textNotification);
        textNotificationDateTime = itemNotificationView.findViewById(R.id.textNotificationDateTime);
        buttonReject = itemNotificationView.findViewById(R.id.buttonReject);
        buttonApprove = itemNotificationView.findViewById(R.id.buttonApprove);
        textNotificationBtnHidden = itemNotificationView.findViewById(R.id.textNotificationBtnHidden);
        imageAvatarParticipant = itemNotificationView.findViewById(R.id.imageAvatarParticipant);

        // Click listeners:
        buttonReject.setOnClickListener(v -> {
            if (notificationListener != null) {
                notificationListener.onRejectButtonClick(position);
            }
        });

        buttonApprove.setOnClickListener(v -> {
            if (notificationListener != null) {
                notificationListener.onApproveButtonClick(position);
            }
        });

        imageAvatarParticipant.setOnClickListener(v -> {
            if (notificationListener != null) {
                notificationListener.onAvatarImageClick(position);
            }
        });

    }

    public void setPosition(int position){
        this.position = position;
    }

    public TextView getTextNotificationTitle() {
        return textNotificationTitle;
    }

    public TextView getTextNotification() {
        return textNotification;
    }

    public TextView getTextNotificationDateTime() {
        return textNotificationDateTime;
    }

    public Button getButtonReject() {
        return buttonReject;
    }

    public Button getButtonApprove() {
        return buttonApprove;
    }

    public TextView getTextNotificationBtnHidden() {
        return textNotificationBtnHidden;
    }

    public ImageView getImageAvatarParticipant() {
        return imageAvatarParticipant;
    }

}
