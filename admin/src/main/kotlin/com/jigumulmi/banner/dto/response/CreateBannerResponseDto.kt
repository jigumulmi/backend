package com.jigumulmi.banner.dto.response

import com.fasterxml.jackson.annotation.JsonProperty
import com.jigumulmi.banner.dto.AdminCreateBannerImageS3KeyDto
import io.swagger.v3.oas.annotations.media.Schema

data class CreateBannerResponseDto(
    val bannerId: Long,

    @Schema(hidden = true)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    val s3KeyDto: AdminCreateBannerImageS3KeyDto
)
