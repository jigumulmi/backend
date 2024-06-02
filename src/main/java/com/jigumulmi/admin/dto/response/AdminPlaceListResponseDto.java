package com.jigumulmi.admin.dto.response;

import com.jigumulmi.place.domain.Place;
import com.jigumulmi.place.domain.SubwayStation;
import com.jigumulmi.place.domain.SubwayStationPlace;
import com.jigumulmi.place.dto.response.PlaceDetailResponseDto;
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
    public static class PlaceDto extends PlaceDetailResponseDto {

        private LocalDateTime createdAt;
        private LocalDateTime modifiedAt;
        private String registrantComment;
        private Boolean isApproved;

        public static PlaceDto from(Place place) {
            List<SubwayStationPlace> subwayStationPlaceList = place.getSubwayStationPlaceList();
            List<SubwayStationResponseDto> subwayStationDtoList = new ArrayList<>();
            for (SubwayStationPlace subwayStationPlace : subwayStationPlaceList) {
                if (subwayStationPlace.getIsMain()) {
                    SubwayStation subwayStation = subwayStationPlace.getSubwayStation();
                    subwayStationDtoList.add(
                        SubwayStationResponseDto.builder()
                            .id(subwayStation.getId())
                            .stationName(subwayStation.getStationName())
                            .isMain(subwayStationPlace.getIsMain())
                            .build()
                    );
                }
            }

            return PlaceDto.builder()
                .createdAt(place.getCreatedAt())
                .modifiedAt(place.getModifiedAt())
                .id(place.getId())
                .name(place.getName())
                .category(place.getCategory())
                .address(place.getAddress())
                .contact(place.getContact())
                .mainImageUrl(place.getMainImageUrl())
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
                .build();
        }

        public static PlaceDto detailedFrom(Place place) {
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

            return PlaceDto.builder()
                .createdAt(place.getCreatedAt())
                .modifiedAt(place.getModifiedAt())
                .id(place.getId())
                .name(place.getName())
                .category(place.getCategory())
                .address(place.getAddress())
                .contact(place.getContact())
                .mainImageUrl(place.getMainImageUrl())
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
                .build();
        }

    }

    private List<PlaceDto> data;
    private PageDto page;
}
