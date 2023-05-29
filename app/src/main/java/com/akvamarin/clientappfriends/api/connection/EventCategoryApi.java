package com.akvamarin.clientappfriends.api.connection;

import com.akvamarin.clientappfriends.domain.dto.EventCategoryDTO;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface EventCategoryApi {
    String PATH_PREFIX = "/api/v1/categories";

    @GET(PATH_PREFIX + "/")
    Call<List<EventCategoryDTO>> getAllCategories();
}
