package com.jigumulmi.admin.place.dto.response;

import com.jigumulmi.common.PageDto;
import com.jigumulmi.place.domain.Place;
import com.jigumulmi.place.dto.response.PlaceCategoryDto;
import com.jigumulmi.place.dto.response.PlaceResponseDto;
import com.jigumulmi.place.dto.response.SubwayStationResponseDto;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@Builder
public class AdminPlaceListResponseDto {

    @Getter
    @SuperBuilder
    @NoArgsConstructor
    public static class PlaceDto extends PlaceResponseDto {

        private Boolean isApproved;

        public static PlaceDto from(Place place) {
            SubwayStationResponseDto subwayStationResponseDto;
            if (place.getSubwayStationPlaceList().isEmpty()) {
                subwayStationResponseDto = null;
            } else {
                subwayStationResponseDto = SubwayStationResponseDto.fromMainStation(
                    place.getSubwayStationPlaceList().getFirst().getSubwayStation()
                );
            }

            return PlaceDto.builder()
                .id(place.getId())
                .name(place.getName())
                .position(
                    PositionDto.builder().latitude(place.getLatitude())
                        .longitude(place.getLongitude())
                        .build()
                )
                .subwayStation(subwayStationResponseDto)
                .categoryList(place.getCategoryMappingList().stream()
                    .map(PlaceCategoryDto::fromPlaceCategoryMapping).toList())
                .isApproved(place.getIsApproved())
                .build();
        }
    }

    private PageDto page;
    private List<PlaceDto> data;
}
