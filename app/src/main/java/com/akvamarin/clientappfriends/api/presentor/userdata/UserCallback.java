package com.akvamarin.clientappfriends.api.presentor.userdata;

import com.akvamarin.clientappfriends.domain.dto.ViewUserDTO;

public interface UserCallback {
    void onUserRetrieved(ViewUserDTO user);
    void onUserRetrievalError(int responseCode);
}

