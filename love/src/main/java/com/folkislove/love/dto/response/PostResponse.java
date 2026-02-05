package com.folkislove.love.dto.response;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PostResponse {
    
    private String title;

    private String content;

    private String authorUsername;

    private LocalDateTime createdAt;

    private List<String> tags;

    private Integer commentsCount;

}
