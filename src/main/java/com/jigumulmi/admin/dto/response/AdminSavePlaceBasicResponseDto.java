package com.jigumulmi.admin.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AdminSavePlaceBasicResponseDto {

    @Schema(description = "생성 혹은 수정된 장소의 placeId")
    private Long placeId;
}
