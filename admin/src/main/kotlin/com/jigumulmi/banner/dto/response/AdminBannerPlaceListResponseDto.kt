package com.jigumulmi.banner.dto.response

import com.jigumulmi.common.PagedResponseDto
import com.jigumulmi.place.domain.Place
import com.jigumulmi.place.dto.response.PlaceCategoryDto
import com.jigumulmi.place.dto.response.SubwayStationResponseDto
import com.jigumulmi.place.vo.District

data class AdminBannerPlaceListResponseDto(
    override val page: PageDto,
    override val data: List<BannerPlaceDto>
) : PagedResponseDto<AdminBannerPlaceListResponseDto.BannerPlaceDto>(page, data) {

    data class BannerPlaceDto(
        val id: Long,
        val name: String?,
        val district: District?,
        val subwayStation: SubwayStationResponseDto?,
        val categoryList: List<PlaceCategoryDto>?
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
