package com.akvamarin.clientappfriends.api.connection;

import com.akvamarin.clientappfriends.domain.dto.AuthToken;
import com.akvamarin.clientappfriends.domain.dto.UserDTO;
import com.akvamarin.clientappfriends.domain.dto.ViewUserDTO;
import com.akvamarin.clientappfriends.domain.dto.ViewUserSlimDTO;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.PATCH;
import retrofit2.http.Path;

/** API-интерфейс пользователя **/
public interface UserApi {
    String PATH_PREFIX = "/api/v1/users";

    @GET(PATH_PREFIX + "/")
    Call<List<ViewUserSlimDTO>> getAllSlimUsers();

    @GET(PATH_PREFIX + "/login/{login}")
    Call<ViewUserDTO> getUserByLogin(@Path("login") String login, @Header("Authorization") AuthToken authToken);

    @GET(PATH_PREFIX + "/{id}")
    Call<ViewUserDTO> getUserById(@Path("id") Long id, @Header("Authorization") AuthToken authToken);

    @GET(PATH_PREFIX + "/slim/login/{login}")
    Call<ViewUserSlimDTO> getSlimUserByLogin(@Path("login") String login, @Header("Authorization") AuthToken authToken);

    @DELETE(PATH_PREFIX + "/{id}")
    Call<Void> deleteUser(@Path("id") Long id, @Header("Authorization") AuthToken authToken);

    @GET(PATH_PREFIX + "/check/{username}")
    Call<Boolean> checkUsername(@Path("username") String username);

    @PATCH(PATH_PREFIX + "/")
    Call<Void> updateUser (@Body UserDTO user, @Header("Authorization") AuthToken authToken);

}
