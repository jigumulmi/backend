package com.jigumulmi.place.dto.request;

import com.jigumulmi.place.vo.PlaceCategoryGroup;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springdoc.core.annotations.ParameterObject;

@Getter
@Setter
@ToString
@NoArgsConstructor
@ParameterObject
public class GetPlaceListRequestDto {

    @Parameter(description = "지하철 ID")
    private Long subwayStationId;

    @Parameter(description = "장소 이름, 검색어로 시작하는 장소 조회")
    private String placeName;

    @Parameter(description = "장소 상위 카테고리")
    private PlaceCategoryGroup categoryGroup;
}
