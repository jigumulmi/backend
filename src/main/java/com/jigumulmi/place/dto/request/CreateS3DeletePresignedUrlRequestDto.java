package com.jigumulmi.place.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CreateS3DeletePresignedUrlRequestDto {

    @Schema(description = "확장자 포함한 파일 이름")
    @NotBlank
    private String fullFilename;
}
