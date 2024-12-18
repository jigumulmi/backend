package com.jigumulmi.place.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MenuImageS3PutPresignedUrlRequestDto {

    @NotBlank
    private String fileExtension;
}
