package com.jigumulmi.banner.dto.request

import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size

data class UpdateBannerRequestDto (
    val title: @NotBlank @Size(max = 50) String,

    @Schema(requiredMode = RequiredMode.REQUIRED)
    val isActive: @NotNull Boolean
)
