package com.folkislove.love.dto.request;

import lombok.Data;

import java.util.Set;

@Data
public class PostRequest {

    private String title;
    private String content;
    private Set<Long> tagIds;
}
