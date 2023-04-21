package com.akvamarin.clientappfriends.domain.dto;

import androidx.annotation.NonNull;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventCategoryDTO implements Serializable {
    private Long id;
    private String name;
    private String urlIcon;

    @NonNull
    @Override
    public String toString() {
        return name;
    }
}
