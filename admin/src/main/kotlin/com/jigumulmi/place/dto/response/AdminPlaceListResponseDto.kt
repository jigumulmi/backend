package com.jigumulmi.place.dto.response

import com.jigumulmi.common.AdminPagedResponseDto
import com.jigumulmi.place.domain.Place
import com.jigumulmi.place.dto.ImageDto
import com.jigumulmi.place.dto.PositionDto
import com.jigumulmi.place.dto.response.AdminPlaceListResponseDto.PlaceDto

data class AdminPlaceListResponseDto(
    override val page: PageDto,
    override val data: List<PlaceDto>
) : AdminPagedResponseDto<PlaceDto>(page, data) {
    data class PlaceDto(
        val id: Long,
        val name: String? = null,
        val imageList: List<ImageDto> = emptyList(),
        val position: PositionDto = PositionDto(),
        val subwayStation: SubwayStationResponseDto? = null,
        val categoryList: List<PlaceCategoryDto> = emptyList(),
        val isApproved: Boolean
    ) {
        companion object {
            fun from(place: Place): PlaceDto {
                val subwayStationResponseDto = if (place.subwayStationPlaceList.isEmpty()) {
                    null
                } else {
                    SubwayStationResponseDto.fromMainStation(
                        place.subwayStationPlaceList.first().subwayStation
                    )
                }

                return PlaceDto(
                    id = place.id,
                    name = place.name,
                    position = PositionDto.builder()
                        .latitude(place.latitude)
                        .longitude(place.longitude)
                        .build(),
                    subwayStation = subwayStationResponseDto,
                    categoryList = place.categoryMappingList.map(PlaceCategoryDto::fromPlaceCategoryMapping),
                    isApproved = place.isApproved
                )
            }
        }
    }
}
