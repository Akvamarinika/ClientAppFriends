package com.akvamarin.clientappfriends.api.presentor;

public interface BaseCallback {
    void onRetrieved();
    void onRetrievalError(int responseCode);
}
