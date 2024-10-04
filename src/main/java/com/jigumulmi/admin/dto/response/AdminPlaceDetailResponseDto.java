package com.jigumulmi.admin.dto.response;

import com.jigumulmi.place.domain.Place;
import com.jigumulmi.place.domain.SubwayStation;
import com.jigumulmi.place.domain.SubwayStationPlace;
import com.jigumulmi.place.dto.response.PlaceDetailResponseDto;
import com.jigumulmi.place.dto.response.SubwayStationResponseDto;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class AdminPlaceDetailResponseDto extends PlaceDetailResponseDto {

    private List<SubwayStationResponseDto> subwayStationList;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
    private String registrantComment;
    private Boolean isApproved;
    private String kakaoPlaceId;
    private String placeUrl;

    public static AdminPlaceDetailResponseDto from(Place place) {
        List<SubwayStationPlace> subwayStationPlaceList = place.getSubwayStationPlaceList();
        List<SubwayStationResponseDto> subwayStationDtoList = new ArrayList<>();
        for (SubwayStationPlace subwayStationPlace : subwayStationPlaceList) {
            SubwayStation subwayStation = subwayStationPlace.getSubwayStation();
            subwayStationDtoList.add(
                SubwayStationResponseDto.builder()
                    .id(subwayStation.getId())
                    .stationName(subwayStation.getStationName())
                    .isMain(subwayStationPlace.getIsMain())
                    .build()
            );
        }

        List<MenuDto> menuList = place.getMenuList().stream().map(MenuDto::from).toList();

        List<ImageDto> imageList = place.getPlaceImageList().stream().map(ImageDto::from).toList();

        List<CategoryResponseDto> categoryData = CategoryResponseDto.fromMappingList(
            place.getCategoryMappingList().stream()
                .map(CategoryMappingDto::fromPlaceCategoryMapping).toList());

        return AdminPlaceDetailResponseDto.builder()
            .createdAt(place.getCreatedAt())
            .modifiedAt(place.getModifiedAt())
            .id(place.getId())
            .name(place.getName())
            .category(categoryData)
            .address(place.getAddress())
            .contact(place.getContact())
            .imageList(imageList)
            .registrantComment(place.getRegistrantComment())
            .isApproved(place.getIsApproved())
            .additionalInfo(place.getAdditionalInfo())
            .position(
                PositionDto.builder()
                    .longitude(place.getLongitude())
                    .latitude(place.getLatitude())
                    .build()
            )
            .openingHour(
                OpeningHourDto.builder()
                    .openingHourSun(place.getOpeningHourSun())
                    .openingHourMon(place.getOpeningHourMon())
                    .openingHourTue(place.getOpeningHourTue())
                    .openingHourWed(place.getOpeningHourWed())
                    .openingHourThu(place.getOpeningHourThu())
                    .openingHourFri(place.getOpeningHourFri())
                    .openingHourSat(place.getOpeningHourSat())
                    .build()
            )
            .subwayStationList(subwayStationDtoList)
            .menuList(menuList)
            .kakaoPlaceId(place.getKakaoPlaceId())
            .placeUrl(place.getPlaceUrl())
            .build();
    }
}
