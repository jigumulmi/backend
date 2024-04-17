package com.jigumulmi.place.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.experimental.SuperBuilder;


@Getter
@SuperBuilder
public class RestaurantResponseDto {
    @Getter
    @Builder
    public static class PositionDto {
        private Double latitude;

        private Double longitude;
    }

    private Long id;

    private String name;

    private String mainImageUrl;

    private PositionDto position;

    private SubwayStationResponseDto subwayStation;
}
