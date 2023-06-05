package com.akvamarin.clientappfriends.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.akvamarin.clientappfriends.domain.dto.EventFilter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class EventFilterPreferences {
    private static final String PREF_NAME = "EventFilterPrefs";
    private static final String KEY_EVENT_FILTER = "EventFilter";

    private final SharedPreferences sharedPreferences;
    private final ObjectMapper objectMapper;

    public EventFilterPreferences(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        objectMapper = new ObjectMapper();
    }

    public void saveEventFilter(EventFilter eventFilter) {
        try {
            String eventFilterJson = objectMapper.writeValueAsString(eventFilter);
            sharedPreferences.edit().putString(KEY_EVENT_FILTER, eventFilterJson).apply();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    public EventFilter getEventFilter() {
        String eventFilterJson = sharedPreferences.getString(KEY_EVENT_FILTER, null);
        if (eventFilterJson != null) {
            try {
                return objectMapper.readValue(eventFilterJson, EventFilter.class);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public void deleteEventFilter() {
        sharedPreferences.edit().remove(KEY_EVENT_FILTER).apply();
    }

}


