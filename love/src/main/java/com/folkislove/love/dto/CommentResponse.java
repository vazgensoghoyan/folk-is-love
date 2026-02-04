package com.folkislove.love.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class CommentResponse {

    private Long id;

    private String content;

    private String authorUsername;

    private LocalDateTime createdAt;

    private boolean edited;

}
