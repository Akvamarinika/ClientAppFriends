package com.akvamarin.clientappfriends.API.connection;

import com.akvamarin.clientappfriends.domain.dto.AuthToken;
import com.akvamarin.clientappfriends.domain.dto.NotificationDTO;
import com.akvamarin.clientappfriends.domain.dto.NotificationFeedbackDTO;
import com.akvamarin.clientappfriends.domain.dto.ViewEventDTO;
import com.akvamarin.clientappfriends.domain.dto.ViewNotificationDTO;
import com.akvamarin.clientappfriends.domain.dto.ViewUserSlimDTO;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface NotificationParticipantApi {
    String PATH_PREFIX = "/api/v1/notifications";

    @POST(PATH_PREFIX + "/")
    Call<Void> createParticipantRequest(@Body NotificationDTO notificationDTO, @Header("Authorization") AuthToken authToken);

    @PUT(PATH_PREFIX)
    Call<ViewNotificationDTO> updateFeedbackStatus(@Body NotificationFeedbackDTO notificationFeedbackDTO, @Header("Authorization") AuthToken authToken);

    @DELETE(PATH_PREFIX + "/{requestId}")
    Call<Void> deleteParticipantRequest(@Path("requestId") Long requestId, @Header("Authorization") AuthToken authToken);

    @GET(PATH_PREFIX + "/user/{userId}")
    Call<List<ViewNotificationDTO>> findAllNotificationsByUserId(@Path("userId") Long userId, @Header("Authorization") AuthToken authToken);

    @GET(PATH_PREFIX + "/participant/{userId}")
    Call<List<ViewEventDTO>> findParticipantWithApproved(@Path("userId") Long userId, @Header("Authorization") AuthToken authToken);

    @GET(PATH_PREFIX + "/user/{userId}/waiting")
    Call<List<ViewEventDTO>> findUserEventsWithWaitingFeedback(@Path("userId") Long userId, @Header("Authorization") AuthToken authToken);

    @GET(PATH_PREFIX + "/event/{eventId}/participants")
    Call<List<ViewUserSlimDTO>> findEventParticipantsWithApprovedFeedback(@Path("eventId") Long eventId, @Header("Authorization") AuthToken authToken);

    @PUT(PATH_PREFIX + "/{requestId}/owner-viewed")
    Call<Void> updateOwnerViewed(@Path("requestId") Long requestId, @Query("ownerViewed") boolean ownerViewed, @Header("Authorization") AuthToken authToken); // -

    @PUT(PATH_PREFIX + "/{requestId}/participant-viewed")
    Call<Void> updateParticipantViewed(@Path("requestId") Long requestId, @Query("participantViewed") boolean participantViewed, @Header("Authorization") AuthToken authToken);

    @GET(PATH_PREFIX + "/check")
    Call<Boolean> isExistNotification(NotificationDTO notificationDTO, @Header("Authorization") AuthToken authToken); // -

    @GET(PATH_PREFIX + "/event/{eventId}/participant/{userId}")
    Call<ViewNotificationDTO> findNotification(@Path("eventId") Long eventId, @Path("userId") Long userId, @Header("Authorization") AuthToken authToken);

}
