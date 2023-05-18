package com.akvamarin.clientappfriends.domain.dto;

import org.jetbrains.annotations.NotNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationDTO {

    @NotNull
    private Long eventId;

    @NotNull
    private Long userId;

}
