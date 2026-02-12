package com.folkislove.love.dto.request;

import lombok.*;
import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventRequest {

    private String title;
    private String description;
    private LocalDateTime dateTime;
    private String city;
    private String country;
    private String venue;
    private String link;
    private Set<Long> tagIds;
}
