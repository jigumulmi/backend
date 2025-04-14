package com.jigumulmi.place.dto.request

import com.jigumulmi.place.vo.PlaceCategoryGroup
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Schema
import org.springdoc.core.annotations.ParameterObject

@ParameterObject
data class AdminGetPlaceListRequestDto(
    @Parameter(description = "지하철 ID")
    var subwayStationId: Long? = null,

    @Parameter(description = "장소 이름, 검색어로 시작하는 장소 조회")
    val placeName: String? = null,

    @Parameter(description = "장소 상위 카테고리")
    val categoryGroup: PlaceCategoryGroup? = null,

    @Parameter(description = "좋아요 필터")
    @Schema(defaultValue = "false")
    val showLikedOnly: Boolean = false,

    @Parameter(description = "유저 등록 신청 -> false, 관리자 등록 -> true")
    @Schema(defaultValue = "true")
    val isFromAdmin: Boolean = true,
) {
}
