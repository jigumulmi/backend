package com.jigumulmi.admin.place.dto.request;

import com.jigumulmi.place.dto.response.PlaceDetailResponseDto;
import com.jigumulmi.place.dto.response.PlaceDetailResponseDto.MenuDto;
import com.jigumulmi.place.dto.response.PlaceCategoryDto;
import com.jigumulmi.place.dto.response.PlaceResponseDto.PositionDto;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AdminCreatePlaceRequestDto {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ImageRequestDto {

        private String url;
        private Boolean isMain = false;
    }

    private String name;
    private List<PlaceCategoryDto> categoryList = new ArrayList<>();
    private String address;
    private String contact;
    private List<MenuDto> menuList = new ArrayList<>();
    private PlaceDetailResponseDto.OpeningHourDto openingHour;
    private List<ImageRequestDto> imageList = new ArrayList<>();
    private String placeUrl;
    private PositionDto position;
    private String additionalInfo;
    private String registrantComment;
    private Boolean isApproved = false;
    private List<Long> subwayStationIdList = new ArrayList<>();
    private String kakaoPlaceId = null;

    private void setKakaoPlaceId(String kakaoPlaceId) {
        this.kakaoPlaceId = (Objects.equals(kakaoPlaceId, "")) ? null : kakaoPlaceId;
    }
}
