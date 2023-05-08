package com.akvamarin.clientappfriends.API.connection;

import com.akvamarin.clientappfriends.domain.dto.AuthToken;
import com.akvamarin.clientappfriends.domain.dto.CommentDTO;
import com.akvamarin.clientappfriends.domain.dto.ViewCommentDTO;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface CommentApi {
    String PATH_PREFIX = "/api/v1/comments";

    @GET(PATH_PREFIX + "/event/{eventId}")
    Call<List<ViewCommentDTO>> getEventComments(@Path("eventId") Long eventId);

    @POST(PATH_PREFIX + "/create/{eventId}")
    Call<Void> createComment(@Path("eventId") Long eventId, @Body CommentDTO comment,
                             @Header("Authorization") AuthToken authToken);

    @GET(PATH_PREFIX + "/{commentId}")
    Call<ViewCommentDTO> findCommentById(@Path("commentId") Long commentId);

    @PATCH(PATH_PREFIX + "/{commentId}")
    Call<ViewCommentDTO> updateComment(@Path("commentId") Long commentId, @Body CommentDTO commentDTO,
                                   @Header("Authorization") AuthToken authToken);

    @DELETE(PATH_PREFIX + "/{commentId}")
    Call<Void> deleteComment(@Path("commentId") Long commentId, @Header("Authorization") AuthToken authToken);
}
