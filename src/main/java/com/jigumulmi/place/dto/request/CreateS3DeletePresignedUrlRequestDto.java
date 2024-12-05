package com.jigumulmi.place.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CreateS3DeletePresignedUrlRequestDto {

    @Schema(description = "메뉴 정보 조회 시 전달한 imageS3Key, 파일이름만 보내시면 안됩니다")
    @NotBlank
    private String s3Key;
}
