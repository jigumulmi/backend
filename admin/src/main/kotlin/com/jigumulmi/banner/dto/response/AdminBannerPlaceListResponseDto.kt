package com.jigumulmi.banner.dto.response

import com.jigumulmi.common.AdminPagedResponseDto
import com.jigumulmi.place.domain.Place
import com.jigumulmi.place.dto.response.PlaceCategoryDto
import com.jigumulmi.place.dto.response.SubwayStationResponseDto
import com.jigumulmi.place.vo.District

data class AdminBannerPlaceListResponseDto(
    override val page: PageDto,
    override val data: List<BannerPlaceDto>
) : AdminPagedResponseDto<AdminBannerPlaceListResponseDto.BannerPlaceDto>(page, data) {

    data class BannerPlaceDto(
        val id: Long,
        val name: String? = null,
        val district: District? = null,
        val subwayStation: SubwayStationResponseDto? = null,
        val categoryList: List<PlaceCategoryDto> = emptyList()
    ) {
        companion object {
            fun from(place: Place): BannerPlaceDto {
                val subwayStationResponseDto = if (place.subwayStationPlaceList.isEmpty()) {
                    null
                } else {
                    SubwayStationResponseDto.fromMainStation(
                        place.subwayStationPlaceList.first().subwayStation
                    )
                }

                return BannerPlaceDto(
                    id = place.id,
                    name = place.name,
                    district = place.district,
                    subwayStation = subwayStationResponseDto,
                    categoryList = place.categoryMappingList.map { mapping ->
                        PlaceCategoryDto.fromPlaceCategoryMapping(mapping)
                    }
                )
            }
        }
    }
}
