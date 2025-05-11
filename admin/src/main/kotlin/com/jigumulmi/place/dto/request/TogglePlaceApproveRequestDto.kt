package com.jigumulmi.place.dto.request

import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode
import jakarta.validation.constraints.NotNull

data class TogglePlaceApproveRequestDto (
    @Schema(description = "승인 요청 -> true, 미승인 요청 -> false", requiredMode = RequiredMode.REQUIRED)
    val approve: @NotNull Boolean
)
