package com.jigumulmi.admin.dto.response;

import com.jigumulmi.place.domain.Restaurant;
import com.jigumulmi.place.domain.SubwayStation;
import com.jigumulmi.place.domain.SubwayStationPlace;
import com.jigumulmi.place.dto.response.RestaurantDetailResponseDto;
import com.jigumulmi.place.dto.response.SubwayStationResponseDto;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@Builder
public class AdminPlaceListResponseDto {

    @Getter
    @SuperBuilder
    public static class PlaceDto extends RestaurantDetailResponseDto {

        private LocalDateTime createdAt;
        private LocalDateTime modifiedAt;
        private String registrantComment;
        private Boolean isApproved;

        public static PlaceDto from(Restaurant restaurant) {
            List<SubwayStationPlace> subwayStationPlaceList = restaurant.getSubwayStationPlaceList();
            List<SubwayStationResponseDto> subwayStationDtoList = new ArrayList<>();
            for (SubwayStationPlace subwayStationPlace : subwayStationPlaceList) {
                if (subwayStationPlace.getIsMain()) {
                    SubwayStation subwayStation = subwayStationPlace.getSubwayStation();
                    subwayStationDtoList.add(
                        SubwayStationResponseDto.builder()
                            .id(subwayStation.getId())
                            .lineNumber(subwayStation.getLineNumber())
                            .stationName(subwayStation.getStationName())
                            .isMain(subwayStationPlace.getIsMain())
                            .build()
                    );
                }
            }

            return PlaceDto.builder()
                .createdAt(restaurant.getCreatedAt())
                .modifiedAt(restaurant.getModifiedAt())
                .id(restaurant.getId())
                .name(restaurant.getName())
                .category(restaurant.getCategory())
                .address(restaurant.getAddress())
                .contact(restaurant.getContact())
                .mainImageUrl(restaurant.getMainImageUrl())
                .registrantComment(restaurant.getRegistrantComment())
                .isApproved(restaurant.getIsApproved())
                .additionalInfo(restaurant.getAdditionalInfo())
                .position(
                    PositionDto.builder()
                        .longitude(restaurant.getLongitude())
                        .latitude(restaurant.getLatitude())
                        .build()
                )
                .openingHour(
                    OpeningHourDto.builder()
                        .openingHourSun(restaurant.getOpeningHourSun())
                        .openingHourMon(restaurant.getOpeningHourMon())
                        .openingHourTue(restaurant.getOpeningHourTue())
                        .openingHourWed(restaurant.getOpeningHourWed())
                        .openingHourThu(restaurant.getOpeningHourThu())
                        .openingHourFri(restaurant.getOpeningHourFri())
                        .openingHourSat(restaurant.getOpeningHourSat())
                        .build()
                )
                .subwayStationList(subwayStationDtoList)
                .build();
        }

        public static PlaceDto detailedFrom(Restaurant restaurant) {
            List<SubwayStationPlace> subwayStationPlaceList = restaurant.getSubwayStationPlaceList();
            List<SubwayStationResponseDto> subwayStationDtoList = new ArrayList<>();
            for (SubwayStationPlace subwayStationPlace : subwayStationPlaceList) {
                SubwayStation subwayStation = subwayStationPlace.getSubwayStation();
                subwayStationDtoList.add(
                    SubwayStationResponseDto.builder()
                        .id(subwayStation.getId())
                        .lineNumber(subwayStation.getLineNumber())
                        .stationName(subwayStation.getStationName())
                        .isMain(subwayStationPlace.getIsMain())
                        .build()
                );
            }
            List<MenuDto> menuList = restaurant.getMenuList().stream().map(MenuDto::from).toList();

            return PlaceDto.builder()
                .createdAt(restaurant.getCreatedAt())
                .modifiedAt(restaurant.getModifiedAt())
                .id(restaurant.getId())
                .name(restaurant.getName())
                .category(restaurant.getCategory())
                .address(restaurant.getAddress())
                .contact(restaurant.getContact())
                .mainImageUrl(restaurant.getMainImageUrl())
                .registrantComment(restaurant.getRegistrantComment())
                .isApproved(restaurant.getIsApproved())
                .additionalInfo(restaurant.getAdditionalInfo())
                .position(
                    PositionDto.builder()
                        .longitude(restaurant.getLongitude())
                        .latitude(restaurant.getLatitude())
                        .build()
                )
                .openingHour(
                    OpeningHourDto.builder()
                        .openingHourSun(restaurant.getOpeningHourSun())
                        .openingHourMon(restaurant.getOpeningHourMon())
                        .openingHourTue(restaurant.getOpeningHourTue())
                        .openingHourWed(restaurant.getOpeningHourWed())
                        .openingHourThu(restaurant.getOpeningHourThu())
                        .openingHourFri(restaurant.getOpeningHourFri())
                        .openingHourSat(restaurant.getOpeningHourSat())
                        .build()
                )
                .subwayStationList(subwayStationDtoList)
                .menuList(menuList)
                .build();
        }

    }

    private List<PlaceDto> data;
    private PageDto page;
}
