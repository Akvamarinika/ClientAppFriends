package com.akvamarin.clientappfriends.view.chats;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.akvamarin.clientappfriends.api.RetrofitService;
import com.akvamarin.clientappfriends.api.connection.UserApi;
import com.akvamarin.clientappfriends.adapters.UserAdapter;
import com.akvamarin.clientappfriends.databinding.ActivityAddUsersBinding;
import com.akvamarin.clientappfriends.domain.dto.ViewUserSlimDTO;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddUsersActivity extends AppCompatActivity {
    private static final String TAG = "recyclerSlimUsers";
    private ActivityAddUsersBinding binding;
    private RetrofitService retrofitService;
    private UserApi userApi;
    private static List<ViewUserSlimDTO> userList = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddUsersBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        //setContentView(R.layout.activity_add_users);
        setListeners();
        init();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            uploadUsersFromServerDatabase();
        }

    }

    private void setListeners(){
       binding.chatsImageBack.setOnClickListener(view -> {
           onBackPressed();
       });
    }

    private void init(){
        retrofitService = RetrofitService.getInstance(getApplicationContext());
        userApi = retrofitService.getRetrofit().create(UserApi.class);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void uploadUsersFromServerDatabase(){
        binding.chatsProgressBar.setVisibility(View.INVISIBLE);
        //loading(true);


        Log.d(TAG, "init slimUserDTO list: preparing ...");

        if (userList == null) {
            userList = new ArrayList<>();

            userApi.getAllSlimUsers().enqueue(new Callback<>() {
                @Override
                public void onResponse(@NonNull Call<List<ViewUserSlimDTO>> call, @NonNull Response<List<ViewUserSlimDTO>> response) {

                    if (response.isSuccessful()) {
                        userList = response.body();
                    } else {
                        Toast.makeText(getApplicationContext(), "getSlimUserDTOs() code:" + response.code(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<List<ViewUserSlimDTO>> call, @NonNull Throwable t) {
                    Toast.makeText(getApplicationContext(), "getSlimUserDTOs() --- Fail", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "error: " + t.fillInStackTrace());
                }

            });
        }

        if (userList.size() > 0){
            UserAdapter userAdapter = new UserAdapter(userList);
            binding.chatsRecyclerViewUsers.setAdapter(userAdapter);
            binding.chatsRecyclerViewUsers.setVisibility(View.VISIBLE);

            binding.fabBtnSelectedUsers.setOnClickListener(new View.OnClickListener() { ///////////////////////////// перенести
                @Override
                public void onClick(View view) {
                    List<ViewUserSlimDTO> selectedUsers;
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