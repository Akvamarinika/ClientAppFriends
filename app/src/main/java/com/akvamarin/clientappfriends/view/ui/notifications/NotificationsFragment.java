package com.akvamarin.clientappfriends.view.ui.notifications;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.akvamarin.clientappfriends.api.ErrorResponse;
import com.akvamarin.clientappfriends.api.ErrorUtils;
import com.akvamarin.clientappfriends.api.RetrofitService;
import com.akvamarin.clientappfriends.api.connection.NotificationParticipantApi;
import com.akvamarin.clientappfriends.R;
import com.akvamarin.clientappfriends.domain.dto.AuthToken;
import com.akvamarin.clientappfriends.domain.dto.NotificationFeedbackDTO;
import com.akvamarin.clientappfriends.domain.dto.ViewNotificationDTO;
import com.akvamarin.clientappfriends.domain.enums.FeedbackType;
import com.akvamarin.clientappfriends.utils.Constants;
import com.akvamarin.clientappfriends.utils.PreferenceManager;
import com.akvamarin.clientappfriends.view.ui.profile.ViewProfileActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationsFragment extends Fragment implements INotificationOrganizerListener, INotificationParticipantListener {
    private static final String TAG = "NotificationsFragment";
    private TextView textViewBaseText;
    private Toolbar toolbar;
    private FloatingActionButton floatingActionButton;

    /* соединяются адаптером Recycler & List: */
    private RecyclerView recyclerViewNotification;
    private final List<ViewNotificationDTO> notificationList = new ArrayList<>();
    private NotificationAdapter notificationAdapter;
    private INotificationOrganizerListener notificationListener;
    private INotificationParticipantListener participantListener;

    private RetrofitService retrofitService;
    private NotificationParticipantApi notificationParticipantApi;
    private PreferenceManager preferenceManager;

    private View viewFragmentNotification;

    public View onCreateView(@NonNull LayoutInflater layoutInflater,
                             ViewGroup container, Bundle savedInstanceState) {

        viewFragmentNotification = layoutInflater.inflate(R.layout.fragment_notifications, container, false);
        updateToolbar();
        setHasOptionsMenu(true);
        updateMainFAB();
        init();
        initNotificationList();

        notificationListener = this;
        participantListener = this;
        notificationAdapter = new NotificationAdapter(notificationList, notificationListener, participantListener);
        recyclerViewNotification.setLayoutManager(new LinearLayoutManager(requireActivity()));
        recyclerViewNotification.setAdapter(notificationAdapter);

        return viewFragmentNotification;
    }

    private void updateToolbar(){
        toolbar = requireActivity().findViewById(R.id.top_toolbar);
        toolbar.setTitle(R.string.title_notifications);
        toolbar.setLogo(R.drawable.ic_notifications);
    }

    private void updateMainFAB(){
        floatingActionButton = requireActivity().findViewById(R.id.fab_btn);
        floatingActionButton.setImageResource(R.drawable.ic_chats_person_add);
        floatingActionButton.setVisibility(View.GONE);
    }

    private void init(){
        recyclerViewNotification = viewFragmentNotification.findViewById(R.id.recycler_view_notifications);
        textViewBaseText = viewFragmentNotification.findViewById(R.id.text_base_notifications);

        preferenceManager = new PreferenceManager(requireActivity());
        retrofitService = RetrofitService.getInstance(getContext());
        notificationParticipantApi = retrofitService.getRetrofit().create(NotificationParticipantApi.class);


    }

    private void initNotificationList() {
        Long userId = preferenceManager.getLong(Constants.KEY_USER_ID);
        String authToken = preferenceManager.getString(Constants.KEY_APP_TOKEN);
        Log.d(TAG, "ID user: " + userId);

        if (authToken != null && userId != 0L) {
            notificationParticipantApi.findAllNotificationsByUserId(userId, new AuthToken(authToken)).enqueue(new Callback<>() {
                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void onResponse(@NonNull Call<List<ViewNotificationDTO>> call, @NonNull Response<List<ViewNotificationDTO>> response) {

                    if (response.isSuccessful() && response.body() != null) {
                        notificationList.clear();
                        notificationList.addAll(response.body());
                        notificationAdapter.notifyDataSetChanged(); // Notify the adapter that the data has changed
                        Log.d(TAG, "initNotificationList() size: " + notificationList.size());

                        showBaseTextWhenListIsEmpty();
                    } else {
                        ErrorResponse error = ErrorUtils.parseError(response, retrofitService);
                        Log.d(TAG, error.getStatusCode() + " " + error.getMessage());
                    }
                }

                @Override
                public void onFailure(@NonNull Call<List<ViewNotificationDTO>> call, @NonNull Throwable t) {
                    Log.d(TAG, "Error initNotificationList(): " + t.fillInStackTrace());
                }
            });
        }
    }

    private void updateStatusNotificationFuncForOrganizer(NotificationFeedbackDTO notificationFeedbackDTO, int position) {
        Long userId = preferenceManager.getLong(Constants.KEY_USER_ID);
        String authToken = preferenceManager.getString(Constants.KEY_APP_TOKEN);
        Log.d(TAG, "ID user: " + userId);

        if (authToken != null && userId != 0L) {
            notificationParticipantApi.updateFeedbackStatus(notificationFeedbackDTO, new AuthToken(authToken)).enqueue(new Callback<>() {
                @Override
                public void onResponse(@NonNull Call<ViewNotificationDTO> call, @NonNull Response<ViewNotificationDTO> response) {

                    if (response.isSuccessful() && response.body() != null) {
                        ViewNotificationDTO viewNotificationDTO = response.body();
                        notificationList.get(position).setHiddenBtn(true);
                        notificationList.get(position).setFeedbackType(viewNotificationDTO.getFeedbackType());
                        notificationAdapter.notifyItemChanged(position); // Notify the adapter that the data has changed
                        Log.d(TAG, " updateStatusNotificationFuncForOrganizer size: " + notificationList.size());

                        showBaseTextWhenListIsEmpty();
                    } else {
                        ErrorResponse error = ErrorUtils.parseError(response, retrofitService);
                        Log.d(TAG, error.getStatusCode() + " " + error.getMessage());
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ViewNotificationDTO> call, @NonNull Throwable t) {
                    Log.d(TAG, "Error  updateStatusNotificationFuncForOrganizer(): " + t.fillInStackTrace());
                }
            });
        }
    }

    private void updateParticipantViewedOnServer(Long requestId, int position) {
        boolean isParticipantViewed = true;
        String authToken = preferenceManager.getString(Constants.KEY_APP_TOKEN);
        Log.d(TAG, "ID requestId: " + requestId);

        if (authToken != null && requestId != null) {
            notificationParticipantApi.updateParticipantViewed(requestId, isParticipantViewed, new AuthToken(authToken)).enqueue(new Callback<>() {
                @Override
                public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {

                    if (response.isSuccessful()) {
                        notificationList.get(position).setHiddenBtn(true);
                        notificationAdapter.notifyItemChanged(position); // Notify the adapter that the data has changed
                        Log.d(TAG, " updateParticipantViewedOnServer size: " + notificationList.size());

                        showBaseTextWhenListIsEmpty();
                    } else {
                        ErrorResponse error = ErrorUtils.parseError(response, retrofitService);
                        Log.d(TAG, error.getStatusCode() + " " + error.getMessage());
                    }
                }

                @Override
                public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                    Log.d(TAG, "Error updateParticipantViewedOnServer(): " + t.fillInStackTrace());
                }
            });
        }
    }

    public void showBaseTextWhenListIsEmpty(){
        textViewBaseText.setVisibility(notificationList.isEmpty() ? View.VISIBLE : View.GONE);
    }



    @Override
    public void onResume() {
        super.onResume();
        initNotificationList();
    }


    @Override
    public void onRejectButtonClick(int position) {
        ViewNotificationDTO notification = notificationList.get(position);
        NotificationFeedbackDTO notificationFeedbackDTO = new NotificationFeedbackDTO(notification.getId(), FeedbackType.REJECTED);
        updateStatusNotificationFuncForOrganizer(notificationFeedbackDTO, position);
    }

    @Override
    public void onApproveButtonClick(int position) {
        ViewNotificationDTO notification = notificationList.get(position);
        NotificationFeedbackDTO notificationFeedbackDTO = new NotificationFeedbackDTO(notification.getId(), FeedbackType.APPROVED);
        updateStatusNotificationFuncForOrganizer(notificationFeedbackDTO, position);
    }

    @Override
    public void onAvatarImageClick(int position) {
        startViewProfileActivity(position);
    }

    @Override
    public void onHiddenButtonClick(int position) {
        ViewNotificationDTO notification = notificationList.get(position);
        updateParticipantViewedOnServer(notification.getId(), position);
    }

    @Override
    public void onAvatarOrganizerImageClick(int position) {
        startViewProfileActivity(position);
    }

    public void startViewProfileActivity(int position){
        ViewNotificationDTO currentNotification = notificationList.get(position);
        Intent intent = new Intent(requireContext(), ViewProfileActivity.class);
        intent.putExtra("userId", currentNotification.getUserId()); // user id
        startActivity(intent);
    }

}
