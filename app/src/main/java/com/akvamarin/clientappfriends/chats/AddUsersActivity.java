package com.akvamarin.clientappfriends.chats;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.akvamarin.clientappfriends.adapters.UserAdapter;
import com.akvamarin.clientappfriends.databinding.ActivityAddUsersBinding;
import com.akvamarin.clientappfriends.dto.User;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class AddUsersActivity extends AppCompatActivity {
    private ActivityAddUsersBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddUsersBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        //setContentView(R.layout.activity_add_users);
        setListeners();
        uploadUsersFromDatabase();

    }

    private void setListeners(){
       binding.chatsImageBack.setOnClickListener(view -> {
           onBackPressed();
       });


    }

    private void uploadUsersFromDatabase(){
        binding.chatsProgressBar.setVisibility(View.INVISIBLE);
        //loading(true);
        List<User> userList = new ArrayList<>();

        User user1 = new User("Виктор", 28, "https://meragor.com/files/styles//ava_800_800_wm/sfztn_boy_avatar_1.jpg");
        user1.setId(1);
        User user2 = new User("Анна", 25, "https://meragor.com/files/styles//ava_800_800_wm/_5_5.jpg");
        User user3 = new User("Александр", 23, "https://demotivation.ru/wp-content/uploads/2020/11/905e85b5e1f2b5e1935c81b3c2478829.jpg");
        User user4 = new User("Виктория", 20, "https://meragor.com/files/styles//ava_800_800_wm/avatar-211226-001768.png");
        User user5 = new User("Инна", 31, "https://meragor.com/files/styles//ava_800_800_wm/avatar-210866-000320.png");
        User user6 = new User("Алекс", 35, "https://meragor.com/files/styles//ava_800_800_wm/sfztn_boy_avatar_18.jpg");
        User user7 = new User("Марина", 27, "https://meragor.com/files/styles//ava_800_800_wm/2_8.jpg");
        User user8 = new User("Пётр", 27, "https://it-doc.info/wp-content/uploads/2019/06/avatar-9.jpg");
        User user9 = new User("Никита", 25, "https://it-doc.info/wp-content/uploads/2019/06/avatar-guitar-man.jpg");
        User user10 = new User("Николай", 32, "https://meragor.com/files/styles//ava_800_800_wm/sfztn_boy_avatar_64.jpg");

        userList.add(user1);
        userList.add(user2);
        userList.add(user3);
        userList.add(user4);
        userList.add(user5);
        userList.add(user6);
        userList.add(user7);
        userList.add(user8);
        userList.add(user9);
        userList.add(user10);

        if (userList.size() > 0){
            UserAdapter userAdapter = new UserAdapter(userList);
            binding.chatsRecyclerViewUsers.setAdapter(userAdapter);
            binding.chatsRecyclerViewUsers.setVisibility(View.VISIBLE);

            binding.fabBtnSelectedUsers.setOnClickListener(new View.OnClickListener() { ///////////////////////////// перенести
                @Override
                public void onClick(View view) {
                    List<User> selectedUsers;
                    selectedUsers = userAdapter.getSelectedUsers();

                    Intent intent = new Intent(binding.getRoot().getContext(), GroupChatActivity.class);
                    intent.putExtra("selectedUsers", (Serializable) selectedUsers);
                    startActivity(intent);
                }
            });
        } else {
            showErrorMessage();
        }



    }

    private void loading(Boolean isLoading){
        if (isLoading){
            binding.chatsProgressBar.setVisibility(View.VISIBLE);
        } else {
            binding.chatsProgressBar.setVisibility(View.INVISIBLE);
        }

    }

    private void showErrorMessage(){
        binding.chatsTextErrorMessage.setText(String.format("%s", "Пользователи не найдены!"));
        binding.chatsTextErrorMessage.setVisibility(View.VISIBLE);
    }
}