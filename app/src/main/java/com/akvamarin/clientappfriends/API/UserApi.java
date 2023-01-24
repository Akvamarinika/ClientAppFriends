package com.akvamarin.clientappfriends.API;

import com.akvamarin.clientappfriends.dto.User;
import com.akvamarin.clientappfriends.dto.UserSignInDTO;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface UserApi {
    @POST("users/add")
    Call<User> save(@Body User user);

    @POST("/addUser")
    Call<User> addUser(@Body User user);

    @POST("/getUser")
    Call<User> getUser(@Body UserSignInDTO userSignInDTO);


    @POST("/clientSendToken")
    Call<String> sendToken(@Body String token);
}
