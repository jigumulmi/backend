package com.jigumulmi.admin.dto.request;

import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AdminCreatePlaceRequestDto {

    private String name;
    private String category;
    private String address;
    private String contact;
    private List<String> menuList = new ArrayList<>();
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
    private String registrantComment;
    private Boolean isApproved = false;
    private Long subwayStationId;

}
