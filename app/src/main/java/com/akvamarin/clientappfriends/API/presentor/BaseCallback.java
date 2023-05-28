package com.akvamarin.clientappfriends.API.presentor;

public interface BaseCallback {
    void onRetrieved();
    void onRetrievalError(int responseCode);
}
