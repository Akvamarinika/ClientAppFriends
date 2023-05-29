package com.akvamarin.clientappfriends.api.connection;

import com.akvamarin.clientappfriends.domain.dto.CityDTO;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

/** API-интерфейс пользователя **/
public interface CityApi {
    String PATH_PREFIX = "/api/v1/cities";

    @GET(PATH_PREFIX + "/")
    Call<List<CityDTO>> getAllCities();
}
