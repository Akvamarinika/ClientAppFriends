package com.akvamarin.clientappfriends.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentDTO {
    private Long id;

    private String text;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private Long userId;

    private Long eventId;

    public static CommentDTO convertInSendServerDto(ViewCommentDTO viewCommentDTO) {
        return CommentDTO.builder()
                .id(viewCommentDTO.getId())
                .text(viewCommentDTO.getText())
                .userId(viewCommentDTO.getUserSlimDTO().getId())
                .eventId(viewCommentDTO.getEventId())
                .build();
    }
}
