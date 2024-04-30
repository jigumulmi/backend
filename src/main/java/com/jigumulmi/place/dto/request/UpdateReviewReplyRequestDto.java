package com.jigumulmi.place.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UpdateReviewReplyRequestDto {

    @NotNull
    private Long reviewReplyId;
    @NotBlank
    private String content;
}
