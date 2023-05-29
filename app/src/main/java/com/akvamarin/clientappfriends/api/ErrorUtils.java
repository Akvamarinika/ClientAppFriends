package com.akvamarin.clientappfriends.api;

import java.io.IOException;
import java.lang.annotation.Annotation;

import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Response;

public class ErrorUtils {

    public static ErrorResponse parseError(Response<?> response, RetrofitService retrofitService) {
        Converter<ResponseBody, ErrorResponse> converter =
                retrofitService.getRetrofit()
                        .responseBodyConverter(ErrorResponse.class, new Annotation[0]);

        ErrorResponse error = null;

        try {
            if (response != null && response.errorBody() != null) {
                error = converter.convert(response.errorBody());
            }
        } catch (IOException e) {
            return new ErrorResponse();
        }

        return error;
    }
}