package com.jigumulmi.place.dto.response;

import com.jigumulmi.place.domain.PlaceImage;
import com.jigumulmi.place.vo.PlaceCategory;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;


@Getter
@SuperBuilder
@NoArgsConstructor
public class PlaceResponseDto {

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PositionDto {

        private Double latitude;

        private Double longitude;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ImageDto {

        private Long id;
        private String url;
        private Boolean isMain;

        public static ImageDto from(PlaceImage image) {
            return ImageDto.builder()
                .id(image.getId())
                .url(image.getUrl())
                .isMain(image.getIsMain())
                .build();
        }
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SurroundingDateOpeningHour {

        private String yesterday;
        private String today;
        private String tomorrow;
    }


    private Long id;

    private String name;

    @Setter
    private List<ImageDto> imageList;

    private PositionDto position;

    private SubwayStationResponseDto subwayStation;

    private PlaceCategory category;

    private SurroundingDateOpeningHour surroundingDateOpeningHour;

    @Setter
    private String currentOpeningInfo;
}
