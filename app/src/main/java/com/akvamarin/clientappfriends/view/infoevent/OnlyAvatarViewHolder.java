package com.akvamarin.clientappfriends.view.infoevent;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.akvamarin.clientappfriends.R;

public class OnlyAvatarViewHolder extends RecyclerView.ViewHolder {
    private final ImageView imageViewAvatar;
    private final TextView textViewMore;

    public OnlyAvatarViewHolder(@NonNull View itemView) {
        super(itemView);
        imageViewAvatar = itemView.findViewById(R.id.avatarImageView);
        textViewMore = itemView.findViewById(R.id.textViewMore);
    }

    public ImageView getImageViewAvatar() {
        return imageViewAvatar;
    }

    public TextView getTextViewMore() {
        return textViewMore;
    }
}
