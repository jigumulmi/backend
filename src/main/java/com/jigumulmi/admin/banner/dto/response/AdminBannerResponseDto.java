package com.jigumulmi.admin.banner.dto.response;

import com.jigumulmi.banner.domain.Banner;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AdminBannerResponseDto {

    private LocalDateTime modifiedAt;
    private Long id;
    private String title;
    private Boolean isActive;

    public static AdminBannerResponseDto from(Banner banner) {
        return AdminBannerResponseDto.builder()
            .id(banner.getId())
            .title(banner.getTitle())
            .modifiedAt(banner.getModifiedAt())
            .isActive(banner.getIsActive())
            .build();
    }
}
