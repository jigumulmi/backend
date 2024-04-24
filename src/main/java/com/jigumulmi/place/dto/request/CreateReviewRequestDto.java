package com.jigumulmi.place.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CreateReviewRequestDto {
    @NotNull
    private Long placeId;
    @NotNull
    private Integer rating;
    @NotBlank
    private String content;
}
