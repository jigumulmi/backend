package com.jigumulmi.banner.dto.request

import com.fasterxml.jackson.annotation.JsonProperty
import com.jigumulmi.place.vo.District
import com.jigumulmi.place.vo.PlaceCategoryGroup
import com.jigumulmi.place.vo.Region
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotNull
import org.springdoc.core.annotations.ParameterObject

@ParameterObject
data class GetCandidatePlaceListRequestDto(
    @Parameter(required = true)
    val bannerId: @NotNull Long,

    @Parameter(description = "장소 이름 검색어")
    val placeName: String,

    @Parameter(description = "광역시도")
    val region: Region,

    @Parameter(
        description = "시군구 ID",
        name = "districtId",
        schema = Schema(implementation = Int::class)
    )
    @JsonProperty("districtId")
    val district: District,

    @Parameter(description = "상위 카테고리")
    val placeCategoryGroup: PlaceCategoryGroup,

    @Parameter(description = "지하철 ID")
    val subwayStationId: Long,

    @Parameter(description = "메뉴 이름 검색어")
    val menuName: String
)
