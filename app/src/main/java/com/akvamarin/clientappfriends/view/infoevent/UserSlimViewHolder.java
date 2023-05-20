package com.akvamarin.clientappfriends.view.infoevent;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.akvamarin.clientappfriends.R;

public class UserSlimViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
    private final IUserSlimListener userSlimListener;

    private final ImageView avatarImageView;
    private final TextView nicknameTextView;
    private int position;

    public UserSlimViewHolder(View itemUserSlimView, IUserSlimListener userSlimListener) {
        super(itemUserSlimView);
        avatarImageView = itemUserSlimView.findViewById(R.id.avatarImageView);
        nicknameTextView = itemUserSlimView.findViewById(R.id.nicknameTextView);
        this.userSlimListener = userSlimListener;

        /* нажатие по карточке:  this == OnClickListener elem */
        itemUserSlimView.setOnClickListener(this);
    }

    public void setPosition(int position){
        this.position = position;
    }

    public ImageView getAvatarImageView() {
        return avatarImageView;
    }

    public TextView getNicknameTextView() {
        return nicknameTextView;
    }

    @Override
    public void onClick(View view) {
        userSlimListener.onClickItemUserSlimSelected(position);
    }
}
