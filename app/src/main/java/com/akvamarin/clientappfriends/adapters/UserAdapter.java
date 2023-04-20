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
import com.akvamarin.clientappfriends.domain.dto.ViewUserSlimDTO;
import com.akvamarin.clientappfriends.utils.Utils;
import com.squareup.picasso.Picasso;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder>{
    private final List<ViewUserSlimDTO> userListFromCityX;

    /*selected item*/
    private boolean isSelectMode = false;
    private final List<ViewUserSlimDTO> selectedUsers = new ArrayList<>();


    public UserAdapter(List<ViewUserSlimDTO> userListFromCityX) {
        this.userListFromCityX = userListFromCityX;
    }

    public List<ViewUserSlimDTO> getSelectedUsers() {
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
                    ViewUserSlimDTO selectedUser = userListFromCityX.get(getAdapterPosition());

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

                        ViewUserSlimDTO selectedUser = userListFromCityX.get(getAdapterPosition());

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
        void setUserData(ViewUserSlimDTO slimUser){
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                LocalDate birthday = LocalDate.parse(slimUser.getDateOfBirthday());
                int age = Utils.getAgeWithCalendar(birthday.getYear(), birthday.getMonthValue(), birthday.getDayOfMonth());
                itemContainer.chatsUserName.setText(slimUser.getNickname() + ", " + age);
            }

            itemContainer.chatsUserInterest.setText(slimUser.getCityDTO().getName());
            //itemContainer.chatsImageProfile.setImageBitmap(BitmapConvertor.convertFromBase64ToBitmap(user.getUrlAvatar())); ///////////////////////////////////

            if (slimUser.getUrlAvatar().isEmpty()){                                  //TODO вынести блок
                itemContainer.chatsImageProfile.setImageResource(R.drawable.no_avatar);
            } else {
                Picasso.get()
                        .load(slimUser.getUrlAvatar())
                        .fit()
                        .error(R.drawable.error_loading_image)
                        .into(itemContainer.chatsImageProfile);   //.setLoggingEnabled(true)
            }

        }
    }

}
