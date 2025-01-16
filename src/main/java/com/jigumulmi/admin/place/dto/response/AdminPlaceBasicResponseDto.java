package com.jigumulmi.admin.place.dto.response;

import com.jigumulmi.place.domain.Place;
import com.jigumulmi.place.dto.response.PlaceCategoryDto;
import com.jigumulmi.place.dto.response.PlaceResponseDto.PositionDto;
import com.jigumulmi.place.dto.response.SubwayStationResponseDto;
import com.jigumulmi.place.vo.District;
import com.jigumulmi.place.vo.Region;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AdminPlaceBasicResponseDto {

    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
    private Long id;
    private String name;
    private PositionDto position;
    private List<PlaceCategoryDto> categoryList;
    private Region region;
    private District district;
    private String address;
    private String contact;
    private List<SubwayStationResponseDto> subwayStationList;
    private String registrantComment;
    private Boolean isApproved;
    private String kakaoPlaceId;
    private String placeUrl;
    private String additionalInfo;

    public static AdminPlaceBasicResponseDto from(Place place) {
        List<PlaceCategoryDto> categoryList = place.getCategoryMappingList().stream()
            .map(PlaceCategoryDto::fromPlaceCategoryMapping).toList();

        List<SubwayStationResponseDto> subwayStationList = place.getSubwayStationPlaceList()
            .stream()
            .map(subwayStationPlace -> SubwayStationResponseDto.builder()
                .id(subwayStationPlace.getSubwayStation().getId())
                .stationName(subwayStationPlace.getSubwayStation().getStationName())
                .isMain(subwayStationPlace.getIsMain())
                .build())
            .collect(Collectors.toList());

        return AdminPlaceBasicResponseDto.builder()
            .createdAt(place.getCreatedAt())
            .modifiedAt(place.getModifiedAt())
            .id(place.getId())
            .name(place.getName())
            .categoryList(categoryList)
            .region(place.getRegion())
            .district(place.getDistrict())
            .address(place.getAddress())
            .contact(place.getContact())
            .registrantComment(place.getRegistrantComment())
            .isApproved(place.getIsApproved())
            .additionalInfo(place.getAdditionalInfo())
            .position(
                PositionDto.builder()
                    .longitude(place.getLongitude())
                    .latitude(place.getLatitude())
                    .build()
            )
            .subwayStationList(subwayStationList)
            .kakaoPlaceId(place.getKakaoPlaceId())
            .placeUrl(place.getPlaceUrl())
            .build();
    }
}
