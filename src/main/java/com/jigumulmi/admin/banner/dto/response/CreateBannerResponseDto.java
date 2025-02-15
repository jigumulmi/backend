package com.jigumulmi.admin.banner.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import com.jigumulmi.admin.banner.dto.AdminCreateBannerImageS3KeyDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CreateBannerResponseDto {

    private Long bannerId;

    @Schema(hidden = true)
    @JsonProperty(access = Access.WRITE_ONLY)
    private AdminCreateBannerImageS3KeyDto s3KeyDto;
}
