package com.jigumulmi.place.dto.response;

import com.jigumulmi.place.domain.PlaceImage;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;


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

    private Long id;

    private String name;

    @Setter
    private List<ImageDto> imageList;

    private PositionDto position;

    private SubwayStationResponseDto subwayStation;

    private String category;

    @Setter
    private String currentOpeningInfo;
}
