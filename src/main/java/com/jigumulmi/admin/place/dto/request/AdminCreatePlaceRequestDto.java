package com.jigumulmi.admin.place.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.jigumulmi.place.dto.response.PlaceCategoryDto;
import com.jigumulmi.place.dto.response.PlaceResponseDto.PositionDto;
import com.jigumulmi.place.vo.District;
import com.jigumulmi.place.vo.Region;
import io.swagger.v3.oas.annotations.media.Schema;
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
    @Schema(description = "시군구 ID", implementation = Integer.class)
    @JsonProperty("districtId")
    private District district;
    private String address;
    private String contact;
    private String placeUrl;
    private PositionDto position;
    private String additionalInfo;
    private String registrantComment;
    private Boolean isApproved = false;
    private List<Long> subwayStationIdList = new ArrayList<>();
    private String kakaoPlaceId = null;
}
