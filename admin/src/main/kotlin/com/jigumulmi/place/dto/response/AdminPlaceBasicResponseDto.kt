package com.jigumulmi.place.dto.response

import com.jigumulmi.place.domain.Place
import com.jigumulmi.place.domain.SubwayStationPlace
import com.jigumulmi.place.dto.PositionDto
import com.jigumulmi.place.vo.Region
import java.time.LocalDateTime
import java.util.stream.Collectors

data class AdminPlaceBasicResponseDto(
    val createdAt: LocalDateTime? = null,
    val modifiedAt: LocalDateTime? = null,
    val id: Long? = null,
    val name: String? = null,
    val position: PositionDto = PositionDto(),
    val categoryList: List<PlaceCategoryDto>,
    val region: Region? = null,
    val district: DistrictResponseDto? = null,
    val address: String? = null,
    val contact: String? = null,
    val subwayStationList: List<SubwayStationResponseDto>,
    val registrantComment: String? = null,
    val isApproved: Boolean? = null,
    val kakaoPlaceId: String? = null,
    val placeUrl: String? = null,
    val additionalInfo: String? = null,
) {
    companion object {
        fun from(place: Place): AdminPlaceBasicResponseDto {
            val categoryList = place.categoryMappingList.stream()
                .map(PlaceCategoryDto::fromPlaceCategoryMapping)
                .toList()

            val subwayStationList = place.subwayStationPlaceList
                .stream()
                .map { subwayStationPlace: SubwayStationPlace ->
                    SubwayStationResponseDto.builder()
                        .id(subwayStationPlace.subwayStation.id)
                        .stationName(subwayStationPlace.subwayStation.stationName)
                        .isMain(subwayStationPlace.isMain)
                        .build()
                }
                .collect(Collectors.toList())

            return AdminPlaceBasicResponseDto(
                createdAt = place.createdAt,
                modifiedAt = place.modifiedAt,
                id = place.id,
                name = place.name,
                categoryList = categoryList,
                region = place.region,
                district = DistrictResponseDto.fromDistrict(place.district),
                address = place.address,
                contact = place.contact,
                registrantComment = place.registrantComment,
                isApproved = place.isApproved,
                additionalInfo = place.additionalInfo,
                position = PositionDto.builder()
                    .latitude(place.latitude)
                    .longitude(place.longitude)
                    .build(),
                subwayStationList = subwayStationList,
                kakaoPlaceId = place.kakaoPlaceId,
                placeUrl = place.placeUrl,
            )
        }
    }
}
