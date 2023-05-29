package com.akvamarin.clientappfriends.api.connection;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface ImageApi {
    String PATH_PREFIX = "/api/v1/images";

    @Multipart
    @POST(PATH_PREFIX + "/")
    Call<String> uploadImage(@Part MultipartBody.Part image);

    @Multipart
    @POST(PATH_PREFIX + "/{userId}/avatar")
    Call<Void> uploadNewAvatar( @Part MultipartBody.Part file, @Path("userId") Long userId);
}
