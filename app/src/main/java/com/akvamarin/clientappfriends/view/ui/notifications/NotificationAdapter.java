package com.akvamarin.clientappfriends.view.ui.notifications;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.akvamarin.clientappfriends.R;
import com.akvamarin.clientappfriends.domain.dto.ViewNotificationDTO;
import com.akvamarin.clientappfriends.domain.enums.FeedbackType;
import com.akvamarin.clientappfriends.view.infoevent.InfoEventActivity;
import com.squareup.picasso.Picasso;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = "recyclerNotification";
    private static final int VIEW_TYPE_ORGANIZER = 1;
    private static final int VIEW_TYPE_PARTICIPANT = 2;

    private final List<ViewNotificationDTO> notificationList;
    private final INotificationOrganizerListener notificationListener;
    private final INotificationParticipantListener participantListener;
    private static final String DATETIME_PATTERN = "dd.MM.yyyy в HH:mm";

    public NotificationAdapter(List<ViewNotificationDTO> notificationList, INotificationOrganizerListener notificationListener,
                               INotificationParticipantListener participantListener) {
        this.notificationList = notificationList;
        this.notificationListener = notificationListener;
        this.participantListener = participantListener;
    }

    @Override
    public int getItemViewType(int position) {
        ViewNotificationDTO viewNotificationDTO = notificationList.get(position);
        return viewNotificationDTO.isForOwner() ? VIEW_TYPE_ORGANIZER : VIEW_TYPE_PARTICIPANT;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view;
        if (viewType == VIEW_TYPE_ORGANIZER) {
            view = inflater.inflate(R.layout.item_notification_for_organizer, parent, false);
            return new NotificationOrganizerViewHolder(view, notificationListener);
        } else {
            view = inflater.inflate(R.layout.item_notification_for_participant, parent, false);
            return new NotificationParticipantViewHolder(view, participantListener);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder Notification: called");
        ViewNotificationDTO viewNotificationDTO = notificationList.get(position);
        String eventName = viewNotificationDTO.getEventName();
        String userNickname = viewNotificationDTO.getUserNickname();
        String userUrlAvatar = viewNotificationDTO.getUserUrlAvatar();
        FeedbackType feedbackType = viewNotificationDTO.getFeedbackType();
        LocalDateTime updatedAt = viewNotificationDTO.getUpdatedAt();
        String updateDateTime = formatDateTime(updatedAt);

        //Organizer ViewHolder
        if (holder instanceof NotificationOrganizerViewHolder) {
            NotificationOrganizerViewHolder organizerViewHolder = (NotificationOrganizerViewHolder) holder;

            //buttons hidden:
            if (notificationList.get(position).isHiddenBtn()) {
                organizerViewHolder.getButtonApprove().setVisibility(View.GONE);
                organizerViewHolder.getButtonReject().setVisibility(View.GONE);
                organizerViewHolder.getTextNotificationBtnHidden().setVisibility(View.VISIBLE);
                int hiddenBtnTextResId = feedbackType == FeedbackType.APPROVED ? R.string.text_hidden_btn_organizer_approve : R.string.text_hidden_btn_organizer_reject;
                organizerViewHolder.getTextNotificationBtnHidden().setText(hiddenBtnTextResId);
            } else {
                organizerViewHolder.getButtonApprove().setVisibility(View.VISIBLE);
                organizerViewHolder.getButtonReject().setVisibility(View.VISIBLE);
                organizerViewHolder.getTextNotificationBtnHidden().setVisibility(View.GONE);
            }

            organizerViewHolder.getTextNotificationTitle().setText(R.string.text_notification_title_organizer);
            setClickableTextEventName(organizerViewHolder.getTextNotification(), fullTextForOrganizer(userNickname, eventName), eventName, position);

            // Set the image avatar using Picasso:
            if (userUrlAvatar.isEmpty()) {
                organizerViewHolder.getImageAvatarParticipant().setImageResource(R.drawable.no_avatar);
            } else {
                Picasso.get()
                        .load(userUrlAvatar)
                        .fit()
                        .error(R.drawable.error_loading_image)
                        .into(organizerViewHolder.getImageAvatarParticipant());
            }
            organizerViewHolder.getTextNotificationDateTime().setText(updateDateTime);
            organizerViewHolder.setPosition(position);
        //Participant ViewHolder
        } else if (holder instanceof NotificationParticipantViewHolder) {
            NotificationParticipantViewHolder participantViewHolder = (NotificationParticipantViewHolder) holder;

            //button hidden:
            if (notificationList.get(position).isHiddenBtn()) {
                participantViewHolder.getButtonHidden().setVisibility(View.GONE);
                participantViewHolder.getTextNotificationBtnHidden().setVisibility(View.VISIBLE);
            } else {
                participantViewHolder.getButtonHidden().setVisibility(View.VISIBLE);
                participantViewHolder.getTextNotificationBtnHidden().setVisibility(View.GONE);
            }

            int titleResId = feedbackType == FeedbackType.APPROVED ? R.string.text_notification_title_approved : R.string.text_notification_title_rejected;
            participantViewHolder.getTextNotificationTitleParticipant().setText(titleResId);
            setClickableTextEventName(participantViewHolder.getTextNotificationParticipant(), fullTextForParticipant(feedbackType, eventName), eventName, position);

            // Set the image avatar using Picasso:
            if (userUrlAvatar.isEmpty()) {
                participantViewHolder.getImageAvatarOrganizer().setImageResource(R.drawable.no_avatar);
            } else {
                Picasso.get()
                        .load(userUrlAvatar)
                        .fit()
                        .error(R.drawable.error_loading_image)
                        .into(participantViewHolder.getImageAvatarOrganizer());
            }

            participantViewHolder.getTextNotificationDateTime().setText(updateDateTime);
            participantViewHolder.setPosition(position);
        }

        holder.itemView.setTag(position);
    }

    private String fullTextForOrganizer(String userNickname, String eventName) {
        return "Пользователь " + userNickname + " подал заявку на Ваше мероприятие " + eventName;
    }

    private String fullTextForParticipant(FeedbackType feedbackType, String eventName) {
        if (feedbackType == FeedbackType.APPROVED) {
            return "Вашу заявку на участие в мероприятии " + eventName + " одобрил организатор мероприятия";
        } else {
            return "К сожалению, Ваша заявка на участие в мероприятии " + eventName + " была отклонена организатором";
        }
    }

    private void setClickableTextEventName(TextView textView, String fullText, String clickableText, int position) {
        SpannableString spannableString = new SpannableString(fullText);
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                if (position != RecyclerView.NO_POSITION) {
                    ViewNotificationDTO currentNotification = notificationList.get(position);
                    Intent intent = new Intent(widget.getContext(), InfoEventActivity.class);
                    intent.putExtra("current_event_id", currentNotification.getEventId()); // event id
                    widget.getContext().startActivity(intent);
                }
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                ds.setUnderlineText(false); // Remove underline
                ds.setColor(textView.getContext().getColor(R.color.colorPrimaryDark)); // Custom text color
                ds.setTypeface(Typeface.DEFAULT_BOLD);
            }
        };

        int clickableTextStart = fullText.indexOf(clickableText);
        int clickableTextEnd = clickableTextStart + clickableText.length();

        spannableString.setSpan(clickableSpan, clickableTextStart, clickableTextEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        textView.setText(spannableString);
        textView.setMovementMethod(LinkMovementMethod.getInstance());
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private String formatDateTime(LocalDateTime dateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATETIME_PATTERN);
        return dateTime.format(formatter);
    }

    @Override
    public int getItemCount() {
        return notificationList.size();
    }
}