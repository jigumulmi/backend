package com.jigumulmi.place.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.jigumulmi.member.dto.response.MemberBasicResponseDto;
import com.jigumulmi.place.domain.Menu;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@NoArgsConstructor
public class PlaceDetailResponseDto extends PlaceResponseDto {

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
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

    private String address;

    private String contact;

    private List<PlaceDetailResponseDto.MenuDto> menuList;

    private PlaceDetailResponseDto.OpeningHourDto openingHour;

    private String additionalInfo;

    private OverallReviewResponseDto overallReview;

    private List<ReviewImageResponseDto> reviewImageList;

    private Boolean showLikeCount;
    private Long likeCount;

    private MemberBasicResponseDto member;
    @JsonIgnore
    private Boolean isFromAdmin;
}
