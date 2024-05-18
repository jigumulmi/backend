package com.jigumulmi.place.dto.response;

import com.jigumulmi.place.domain.Menu;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Getter
@SuperBuilder
public class RestaurantDetailResponseDto extends RestaurantResponseDto {

    @Getter
    @Builder
    public static class MenuDto {

        private Long id;

        private String name;

        public static MenuDto from(Menu menu) {
            return MenuDto.builder()
                .id(menu.getId())
                .name(menu.getName())
                .build();
        }
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OpeningHourDto {

        private String openingHourSun;

        private String openingHourMon;

        private String openingHourTue;

        private String openingHourWed;

        private String openingHourThu;

        private String openingHourFri;

        private String openingHourSat;
    }

    private String category;

    private String address;

    private String contact;

    private List<RestaurantDetailResponseDto.MenuDto> menuList;

    private RestaurantDetailResponseDto.OpeningHourDto openingHour;

    private String additionalInfo;

    private OverallReviewResponseDto overallReview;
}
