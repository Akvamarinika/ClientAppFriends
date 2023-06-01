package com.akvamarin.clientappfriends.api.presentor.eventdata;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.akvamarin.clientappfriends.api.ErrorResponse;
import com.akvamarin.clientappfriends.api.ErrorUtils;
import com.akvamarin.clientappfriends.api.RetrofitService;
import com.akvamarin.clientappfriends.api.connection.EventApi;
import com.akvamarin.clientappfriends.api.presentor.BaseCallback;
import com.akvamarin.clientappfriends.domain.dto.AuthToken;
import com.akvamarin.clientappfriends.domain.dto.EventFilter;
import com.akvamarin.clientappfriends.domain.dto.ViewEventDTO;
import com.akvamarin.clientappfriends.utils.Constants;
import com.akvamarin.clientappfriends.utils.PreferenceManager;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EventDataApi {
    private static final String TAG = "EventDataApi";
    private static final int REQUEST_CODE_ERROR = -1;
    private final RetrofitService retrofitService;
    private final EventApi eventApi;
    private final PreferenceManager preferenceManager;
    private final Context context;

    public EventDataApi(Context context) {
        this.context = context;
        retrofitService = RetrofitService.getInstance(context);
        eventApi = retrofitService.getRetrofit().create(EventApi.class);
        preferenceManager = new PreferenceManager(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void requestAllEvents(EventListCallback callback){
        eventApi.getAllEvents().enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<List<ViewEventDTO>> call, @NonNull Response<List<ViewEventDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<ViewEventDTO> viewEventDTOList = response.body();
                    callback.onEventListRetrieved(viewEventDTOList);
                } else {
                    callback.onEventListRetrievalError(response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<ViewEventDTO>> call, @NonNull Throwable t) {
                Log.d(TAG, "Error fetching events: " + t.fillInStackTrace());
                callback.onEventListRetrievalError(REQUEST_CODE_ERROR);
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void requestFilterEvents(EventFilter eventFilter, EventListCallback callback){
        eventApi.filterEvents(eventFilter).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<List<ViewEventDTO>> call, @NonNull Response<List<ViewEventDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<ViewEventDTO> viewEventDTOList = response.body();
                    callback.onEventListRetrieved(viewEventDTOList);
                } else {
                    callback.onEventListRetrievalError(response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<ViewEventDTO>> call, @NonNull Throwable t) {
                Log.d(TAG, "Error fetching events: " + t.fillInStackTrace());
                callback.onEventListRetrievalError(REQUEST_CODE_ERROR);
            }
        });
    }

    public void requestDeleteEvent(Long id, BaseCallback callback) {
        String authToken = preferenceManager.getString(Constants.KEY_APP_TOKEN);
        Log.d(TAG, "Event delete: " + id);

        if (authToken != null && id != null) {
            eventApi.deleteEvent(id, new AuthToken(authToken)).enqueue(new Callback<>() {
                @Override
                public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                    Log.d(TAG, "requestDeleteEvent() " + response.headers());

                    if (response.isSuccessful()) {
                        callback.onRetrieved();
                    } else {
                        callback.onRetrievalError(response.code());
                    }
                }

                @Override
                public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                    Log.d(TAG, "Error deleteNotificationOnServer(): " + t.fillInStackTrace());
                    callback.onRetrievalError(REQUEST_CODE_ERROR);
                }
            });
        }
    }

}
