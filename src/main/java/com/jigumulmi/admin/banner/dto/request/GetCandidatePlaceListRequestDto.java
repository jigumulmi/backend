package com.jigumulmi.admin.banner.dto.request;

import com.jigumulmi.place.vo.District;
import com.jigumulmi.place.vo.PlaceCategoryGroup;
import com.jigumulmi.place.vo.Region;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import org.springdoc.core.annotations.ParameterObject;

@Getter
@ParameterObject
public class GetCandidatePlaceListRequestDto {

    @Parameter(required = true)
    @NotNull
    private Long bannerId;

    @Parameter(description = "장소 이름 검색어")
    private String placeName;
    @Parameter(description = "광역시도")
    private Region region;
    @Parameter(description = "시군구 ID", name = "districtId", schema = @Schema(implementation = Integer.class))
    private District district;
    @Parameter(description = "상위 카테고리")
    private PlaceCategoryGroup placeCategoryGroup;
    @Parameter(description = "지하철 ID")
    private Long subwayStationId;
    @Parameter(description = "메뉴 이름 검색어")
    private String menuName;

    public GetCandidatePlaceListRequestDto(Long bannerId, String placeName, Region region, District districtId,
        PlaceCategoryGroup placeCategoryGroup, Long subwayStationId, String menuName) {
        this.bannerId = bannerId;
        this.placeName = placeName;
        this.region = region;
        this.district = districtId;
        this.placeCategoryGroup = placeCategoryGroup;
        this.subwayStationId = subwayStationId;
        this.menuName = menuName;
    }
}
