package com.akvamarin.clientappfriends.api.presentor.userdata;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.akvamarin.clientappfriends.api.RetrofitService;
import com.akvamarin.clientappfriends.api.connection.UserApi;
import com.akvamarin.clientappfriends.api.presentor.BaseCallback;
import com.akvamarin.clientappfriends.domain.dto.AuthToken;
import com.akvamarin.clientappfriends.domain.dto.UserDTO;
import com.akvamarin.clientappfriends.domain.dto.ViewUserDTO;
import com.akvamarin.clientappfriends.utils.Constants;
import com.akvamarin.clientappfriends.utils.PreferenceManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserDataApi {
    private static final String TAG = "UserDataApi";
    private static final int REQUEST_CODE_ERROR = -1;
    private final RetrofitService retrofitService;
    private final UserApi userApi;
    private final PreferenceManager preferenceManager;
    private final Context context;

    public UserDataApi(Context context) {
        this.context = context;
        retrofitService = RetrofitService.getInstance(context);
        userApi = retrofitService.getRetrofit().create(UserApi.class);
        preferenceManager = new PreferenceManager(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void getUserByLogin(UserCallback callback) {
        String login = preferenceManager.getString(Constants.KEY_LOGIN);
        String authToken = preferenceManager.getString(Constants.KEY_APP_TOKEN);

        if (!login.equals("login") && authToken != null) {
            userApi.getUserByLogin(login, new AuthToken(authToken)).enqueue(new Callback<>() {
                @Override
                public void onResponse(@NonNull Call<ViewUserDTO> call, @NonNull Response<ViewUserDTO> response) {
                    Log.d(TAG, String.format("Response getUserByLogin: %s %s%n", response.code(), response));
                    Log.d(TAG, String.format("My token: %s", authToken));

                    if (response.isSuccessful()) {
                        ViewUserDTO user = response.body();
                        if (user != null) {
                            preferenceManager.putLong(Constants.KEY_USER_ID, user.getId()); // user id
                            callback.onUserRetrieved(user);
                        }
                    } else {
                        callback.onUserRetrievalError(response.code());
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ViewUserDTO> call, @NonNull Throwable t) {
                    Log.d(TAG, "error getUserByLogin: " + t.fillInStackTrace());
                    callback.onUserRetrievalError(REQUEST_CODE_ERROR);
                }
            });
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void getUserById(Long userId, UserCallback callback) {
        String authToken = preferenceManager.getString(Constants.KEY_APP_TOKEN);

        if (userId != null && authToken != null) {
            userApi.getUserById(userId, new AuthToken(authToken)).enqueue(new Callback<>() {
                @Override
                public void onResponse(@NonNull Call<ViewUserDTO> call, @NonNull Response<ViewUserDTO> response) {
                    Log.d(TAG, String.format("Response getUserById: %s %s%n", response.code(), response));
                    Log.d(TAG, String.format("My token: %s", authToken));

                    if (response.isSuccessful()) {
                        ViewUserDTO user = response.body();
                        if (user != null) {
                            callback.onUserRetrieved(user);
                        }
                    } else {
                        callback.onUserRetrievalError(response.code());
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ViewUserDTO> call, @NonNull Throwable t) {
                    Log.d(TAG, "error getUserById: " + t.fillInStackTrace());
                    callback.onUserRetrievalError(REQUEST_CODE_ERROR);
                }
            });
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void updateUserProfile(UserDTO userDTO, BaseCallback callback) {
        String authToken = preferenceManager.getString(Constants.KEY_APP_TOKEN);
        Log.d(TAG, "updateUserProfile: " + userDTO);

        if (authToken != null) {
            userApi.updateUser(userDTO, new AuthToken(authToken)).enqueue(new Callback<>() {
                @Override
                public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                    Log.d(TAG, String.format("Response updateUserProfile: %s %s%n", response.code(), response));
                    Log.d(TAG, String.format("My token: %s", authToken));

                    if (response.isSuccessful()) {
                        callback.onRetrieved();
                    } else {
                        callback.onRetrievalError(response.code());
                    }
                }

                @Override
                public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                    Log.d(TAG, "error Update user: " + t.fillInStackTrace());
                    callback.onRetrievalError(REQUEST_CODE_ERROR);
                }
            });
        }
    }

    public void deleteUser(BaseCallback callback) {
        Long id = preferenceManager.getLong(Constants.KEY_USER_ID);
        String authToken = preferenceManager.getString(Constants.KEY_APP_TOKEN);
        Log.d(TAG, "ID user: " + id);

        if (id != null && authToken != null) {
            userApi.deleteUser(id, new AuthToken(authToken)).enqueue(new Callback<>() {
                @Override
                public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                    if (response.isSuccessful()) {
                        preferenceManager.clear(); // all values clear
                        callback.onRetrieved();
                    } else {
                        callback.onRetrievalError(response.code());
                    }
                }

                @Override
                public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                    Log.d(TAG, "Error delete user: " + t.fillInStackTrace());
                    callback.onRetrievalError(REQUEST_CODE_ERROR);
                }
            });
        }
    }


}
