package com.akvamarin.clientappfriends.api.presentor.eventdata;

import com.akvamarin.clientappfriends.domain.dto.ViewEventDTO;

public interface EventCallback {
    void onEventRetrieved(ViewEventDTO viewEventDTO);
    void onEventRetrievalError(int responseCode);
}

