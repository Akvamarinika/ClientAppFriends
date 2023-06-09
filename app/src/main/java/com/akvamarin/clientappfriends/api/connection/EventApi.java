package com.akvamarin.clientappfriends.api.connection;

import com.akvamarin.clientappfriends.domain.dto.AuthToken;
import com.akvamarin.clientappfriends.domain.dto.EventDTO;
import com.akvamarin.clientappfriends.domain.dto.EventFilter;
import com.akvamarin.clientappfriends.domain.dto.ViewEventDTO;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface EventApi {
    String PATH_PREFIX = "/api/v1/events";

    @GET(PATH_PREFIX + "/")
    Call<List<ViewEventDTO>> getAllEvents();

    @GET(PATH_PREFIX + "/{id}")
    Call<ViewEventDTO> getEventById(@Path("id") Long id, @Header("Authorization") AuthToken authToken);

    @POST(PATH_PREFIX + "/")
    Call<Void> createEvent (@Body EventDTO event, @Header("Authorization") AuthToken authToken);

    @PATCH(PATH_PREFIX + "/")
    Call<ViewEventDTO> updateEvent (@Body EventDTO event, @Header("Authorization") AuthToken authToken);

    @POST(PATH_PREFIX + "/filter")
    Call<List<ViewEventDTO>> filterEvents(@Body EventFilter eventFilter);

    @DELETE(PATH_PREFIX + "/{id}")
    Call<Void> deleteEvent(@Path("id") Long id, @Header("Authorization") AuthToken authToken);

}
