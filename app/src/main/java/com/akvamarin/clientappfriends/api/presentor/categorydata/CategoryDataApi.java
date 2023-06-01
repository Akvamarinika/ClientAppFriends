package com.akvamarin.clientappfriends.api.presentor.categorydata;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.akvamarin.clientappfriends.api.RetrofitService;
import com.akvamarin.clientappfriends.api.connection.EventCategoryApi;
import com.akvamarin.clientappfriends.domain.dto.EventCategoryDTO;
import com.akvamarin.clientappfriends.utils.PreferenceManager;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CategoryDataApi {
    private static final String TAG = "CategoryDataApi";
    private static final int REQUEST_CODE_ERROR = -1;
    private final RetrofitService retrofitService;
    private final EventCategoryApi eventCategoryApi;
    private final PreferenceManager preferenceManager;
    private final Context context;

    public CategoryDataApi(Context context) {
        this.context = context;
        retrofitService = RetrofitService.getInstance(context);
        eventCategoryApi = retrofitService.getRetrofit().create(EventCategoryApi.class);
        preferenceManager = new PreferenceManager(context);
    }

    public void requestAllEventCategories(CategoryCallback callback){
        eventCategoryApi.getAllCategories().enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<List<EventCategoryDTO>> call, @NonNull Response<List<EventCategoryDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<EventCategoryDTO> categoryDTOList = response.body();
                    callback.onCategoryListRetrieved(categoryDTOList);
                } else {
                    callback.onCategoryListRetrievalError(response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<EventCategoryDTO>> call, @NonNull Throwable t) {
                Log.d(TAG, "Error fetching categories: " + t.fillInStackTrace());
                callback.onCategoryListRetrievalError(REQUEST_CODE_ERROR);
            }
        });
    }
}
