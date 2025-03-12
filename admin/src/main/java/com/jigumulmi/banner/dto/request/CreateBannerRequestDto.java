package com.jigumulmi.banner.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@AllArgsConstructor
public class CreateBannerRequestDto {

    @NotBlank
    @Size(max = 50)
    private String title;
    @Schema(description = "메인페이지에서 노출되는 배너 이미지")
    private MultipartFile outerImage;
    @Schema(description = "배너 클릭 후 노출되는 이미지")
    private MultipartFile innerImage;
    @Schema(requiredMode = RequiredMode.REQUIRED)
    @NotNull
    private Boolean isActive;
}
