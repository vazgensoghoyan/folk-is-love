package com.folkislove.common.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.util.Set;

@Data
public class PostRequest {

    @NotBlank
    @Size(max = 255)
    private String title;

    @NotBlank
    private String content;

    private Set<@NotNull Long> tagIds;
}
