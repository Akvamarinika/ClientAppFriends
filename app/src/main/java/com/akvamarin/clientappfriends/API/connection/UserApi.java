package com.akvamarin.clientappfriends.API.connection;

import com.akvamarin.clientappfriends.domain.dto.ViewUserSlimDTO;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

/** API-интерфейс пользователя **/
public interface UserApi {
    String PATH_PREFIX = "/api/v1/users";

    @GET(PATH_PREFIX + "/")
    Call<List<ViewUserSlimDTO>> getAllSlimUsers();
}
