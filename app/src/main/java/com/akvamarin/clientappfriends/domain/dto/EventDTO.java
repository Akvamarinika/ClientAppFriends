package com.akvamarin.clientappfriends.domain.dto;

import com.akvamarin.clientappfriends.domain.enums.DayPeriodOfTime;
import com.akvamarin.clientappfriends.domain.enums.Partner;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
/**
 * DTO для создания / обновления
 * удаления мероприятия
 * */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventDTO {
    private Long id;

    private String name;

    private String description;

    private String date;

    private DayPeriodOfTime periodOfTime;

    private Partner partner;

    private Long eventCategoryId;

    private Long ownerId;

    private Double lat;

    private Double lon;

}
