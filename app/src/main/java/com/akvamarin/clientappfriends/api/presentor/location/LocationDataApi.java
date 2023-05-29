package com.akvamarin.clientappfriends.api.presentor.location;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.akvamarin.clientappfriends.api.RetrofitService;
import com.akvamarin.clientappfriends.api.connection.CityApi;
import com.akvamarin.clientappfriends.domain.dto.CityDTO;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LocationDataApi {
    private static final String TAG = "LocationDataApi";
    private static final int REQUEST_CODE_ERROR = -1;
    private final RetrofitService retrofitService;
    private final CityApi cityApi;
    private final Context context;

    public LocationDataApi(Context context) {
        this.context = context;
        retrofitService = RetrofitService.getInstance(context);
        cityApi = retrofitService.getRetrofit().create(CityApi.class);
    }

    public void loadCityFromServer(CityCallback callback) {
        cityApi.getAllCities().enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<List<CityDTO>> call, @NonNull Response<List<CityDTO>> response) {
                if (response.isSuccessful()) {
                    List<CityDTO> citiesList = response.body();
                    callback.onCityRetrieved(citiesList);
                } else {
                    callback.onCityRetrievalError(response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<CityDTO>> call, @NonNull Throwable t) {
                Log.d(TAG, "error: " + t.fillInStackTrace());
                callback.onCityRetrievalError(REQUEST_CODE_ERROR);
            }
        });
    }
}
