package banner.dto.response;

import com.jigumulmi.banner.domain.Banner;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class AdminBannerDetailResponseDto extends AdminBannerResponseDto {

    private LocalDateTime createdAt;
    private String outerImageS3Key;
    private String innerImageS3Key;

    public static AdminBannerDetailResponseDto from(Banner banner) {
        return AdminBannerDetailResponseDto.builder()
            .createdAt(banner.getCreatedAt())
            .modifiedAt(banner.getModifiedAt())
            .id(banner.getId())
            .title(banner.getTitle())
            .outerImageS3Key(banner.getOuterImageS3Key())
            .innerImageS3Key(banner.getInnerImageS3Key())
            .isActive(banner.getIsActive())
            .build();
    }
}
