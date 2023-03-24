package com.akvamarin.clientappfriends.adapters;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.akvamarin.clientappfriends.R;

import com.akvamarin.clientappfriends.databinding.ItemContainerUserBinding;
import com.akvamarin.clientappfriends.domain.dto.User;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder>{
    private final List<User> userListFromCityX;

    /*selected item*/
    private boolean isSelectMode = false;
    private final List<User> selectedUsers = new ArrayList<>();


    public UserAdapter(List<User> userListFromCityX) {
        this.userListFromCityX = userListFromCityX;
    }

    public List<User> getSelectedUsers() {
        return selectedUsers;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        ItemContainerUserBinding itemContainerUser = ItemContainerUserBinding.inflate(
                LayoutInflater.from(viewGroup.getContext()),
                viewGroup,
                false
        );
        return new UserViewHolder(itemContainerUser);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        holder.setUserData(userListFromCityX.get(position));

    }

    @Override
    public int getItemCount() {
        return userListFromCityX.size();
    }


    class UserViewHolder extends RecyclerView.ViewHolder{
        ItemContainerUserBinding itemContainer;

        public UserViewHolder(@NonNull ItemContainerUserBinding itemContainerUserBinding) {
            super(itemContainerUserBinding.getRoot());
            itemContainer = itemContainerUserBinding;

            /*selected item*/
            itemContainer.getRoot().setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    isSelectMode = true;
                    User selectedUser = userListFromCityX.get(getAdapterPosition());

                    if (selectedUsers.contains(selectedUser)){
                        //View itemView
                        itemContainer.getRoot().setBackgroundColor(Color.TRANSPARENT);
                        selectedUsers.remove(selectedUser);
                    } else {
                        itemContainer.getRoot().setBackgroundResource(R.color.color_blue_light); // SELECTED COLOR
                        selectedUsers.add(selectedUser);
                    }

                    if (selectedUsers.size() == 0){
                        isSelectMode = false;
                    }

                    return true;
                }
            });

            /*selected item*/
            itemContainer.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (isSelectMode){

                        User selectedUser = userListFromCityX.get(getAdapterPosition());

                        if (selectedUsers.contains(selectedUser)){
                            //View itemView
                            itemContainer.getRoot().setBackgroundColor(Color.TRANSPARENT);
                            selectedUsers.remove(selectedUser);
                        } else {
                            itemContainer.getRoot().setBackgroundResource(R.color.color_blue_light); // SELECTED COLOR
                            selectedUsers.add(selectedUser);
                        }

                        if (selectedUsers.size() == 0){
                            isSelectMode = false;
                        }

                    }
                }
            });
        }

        @SuppressLint("SetTextI18n")
        void setUserData(User user){
            itemContainer.chatsUserName.setText(user.getName() + ", " + user.getAge());
            itemContainer.chatsUserInterest.setText(user.getCity());
           // itemContainer.chatsImageProfile.setImageBitmap(BitmapConvertor.convertFromBase64ToBitmap(user.getUrlAvatar())); ///////////////////////////////////

            if (user.getUrlAvatar().isEmpty()){                                  //TODO вынести блок
                itemContainer.chatsImageProfile.setImageResource(R.drawable.no_avatar);
            } else {
                Picasso.get()
                        .load(user.getUrlAvatar())
                        .fit()
                        .error(R.drawable.error_loading_image)
                        .into(itemContainer.chatsImageProfile);   //.setLoggingEnabled(true)
            }

        }
    }

}
