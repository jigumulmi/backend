package com.jigumulmi.banner.dto.response;

import com.jigumulmi.banner.domain.Banner;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class BannerResponseDto {

    private Long id;
    private String title;
    private String outerImageS3Key;

    public static BannerResponseDto from(Banner banner) {
        return BannerResponseDto.builder()
            .id(banner.getId())
            .title(banner.getTitle())
            .outerImageS3Key(banner.getOuterImageS3Key())
            .build();
    }
}
