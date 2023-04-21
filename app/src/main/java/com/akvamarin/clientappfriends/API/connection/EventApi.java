package com.akvamarin.clientappfriends.API.connection;

import com.akvamarin.clientappfriends.domain.dto.AuthToken;
import com.akvamarin.clientappfriends.domain.dto.EventDTO;
import com.akvamarin.clientappfriends.domain.dto.ViewEventDTO;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface EventApi {
    String PATH_PREFIX = "/api/v1/events";

    @GET(PATH_PREFIX + "/")
    Call<List<ViewEventDTO>> getAllEvents();

    @GET(PATH_PREFIX + "/{id}")
    Call<ViewEventDTO> getEvent(@Path("id") Long id);

    @POST(PATH_PREFIX + "/")
    Call<Void> createEvent (@Body EventDTO event, @Header("Authorization") AuthToken authToken);

}
