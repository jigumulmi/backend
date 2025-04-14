package com.jigumulmi.banner.dto.request

import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import org.springframework.web.multipart.MultipartFile


data class CreateBannerRequestDto(
    val title: @NotBlank @Size(max = 50) String,

    @Schema(description = "메인페이지에서 노출되는 배너 이미지")
    val outerImage: MultipartFile? = null,

    @Schema(description = "배너 클릭 후 노출되는 이미지")
    val innerImage: MultipartFile? = null,

    @Schema(requiredMode = RequiredMode.REQUIRED)
    val isActive: @NotNull Boolean,
)
