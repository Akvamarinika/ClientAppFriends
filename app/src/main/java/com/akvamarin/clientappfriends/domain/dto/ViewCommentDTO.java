package com.akvamarin.clientappfriends.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ViewCommentDTO {
    private Long id;

    private String text;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private ViewUserSlimDTO userSlimDTO;

    private Long eventId;

    @JsonProperty(value = "edited")
    private boolean edited;

    public boolean getEdited() {
        return edited;
    }

}
