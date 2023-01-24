package com.akvamarin.clientappfriends.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.akvamarin.clientappfriends.R;
import com.akvamarin.clientappfriends.chats.GroupChatMessage;
import com.akvamarin.clientappfriends.databinding.ItemConteinerSendMessageBinding;
import com.akvamarin.clientappfriends.databinding.ItemContinerReceivedMessageBinding;
import com.squareup.picasso.Picasso;

import java.util.List;

public class GroupChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private final List<GroupChatMessage> groupChatMessagesList;
    private final String receiverProfileImage; /// Bitmap
    private final Long senderId;

    public static final int VIEW_TYPE_SENT = 1;
    public static final int VIEW_TYPE_RECEIVED = 2;

    public GroupChatAdapter(List<GroupChatMessage> groupChatMessagesList, String receiverProfileImage, Long senderId) {
        this.groupChatMessagesList = groupChatMessagesList;
        this.receiverProfileImage = receiverProfileImage;
        this.senderId = senderId;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_SENT){
            return new SentMessageViewHolder(ItemConteinerSendMessageBinding.inflate(
                    LayoutInflater.from(parent.getContext()),
                    parent,
                    false
            ));
        } else {
            return new ReceivedMessageViewHolder(ItemContinerReceivedMessageBinding.inflate(
                    LayoutInflater.from(parent.getContext()),
                    parent,
                    false
            ));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == VIEW_TYPE_SENT) {
            ((SentMessageViewHolder) holder).setData(groupChatMessagesList.get(position));
        } else {
            ((ReceivedMessageViewHolder) holder).setData(groupChatMessagesList.get(position), receiverProfileImage);
        }

    }

    @Override
    public int getItemCount() {
        return groupChatMessagesList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return VIEW_TYPE_SENT;
//        if (groupChatMessagesList.get(position).getSenderId().equals(senderId)){
//            return VIEW_TYPE_SENT;
//        } else {
//            return VIEW_TYPE_RECEIVED;
//        }

    }

    class SentMessageViewHolder extends RecyclerView.ViewHolder {
        private final ItemConteinerSendMessageBinding binding;

        public SentMessageViewHolder(ItemConteinerSendMessageBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void setData(GroupChatMessage groupChatMessage){
            binding.senderTextMessage.setText(groupChatMessage.getMessage());
            binding.senderTextDateTime.setText(groupChatMessage.getDateTime());

        }
    }


    class ReceivedMessageViewHolder extends RecyclerView.ViewHolder {
        private final ItemContinerReceivedMessageBinding binding;

        public ReceivedMessageViewHolder(ItemContinerReceivedMessageBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void setData(GroupChatMessage groupChatMessage, String receiverProfileImage){
            binding.receiverTextMessage.setText(groupChatMessage.getMessage());
            binding.receiverTextDateTime.setText(groupChatMessage.getDateTime());
            //binding.receiverImageProfile.setImageBitmap(receiverProfileImage);

            if (receiverProfileImage.isEmpty()){                                  //TODO вынести блок
                binding.receiverImageProfile.setImageResource(R.drawable.no_avatar);
            } else {
                Picasso.get()
                        .load(receiverProfileImage)
                        .fit()
                        .error(R.drawable.error_loading_image)
                        .into(binding.receiverImageProfile);   //.setLoggingEnabled(true)
            }
        }
    }
}
