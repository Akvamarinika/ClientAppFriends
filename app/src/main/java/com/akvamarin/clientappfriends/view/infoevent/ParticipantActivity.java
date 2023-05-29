package com.akvamarin.clientappfriends.view.infoevent;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.akvamarin.clientappfriends.api.ErrorResponse;
import com.akvamarin.clientappfriends.api.ErrorUtils;
import com.akvamarin.clientappfriends.api.RetrofitService;
import com.akvamarin.clientappfriends.api.connection.NotificationParticipantApi;
import com.akvamarin.clientappfriends.BaseActivity;
import com.akvamarin.clientappfriends.R;
import com.akvamarin.clientappfriends.domain.dto.AuthToken;
import com.akvamarin.clientappfriends.domain.dto.ViewUserSlimDTO;
import com.akvamarin.clientappfriends.utils.Constants;
import com.akvamarin.clientappfriends.utils.PreferenceManager;
import com.akvamarin.clientappfriends.view.dialog.AuthDialog;
import com.akvamarin.clientappfriends.view.ui.profile.ViewProfileActivity;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ParticipantActivity extends BaseActivity implements IUserSlimListener{
    private static final String TAG = "ParticipantActivity";
    private TextView titleParticipant;

    //соединяются адаптером Recycler & List:
    private RecyclerView recyclerViewUserSlim;
    private UserSlimAdapter userSlimAdapter;
    private final List<ViewUserSlimDTO> userSlimList = new ArrayList<>();
    private IUserSlimListener userSlimListener;

    private PreferenceManager preferenceManager;
    private RetrofitService retrofitService;
    private NotificationParticipantApi notificationParticipantApi;
    private String loading;
    private Long eventId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_participant);
        eventId = (Long) getIntent().getSerializableExtra("current_event_id");
        Log.d(TAG, "current_event_id eventId = " + eventId);

        initWidgets();
        initParticipantListFromServer();
    }

    private void initWidgets(){
        loading = getApplicationContext().getString(R.string.loading);
        titleParticipant = findViewById(R.id.titleParticipant);

        recyclerViewUserSlim = findViewById(R.id.recycler_view_participant);
        userSlimListener = this;
        recyclerViewUserSlim.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        userSlimAdapter = new UserSlimAdapter(userSlimList, userSlimListener);
        recyclerViewUserSlim.setAdapter(userSlimAdapter);

        preferenceManager = new PreferenceManager(getApplicationContext());
        retrofitService = RetrofitService.getInstance(getApplicationContext());
        notificationParticipantApi = retrofitService.getRetrofit().create(NotificationParticipantApi.class);
    }

    private void initParticipantListFromServer() {
        String authToken = preferenceManager.getString(Constants.KEY_APP_TOKEN);

        if (authToken != null && eventId != null) {
            showProgressDialog(loading);

            notificationParticipantApi.findEventParticipantsWithApprovedFeedback(eventId, new AuthToken(authToken)).enqueue(new Callback<>() {
                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void onResponse(@NonNull Call<List<ViewUserSlimDTO>> call, @NonNull Response<List<ViewUserSlimDTO>> response) {
                    dismissProgressDialog();

                    if (response.isSuccessful() && response.body() != null) {
                        userSlimList.clear();
                        userSlimList.addAll(response.body());
                        userSlimAdapter.notifyDataSetChanged(); // Notify the adapter that the data has changed
                        updateTitle();
                        Log.d(TAG, "initParticipantListFromServer() size: " + userSlimList.size());
                    } else {
                        ErrorResponse error = ErrorUtils.parseError(response, retrofitService);
                        Log.d(TAG, error.getStatusCode() + " " + error.getMessage());
                    }
                }

                @Override
                public void onFailure(@NonNull Call<List<ViewUserSlimDTO>> call, @NonNull Throwable t) {
                    dismissProgressDialog();
                    Log.d(TAG, "Error initParticipantListFromServer(): " + t.fillInStackTrace());
                }
            });
        }
    }

    private void updateTitle(){
        String title = getString(R.string.title_participant) + " " + userSlimList.size();
        titleParticipant.setText(title);
    }

    @Override
    public void onClickItemUserSlimSelected(int position) {
        if (isAuthenticated()) {
            ViewUserSlimDTO currentUserSlim = userSlimList.get(position);
            Long currentUserSlimId = currentUserSlim.getId();
            Intent intent = new Intent(getApplicationContext(), ViewProfileActivity.class);
            intent.putExtra("userId", currentUserSlimId);
            startActivity(intent);
        } else {
            showAuthDialog();
        }
    }

    private boolean isAuthenticated() {
        return preferenceManager != null && preferenceManager.getString(Constants.KEY_APP_TOKEN) != null;
    }

    private void showAuthDialog() {
        AuthDialog dialog = new AuthDialog(this);
        dialog.show();
    }

    @Override
    public void onResume() {
        super.onResume();
        initParticipantListFromServer();
    }

}