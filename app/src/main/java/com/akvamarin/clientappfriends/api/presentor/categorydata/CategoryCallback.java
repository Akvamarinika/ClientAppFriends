package com.akvamarin.clientappfriends.api.presentor.categorydata;

import com.akvamarin.clientappfriends.domain.dto.EventCategoryDTO;

import java.util.List;

public interface CategoryCallback {
    void onCategoryListRetrieved(List<EventCategoryDTO> categoryDTOList);
    void onCategoryListRetrievalError(int responseCode);
}
