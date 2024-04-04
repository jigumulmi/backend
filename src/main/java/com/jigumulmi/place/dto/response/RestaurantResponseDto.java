package com.jigumulmi.place.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;


@Getter
@Builder
public class RestaurantResponseDto {
    @Getter
    @Builder
    public static class MenuDto {
        private Long id;

        private String name;
    }

    @Getter
    @Builder
    public static class OpeningHourDto {
        private String openingHourSun;

        private String openingHourMon;

        private String openingHourTue;

        private String openingHourWed;

        private String openingHourThu;

        private String openingHourFri;

        private String openingHourSat;
    }

    @Getter
    @Builder
    public static class PositionDto {
        private Double latitude;

        private Double longitude;
    }

    private Long id;

    private String name;

    private String category;

    private String address;

    private String contact;

    private List<MenuDto> menuList;

    private OpeningHourDto openingHour;

    private String additionalInfo;

    private String mainImageUrl;

    private String placeUrl;

    private PositionDto position;

    private SubwayStationResponseDto subwayStation;
}
