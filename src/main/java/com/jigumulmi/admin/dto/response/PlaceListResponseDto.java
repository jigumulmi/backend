package com.jigumulmi.admin.dto.response;

import com.jigumulmi.place.domain.Restaurant;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PlaceListResponseDto {

    @Getter
    @Builder
    public static class PlaceDto {

        private LocalDateTime createdAt;
        private Long id;
        private String name;
        private String category;
        private String address;
        private String contact;
        private String openingHourSun;
        private String openingHourMon;
        private String openingHourTue;
        private String openingHourWed;
        private String openingHourThu;
        private String openingHourFri;
        private String openingHourSat;
        private String additionalInfo;
        private String mainImageUrl;
        private Double longitude;
        private Double latitude;
        private String registrantComment;
        private Boolean isApproved;

        public static PlaceDto from(Restaurant restaurant) {
            return PlaceDto.builder()
                .createdAt(restaurant.getCreatedAt())
                .name(restaurant.getName())
                .category(restaurant.getCategory())
                .address(restaurant.getAddress())
                .contact(restaurant.getContact())
                .openingHourSun(restaurant.getOpeningHourSun())
                .openingHourMon(restaurant.getOpeningHourMon())
                .openingHourTue(restaurant.getOpeningHourTue())
                .openingHourWed(restaurant.getOpeningHourWed())
                .openingHourThu(restaurant.getOpeningHourThu())
                .openingHourFri(restaurant.getOpeningHourFri())
                .openingHourSat(restaurant.getOpeningHourSat())
                .mainImageUrl(restaurant.getMainImageUrl())
                .longitude(restaurant.getLongitude())
                .latitude(restaurant.getLatitude())
                .registrantComment(restaurant.getRegistrantComment())
                .isApproved(restaurant.getIsApproved())
                .build();
        }

    }

    private List<PlaceDto> data;
    private Long totalCount;
}
