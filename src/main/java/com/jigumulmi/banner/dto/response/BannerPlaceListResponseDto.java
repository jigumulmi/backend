package com.jigumulmi.banner.dto.response;

import com.jigumulmi.banner.dto.response.BannerPlaceListResponseDto.BannerPlaceDto;
import com.jigumulmi.common.PagedResponseDto;
import com.jigumulmi.place.domain.Place;
import com.jigumulmi.place.dto.ImageDto;
import com.jigumulmi.place.dto.PositionDto;
import com.jigumulmi.place.dto.response.PlaceCategoryDto;
import com.jigumulmi.place.dto.response.SurroundingDateBusinessHour;
import com.jigumulmi.place.vo.CurrentOpeningStatus;
import com.jigumulmi.place.vo.District;
import com.jigumulmi.place.vo.Region;
import java.time.LocalTime;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class BannerPlaceListResponseDto extends PagedResponseDto<BannerPlaceDto> {

    @Getter
    @Builder
    public static class BannerPlaceDto {

        private Long id;
        private String name;
        private Region region;
        private District district;
        private PositionDto position;
        private List<PlaceCategoryDto> categoryList;
        private List<ImageDto> imageList;
        private CurrentOpeningStatus currentOpeningStatus;

        public static BannerPlaceDto from(Place place) {
            return BannerPlaceDto.builder()
                .id(place.getId())
                .name(place.getName())
                .region(place.getRegion())
                .district(place.getDistrict())
                .position(
                    PositionDto.builder()
                        .longitude(place.getLongitude())
                        .latitude(place.getLatitude())
                        .build()
                )
                .categoryList(place.getCategoryMappingList().stream()
                    .map(PlaceCategoryDto::fromPlaceCategoryMapping).toList())
                .imageList(place.getPlaceImageList().stream()
                    .map(ImageDto::from).limit(4).toList())
                .build();
        }

        public void setCurrentOpeningStatus(SurroundingDateBusinessHour surroundingDateBusinessHour, LocalTime currentTime) {
            this.currentOpeningStatus = CurrentOpeningStatus.getLiveOpeningStatus(
                surroundingDateBusinessHour, currentTime);
        }
    }
}
