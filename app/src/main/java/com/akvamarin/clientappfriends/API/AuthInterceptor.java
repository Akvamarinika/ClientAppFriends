package com.akvamarin.clientappfriends.API;

import android.content.Context;

import com.akvamarin.clientappfriends.utils.Constants;
import com.akvamarin.clientappfriends.utils.PreferenceManager;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import lombok.AllArgsConstructor;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

@AllArgsConstructor
public class AuthInterceptor implements Interceptor {
    private final PreferenceManager preferenceManager;
    private final Context context;

    @NotNull
    @Override
    public Response intercept(@NotNull Chain chain) throws IOException {
        Request request = chain.request();
        String authToken = preferenceManager.getString(Constants.KEY_APP_TOKEN);

        if (authToken != null) {
            request = request.newBuilder()
                    .header("Authorization", "Bearer " + authToken)
                    .build();
        }

        Response response = chain.proceed(request);

        // Handle errors
        int responseCode = response.code();
        String errorMessage = getErrorMessage(responseCode);
        if (errorMessage != null) {
            //displayError(errorMessage);
        }

        return response; // Response from the server
    }

    private String getErrorMessage(int responseCode) {
        return switch (responseCode) {
            case 400 -> "400 Bad Request: что-то пошло не так.";
            case 401 -> "401 Unauthorized: вы неавторизованы для данного действия.";
            case 403 -> "403 Forbidden: у Вас нет доступа к этим ресурсам.";
            case 404 -> "404 Not Found: данный объект не найден.";
            default -> null;
        };
    }

//    private void displayError(String errorMessage) {
//        ((Activity) context).runOnUiThread(() -> {
//            errorTextView.setText(errorMessage);
//            errorTextView.setVisibility(View.VISIBLE);
//        });
//    }
}
