package com.jigumulmi.place.dto.response

import io.swagger.v3.oas.annotations.media.Schema

data class AdminS3PutPresignedUrlResponseDto (
    @Schema(description = "presigned url")
    private val url: String,

    @Schema(description = "확장자 뺀 파일이름 (uuid)")
    private val filename: String
)
