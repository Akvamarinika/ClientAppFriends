package com.akvamarin.clientappfriends.api.presentor.locationdata;

import com.akvamarin.clientappfriends.domain.dto.CityDTO;

import java.util.List;

public interface CityCallback {
    void onCityRetrieved(List<CityDTO> cities);
    void onCityRetrievalError(int responseCode);
}
