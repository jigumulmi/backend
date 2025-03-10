package com.jigumulmi.admin.place.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AdminS3PutPresignedUrlResponseDto {

    @Schema(description = "presigned url")
    private String url;
    @Schema(description = "확장자 뺀 파일이름 (uuid)")
    private String filename;
}
