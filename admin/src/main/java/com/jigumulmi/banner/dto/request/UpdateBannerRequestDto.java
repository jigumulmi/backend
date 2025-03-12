package com.jigumulmi.banner.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UpdateBannerRequestDto {

    @NotBlank
    @Size(max = 50)
    private String title;
    @Schema(requiredMode = RequiredMode.REQUIRED)
    @NotNull
    private Boolean isActive;
}
