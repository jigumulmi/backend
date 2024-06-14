package com.jigumulmi.admin.dto.request;

import com.jigumulmi.place.dto.response.PlaceDetailResponseDto;
import com.jigumulmi.place.dto.response.PlaceResponseDto.PositionDto;
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
    private PlaceDetailResponseDto.OpeningHourDto openingHour;
    private String mainImageUrl;
    private String placeUrl;
    private PositionDto position;
    private String additionalInfo;
    private String registrantComment;
    private Boolean isApproved = false;
    private List<Long> subwayStationIdList;
}
