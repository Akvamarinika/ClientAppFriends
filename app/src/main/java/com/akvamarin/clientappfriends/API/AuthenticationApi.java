package com.akvamarin.clientappfriends.API;

import com.akvamarin.clientappfriends.domain.dto.AuthUserSocialDTO;
import com.akvamarin.clientappfriends.domain.dto.User;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface AuthenticationApi {
    String PATH_PREFIX = "api/v1/auth";

    @POST(PATH_PREFIX + "/registration")
    Call<User> registerUser(@Body User user);

    @POST(PATH_PREFIX + "/login")
    Call<User> authUser(@Body User user);

    @POST(PATH_PREFIX + "/oauth2")
    Call<Void> authUserWithSocial(@Body AuthUserSocialDTO user);

   /* @POST(PATH_PREFIX + "getUser")
    Call<User> getUser(@Body UserSignInDTO userSignInDTO);


    @POST(PATH_PREFIX + "clientSendToken")
    Call<String> sendToken(@Body String token);*/
}
