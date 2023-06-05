package com.akvamarin.clientappfriends.api.presentor.eventdata;

import com.akvamarin.clientappfriends.domain.dto.ViewEventDTO;

import java.util.List;

public interface EventListCallback {
    void onEventListRetrieved(List<ViewEventDTO> viewEventDTO);
    void onEventListRetrievalError(int responseCode);
}

