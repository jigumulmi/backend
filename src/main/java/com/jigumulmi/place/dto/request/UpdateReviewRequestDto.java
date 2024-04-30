package com.jigumulmi.place.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UpdateReviewRequestDto {

    @NotNull
    private Long reviewId;
    private Integer rating;
    private String content;

}
