package com.jigumulmi.place.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class S3DeletePresignedUrlResponseDto {

    private String url;
}
