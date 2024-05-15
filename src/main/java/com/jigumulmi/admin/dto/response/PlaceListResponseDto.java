package com.jigumulmi.admin.dto.response;

import com.jigumulmi.place.domain.Restaurant;
import com.jigumulmi.place.dto.response.RestaurantDetailResponseDto;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@Builder
public class PlaceListResponseDto {

    @Getter
    @SuperBuilder
    public static class PlaceDto extends RestaurantDetailResponseDto {

        private LocalDateTime createdAt;
        private LocalDateTime modifiedAt;
        private String registrantComment;
        private Boolean isApproved;

        public static PlaceDto from(Restaurant restaurant) {
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
                .build();
        }

    }

    private List<PlaceDto> data;
    private PageDto page;
}
