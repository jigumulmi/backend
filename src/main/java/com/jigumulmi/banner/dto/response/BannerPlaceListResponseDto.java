package com.jigumulmi.banner.dto.response;

import com.jigumulmi.common.PageDto;
import com.jigumulmi.place.domain.Place;
import com.jigumulmi.place.dto.ImageDto;
import com.jigumulmi.place.dto.response.PlaceCategoryDto;
import com.jigumulmi.place.dto.response.SurroundingDateBusinessHour;
import com.jigumulmi.place.vo.LiveOpeningStatus;
import com.jigumulmi.place.vo.District;
import com.jigumulmi.place.vo.Region;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class BannerPlaceListResponseDto {

    @Getter
    @Builder
    public static class BannerPlaceDto {

        private Long id;
        private String name;
        private Region region;
        private District district;
        private List<PlaceCategoryDto> categoryList;
        private List<ImageDto> imageList;
        private LiveOpeningStatus liveOpeningStatus;

        public static BannerPlaceDto from(Place place) {
            return BannerPlaceDto.builder()
                .id(place.getId())
                .name(place.getName())
                .region(place.getRegion())
                .district(place.getDistrict())
                .categoryList(place.getCategoryMappingList().stream()
                    .map(PlaceCategoryDto::fromPlaceCategoryMapping).toList())
                .imageList(place.getPlaceImageList().stream()
                    .map(ImageDto::from).limit(4).toList())
                .build();
        }

        public void setLiveOpeningStatus(SurroundingDateBusinessHour surroundingDateBusinessHour) {
            this.liveOpeningStatus = LiveOpeningStatus.getCurrentOpeningInfo(
                surroundingDateBusinessHour);
        }
    }

    private PageDto page;
    private List<BannerPlaceDto> data;

}
