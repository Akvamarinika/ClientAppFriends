package com.akvamarin.clientappfriends.api.connection;

import com.akvamarin.clientappfriends.domain.dto.AuthToken;
import com.akvamarin.clientappfriends.domain.dto.AuthUserParamDTO;
import com.akvamarin.clientappfriends.domain.dto.AuthUserSocialDTO;
import com.akvamarin.clientappfriends.domain.dto.UserDTO;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface AuthenticationApi {
    String PATH_PREFIX = "api/v1/auth";

    @POST(PATH_PREFIX + "/registration/")
    Call<Void> registerUser(@Body UserDTO user);

    @POST(PATH_PREFIX + "/login")
    Call<AuthToken> authUser(@Body AuthUserParamDTO authUserParamDTO);

    @POST(PATH_PREFIX + "/oauth2")
    Call<AuthToken> authUserWithSocial(@Body AuthUserSocialDTO user);

   /* @POST(PATH_PREFIX + "getUser")
    Call<User> getUser(@Body UserSignInDTO userSignInDTO);


    @POST(PATH_PREFIX + "clientSendToken")
    Call<String> sendToken(@Body String token);*/
}
