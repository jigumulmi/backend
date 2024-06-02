package com.jigumulmi.place.dto.response;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
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

    private Long id;

    private String name;

    private String mainImageUrl;

    private PositionDto position;

    private List<SubwayStationResponseDto> subwayStationList;
}
