package com.akvamarin.clientappfriends.view.chats;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.akvamarin.clientappfriends.adapters.GroupChatAdapter;
import com.akvamarin.clientappfriends.databinding.ActivityGroupChatBinding;
import com.akvamarin.clientappfriends.domain.dto.ViewUserSlimDTO;
import com.akvamarin.clientappfriends.utils.Constants;
import com.akvamarin.clientappfriends.utils.PreferenceManager;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class GroupChatActivity extends AppCompatActivity {
    private static final String TAG = "GroupChat";
    private ActivityGroupChatBinding binding;
    private List<ViewUserSlimDTO> selectedUsers;
    private List<GroupChatMessage> chatMessageList;
    private GroupChatAdapter groupChatAdapter;
    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGroupChatBinding.inflate(getLayoutInflater());
        //setContentView(R.layout.activity_group_chat);
        setContentView(binding.getRoot());

        loadReceiversDetails();
        setListeners();
        init();


    }

    private void init(){
        preferenceManager = new PreferenceManager(getApplicationContext());
        chatMessageList = new ArrayList<>();
        groupChatAdapter = new GroupChatAdapter(
                chatMessageList,
                "https://meragor.com/files/styles//ava_800_800_wm/avatar-210866-000320.png",               /////////////////////////////bitmap
                preferenceManager.getLong(Constants.KEY_USER_ID) /// id receiver
        );

        binding.groupChatRecyclerView.setAdapter(groupChatAdapter);
    }

    private void sendMessage(){
        HashMap<String, Object> message = new HashMap<>();
        String msg = binding.inputMessageEditText.getText().toString();
        message.put(Constants.KEY_SENDER_ID, preferenceManager.getLong(Constants.KEY_SENDER_ID)); // sender
        message.put(Constants.KEY_MESSAGE, msg);
        message.put(Constants.KEY_TIMESTAMP, new Date());
        //save msg to DB
        GroupChatMessage messageChat = new GroupChatMessage();
        messageChat.setMessage(msg);
        messageChat.setDateTime(getReadableDateTime(new Date()));
        chatMessageList.add(messageChat);
        binding.inputMessageEditText.setText(null);


        for (ViewUserSlimDTO receiver : selectedUsers){
            message.put(Constants.KEY_RECEIVER_ID, receiver.getId());
        }
    }

    private void loadReceiversDetails(){
        Intent intent = getIntent();
        selectedUsers = (List<ViewUserSlimDTO>) intent.getSerializableExtra("selectedUsers");
        binding.groupChatGroupName.setText("EventName"); //// название меропр-я
    }

    private void setListeners(){
        binding.iconImageBack.setOnClickListener(view -> {
            onBackPressed();
        });
        binding.layoutSend.setOnClickListener(view -> {
            sendMessage();
            uploadMessageFromDB();
        });
    }

    private String getReadableDateTime(Date date){
        return new SimpleDateFormat("MMMM dd, yyyy - hh:mm a", Locale.getDefault()).format(date);
    }

    @SuppressLint("NotifyDataSetChanged")
    private void uploadMessageFromDB(){
        int countMsg = chatMessageList.size();
        //chatMessageList.add();
        chatMessageList.sort(Comparator.comparing(GroupChatMessage::getDateObject));

        if(countMsg == 0){
            groupChatAdapter.notifyDataSetChanged();
        } else {
            groupChatAdapter.notifyItemRangeInserted(countMsg, countMsg);
            binding.groupChatRecyclerView.smoothScrollToPosition(countMsg - 1);
        }

        binding.groupChatRecyclerView.setVisibility(View.VISIBLE);
        binding.chatsProgressBar.setVisibility(View.GONE);

        Log.d(TAG, "uploadMessageFromDB: " + countMsg);
    }
}