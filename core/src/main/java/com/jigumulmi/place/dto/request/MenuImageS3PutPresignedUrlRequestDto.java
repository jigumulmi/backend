package com.jigumulmi.place.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MenuImageS3PutPresignedUrlRequestDto {

    @NotNull
    private long placeId;
}
