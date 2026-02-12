package com.folkislove.love.dto.response;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EventResponse {

    private String title;
    private String description;
    private LocalDateTime dateTime;
    private String city;
    private String country;
    private String venue;
    private String link;
    private String authorUsername;
    private LocalDateTime createdAt;
    private List<String> tags;
}
