package com.akvamarin.clientappfriends.domain.dto;

import com.akvamarin.clientappfriends.domain.enums.DayPeriodOfTime;
import com.akvamarin.clientappfriends.domain.enums.Partner;
import com.akvamarin.clientappfriends.domain.enums.Partner;
import com.google.gson.annotations.SerializedName;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDate;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ViewEventDTO implements Serializable {
    private Long id;

    private String name;

    private String description;

    @SerializedName("date")
    private String date;

    private DayPeriodOfTime periodOfTime;

    private Partner partner;

    private EventCategoryDTO eventCategory;

    private ViewUserSlimDTO userOwner;

    private Double lat;

    private Double lon;



}
