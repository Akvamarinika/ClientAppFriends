package com.akvamarin.clientappfriends.API;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

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
        /*switch (response.code()) {
            case 400 -> showDialog("Bad Request", "Do you want to retry?", chain);
            case 401 -> showDialog("Unauthorized", "Do you want to login?", chain);
            case 403 -> showDialog("Forbidden", "Do you want to try again?", chain);
            case 404 -> showDialog("Not Found", "Do you want to search again?", chain);
        }*/
        return response; //ответ от сервера
    }

    private void showDialog(String title, String message, Interceptor.Chain chain) {
        new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Retry the request
                        try {
                            Response response = chain.proceed(chain.request());
                            // handle response
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}
