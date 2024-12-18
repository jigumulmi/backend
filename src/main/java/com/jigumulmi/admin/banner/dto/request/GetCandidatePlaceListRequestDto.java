package com.jigumulmi.admin.banner.dto.request;

import com.jigumulmi.place.vo.District;
import com.jigumulmi.place.vo.PlaceCategoryGroup;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springdoc.core.annotations.ParameterObject;

@Getter
@AllArgsConstructor
@ParameterObject
public class GetCandidatePlaceListRequestDto {

    @Parameter(required = true)
    @NotNull
    private Long bannerId;

    @Parameter(description = "장소 이름 검색어")
    private String placeName;
    @Parameter(description = "시군구")
    private District district;
    @Parameter(description = "상위 카테고리")
    private PlaceCategoryGroup placeCategoryGroup;
    @Parameter(description = "지하철 ID")
    private Long subwayStationId;
    @Parameter(description = "메뉴 이름 검색어")
    private String menuName;

}
