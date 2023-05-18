package com.akvamarin.clientappfriends.domain.dto;

import com.akvamarin.clientappfriends.domain.enums.FeedbackType;

import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ViewNotificationDTO implements Serializable {

    private Long id;

    private Long eventId;

    private String eventName;

    private Long userId;

    private String userNickname;

    private String userUrlAvatar;

    private FeedbackType feedbackType;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private boolean forOwner;

    private boolean isHiddenBtn;

}
