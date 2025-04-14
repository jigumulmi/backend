package com.jigumulmi.place.dto.request

import com.fasterxml.jackson.annotation.JsonProperty
import com.jigumulmi.config.exception.CustomException
import com.jigumulmi.config.exception.errorCode.CommonErrorCode
import com.jigumulmi.place.dto.PositionDto
import com.jigumulmi.place.dto.response.PlaceCategoryDto
import com.jigumulmi.place.vo.District
import com.jigumulmi.place.vo.Region
import io.swagger.v3.oas.annotations.media.Schema

data class AdminCreatePlaceRequestDto (
    val name: String? = null,
    val categoryList: List<PlaceCategoryDto> = ArrayList(),
    val region: Region? = null,

    @Schema(title = "시군구 ID", implementation = Int::class)
    @JsonProperty("districtId")
    val district: District? = null,
    val address: String? = null,
    val contact: String? = null,
    val placeUrl: String? = null,
    val position: PositionDto = PositionDto(),
    val additionalInfo: String? = null,
    val registrantComment: String? = null,

    @Schema(description = "첫 ID가 메인 지하철이 됩니다")
    val subwayStationIdList: List<Long> = ArrayList(),

    @Schema(description = "값이 없으면 null")
    val kakaoPlaceId: String? = null,
){

    fun validate() {
        if (kakaoPlaceId != null && kakaoPlaceId.trim { it <= ' ' }.isEmpty()) {
            throw CustomException(CommonErrorCode.UNPROCESSABLE_ENTITY, "kakaoPlaceId 오류")
        }
    }
}
