package com.akvamarin.clientappfriends.view.ui.myevents.tabs;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.akvamarin.clientappfriends.API.ErrorResponse;
import com.akvamarin.clientappfriends.API.ErrorUtils;
import com.akvamarin.clientappfriends.API.RetrofitService;
import com.akvamarin.clientappfriends.API.connection.NotificationParticipantApi;
import com.akvamarin.clientappfriends.R;
import com.akvamarin.clientappfriends.domain.dto.AuthToken;
import com.akvamarin.clientappfriends.domain.dto.ViewEventDTO;
import com.akvamarin.clientappfriends.utils.Constants;
import com.akvamarin.clientappfriends.utils.PreferenceManager;
import com.akvamarin.clientappfriends.view.InfoEventActivity;
import com.akvamarin.clientappfriends.view.ui.home.EventAdapter;
import com.akvamarin.clientappfriends.view.ui.home.IEventRecyclerListener;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ParticipantEventFragment extends Fragment implements IEventRecyclerListener {
    private static final String TAG = "ParticipantFragment";
    private TextView textViewBaseText;

    /* соединяются адаптером Recycler & List: */
    private RecyclerView recyclerViewMyEventsParticipant;
    private final List<ViewEventDTO> myEventsParticipantList = new ArrayList<>();
    private EventAdapter eventAdapter;
    private IEventRecyclerListener eventListener;

    private RetrofitService retrofitService;
    private NotificationParticipantApi notificationParticipantApi;
    private PreferenceManager preferenceManager;

    private View view;

    public static ParticipantEventFragment newInstance() {
        return new ParticipantEventFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.participant_event_fragment, container, false);
        setHasOptionsMenu(true);
        init();
        initParticipantEventList();

        eventListener = this;
        eventAdapter = new EventAdapter(myEventsParticipantList, eventListener);
        recyclerViewMyEventsParticipant.setLayoutManager(new LinearLayoutManager(requireActivity()));
        recyclerViewMyEventsParticipant.setAdapter(eventAdapter);
        return view;
    }

    private void init(){
        recyclerViewMyEventsParticipant = view.findViewById(R.id.recycler_view_my_events_participant);
        textViewBaseText = view.findViewById(R.id.text_tab_participant_event);

        preferenceManager = new PreferenceManager(requireActivity());
        retrofitService = RetrofitService.getInstance(getContext());
        notificationParticipantApi = retrofitService.getRetrofit().create(NotificationParticipantApi.class);


    }

    private void initParticipantEventList() {
        Long userId = preferenceManager.getLong(Constants.KEY_USER_ID);
        String authToken = preferenceManager.getString(Constants.KEY_APP_TOKEN);
        Log.d(TAG, "ID user: " + userId);

        if (authToken != null && userId != 0L) {
            notificationParticipantApi.findParticipantWithApproved(userId, new AuthToken(authToken)).enqueue(new Callback<>() {
                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void onResponse(@NonNull Call<List<ViewEventDTO>> call, @NonNull Response<List<ViewEventDTO>> response) {

                    if (response.isSuccessful() && response.body() != null) {
                        myEventsParticipantList.clear();
                        myEventsParticipantList.addAll(response.body());
                        eventAdapter.notifyDataSetChanged(); // Notify the adapter that the data has changed
                        showBaseTextWhenListIsEmpty();
                        Log.d(TAG, "myEventsParticipantList size: " + myEventsParticipantList.size());
                    } else {
                        ErrorResponse error = ErrorUtils.parseError(response, retrofitService);
                        Log.d(TAG, error.getStatusCode() + " " + error.getMessage());
                    }
                }

                @Override
                public void onFailure(@NonNull Call<List<ViewEventDTO>> call, @NonNull Throwable t) {
                    Log.d(TAG, "Error initParticipantEventList(): " + t.fillInStackTrace());
                }
            });
        }
    }

    public void showBaseTextWhenListIsEmpty() {
        textViewBaseText.setVisibility(myEventsParticipantList.isEmpty() ? View.VISIBLE : View.GONE);
    }


    /*** Click card Event: ***/
    @Override
    public void onClickRecyclerEventSelected(int index){
        ViewEventDTO currentEvent = myEventsParticipantList.get(index);
        Intent intent = new Intent(getActivity(), InfoEventActivity.class);
        intent.putExtra("current_event_id", currentEvent.getId());
        startActivity(intent);
    }

    @Override
    public void onResume() {
        super.onResume();
        initParticipantEventList();
    }
}

