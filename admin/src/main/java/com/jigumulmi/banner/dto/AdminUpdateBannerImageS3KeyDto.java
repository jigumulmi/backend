package com.jigumulmi.banner.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AdminUpdateBannerImageS3KeyDto {

    private String newKey;
    private String oldKey;
}
