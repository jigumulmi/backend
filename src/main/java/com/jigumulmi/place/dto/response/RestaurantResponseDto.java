package com.jigumulmi.place.dto.response;

import com.jigumulmi.place.domain.SubwayStation;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class RestaurantResponseDto {

    private Long id;

    private String name;

    private String category;

    private String address;

    private String contact;

    private List<String> menuList;

    private String openingHourSun;

    private String openingHourMon;

    private String openingHourTue;

    private String openingHourWed;

    private String openingHourThu;

    private String openingHourFri;

    private String openingHourSat;

    private String additionalInfo;

    private String mainImageUrl;

    private String placeUrl;

    private Double longitude;

    private Double latitude;

    private SubwayStation subwayStation;
}
