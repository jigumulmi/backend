package banner.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AdminCreateBannerImageS3KeyDto {

    private String outerImage;
    private String innerImage;
}
