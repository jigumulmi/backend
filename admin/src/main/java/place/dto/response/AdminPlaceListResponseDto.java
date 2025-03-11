package place.dto.response;

import com.jigumulmi.common.PagedResponseDto;
import com.jigumulmi.place.domain.Place;
import com.jigumulmi.place.dto.ImageDto;
import com.jigumulmi.place.dto.PositionDto;
import com.jigumulmi.place.dto.response.PlaceCategoryDto;
import com.jigumulmi.place.dto.response.SubwayStationResponseDto;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import place.dto.response.AdminPlaceListResponseDto.PlaceDto;

@Getter
@SuperBuilder
public class AdminPlaceListResponseDto extends PagedResponseDto<PlaceDto> {

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PlaceDto {

        private Long id;

        private String name;

        @Setter
        private List<ImageDto> imageList;

        private PositionDto position;

        @Setter
        private SubwayStationResponseDto subwayStation;

        @Setter
        private List<PlaceCategoryDto> categoryList;

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
}
