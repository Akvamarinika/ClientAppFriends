package com.akvamarin.clientappfriends.view.infoevent;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.akvamarin.clientappfriends.R;
import com.akvamarin.clientappfriends.domain.dto.ViewUserSlimDTO;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Locale;

public class OnlyAvatarAdapter extends RecyclerView.Adapter<OnlyAvatarViewHolder> {
    private final List<ViewUserSlimDTO> userList;
    private Long eventId;

    public OnlyAvatarAdapter(List<ViewUserSlimDTO> userList) {
        this.userList = userList;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    @NonNull
    @Override
    public OnlyAvatarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user_slim_only_avatar, parent, false);
        return new OnlyAvatarViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OnlyAvatarViewHolder holder, int position) {
        ViewUserSlimDTO user = userList.get(position);

        if (user.getUrlAvatar().isEmpty()) {
            holder.getImageViewAvatar().setImageResource(R.drawable.no_avatar);
        } else {
            Picasso.get()
                    .load(user.getUrlAvatar())
                    .fit()
                    .error(R.drawable.error_loading_image)
                    .into(holder.getImageViewAvatar());
        }

        int remainingCount = userList.size() - 3;
        if (position == 2 && remainingCount > 0) {
            String moreStr = String.format(Locale.getDefault(), "+ ещё %d %s", remainingCount, getParticipantEnding(remainingCount));
            holder.getTextViewMore().setVisibility(View.VISIBLE);
            holder.getTextViewMore().setText(moreStr);
        } else {
            holder.getTextViewMore().setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(view -> {
            if (eventId != null) {
                Intent intent = new Intent(view.getContext(), ParticipantActivity.class);
                intent.putExtra("current_event_id", eventId); // event id
                view.getContext().startActivity(intent);
            }
        });
    }

    public static String getParticipantEnding(int count) {
        if (count % 10 == 1 && count % 100 != 11) {
            return "участник";
        } else if ((count % 10 >= 2 && count % 10 <= 4) && !(count % 100 >= 12 && count % 100 <= 14)) {
            return "участника";
        } else {
            return "участников";
        }
    }


    @Override
    public int getItemCount() {
        return Math.min(userList.size(), 3);
    }
}