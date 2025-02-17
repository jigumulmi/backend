package com.jigumulmi.admin.place.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.jigumulmi.config.exception.CustomException;
import com.jigumulmi.config.exception.errorCode.CommonErrorCode;
import com.jigumulmi.place.dto.PositionDto;
import com.jigumulmi.place.dto.response.PlaceCategoryDto;
import com.jigumulmi.place.vo.District;
import com.jigumulmi.place.vo.Region;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AdminCreatePlaceRequestDto {

    private String name;
    private List<PlaceCategoryDto> categoryList = new ArrayList<>();
    private Region region;
    @Schema(title = "시군구 ID", implementation = Integer.class)
    @JsonProperty("districtId")
    private District district;
    private String address;
    private String contact;
    private String placeUrl;
    private PositionDto position;
    private String additionalInfo;
    private String registrantComment;
    @Schema(requiredMode = RequiredMode.REQUIRED)
    private Boolean isApproved;
    @Schema(description = "첫 ID가 메인 지하철이 됩니다")
    private List<Long> subwayStationIdList = new ArrayList<>();
    @Schema(description = "값이 없으면 null")
    private String kakaoPlaceId;

    public void validate() {
        if (isApproved == null) {
            throw new CustomException(CommonErrorCode.UNPROCESSABLE_ENTITY, "isApproved 누락");
        }

        if (kakaoPlaceId != null && kakaoPlaceId.trim().isEmpty()) {
            throw new CustomException(CommonErrorCode.UNPROCESSABLE_ENTITY, "kakaoPlaceId 오류");
        }
    }
}
