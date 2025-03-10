package com.jigumulmi.admin.banner.dto.response;

import com.jigumulmi.admin.banner.dto.response.AdminBannerPlaceListResponseDto.BannerPlaceDto;
import com.jigumulmi.common.PagedResponseDto;
import com.jigumulmi.place.domain.Place;
import com.jigumulmi.place.dto.response.PlaceCategoryDto;
import com.jigumulmi.place.dto.response.SubwayStationResponseDto;
import com.jigumulmi.place.vo.District;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class AdminBannerPlaceListResponseDto extends PagedResponseDto<BannerPlaceDto> {

    @Getter
    @Builder
    public static class BannerPlaceDto {

        private Long id;
        private String name;
        private District district;
        private SubwayStationResponseDto subwayStation;
        private List<PlaceCategoryDto> categoryList;

        public static BannerPlaceDto from(Place place) {
            SubwayStationResponseDto subwayStationResponseDto;
            if (place.getSubwayStationPlaceList().isEmpty()) {
                subwayStationResponseDto = null;
            } else {
                subwayStationResponseDto = SubwayStationResponseDto.fromMainStation(
                    place.getSubwayStationPlaceList().getFirst().getSubwayStation()
                );
            }

            return BannerPlaceDto.builder()
                .id(place.getId())
                .name(place.getName())
                .district(place.getDistrict())
                .subwayStation(subwayStationResponseDto)
                .categoryList(place.getCategoryMappingList().stream()
                    .map(PlaceCategoryDto::fromPlaceCategoryMapping).toList())
                .build();
        }
    }
}
