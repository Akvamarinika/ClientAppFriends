package com.akvamarin.clientappfriends.domain.dto;

import com.akvamarin.clientappfriends.domain.enums.DayPeriodOfTime;
import com.akvamarin.clientappfriends.domain.enums.Partner;
import com.akvamarin.clientappfriends.domain.enums.SortingType;

import java.time.DayOfWeek;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventFilter {
    private Long cityId;

    private List<Long> categoryIds;

    private boolean isUserOrganizer;

    private List<Partner> partnerList;

    private List<DayOfWeek> daysOfWeekList;

    private List<DayPeriodOfTime> periodOfTimeList;

    private SortingType sortingType;

    private Long currentUserId;

    private CurrentUserCoords userCoords;
}
