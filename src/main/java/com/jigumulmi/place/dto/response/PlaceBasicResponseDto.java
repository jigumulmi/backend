package com.jigumulmi.place.dto.response;

import com.jigumulmi.admin.place.dto.WeeklyBusinessHourDto;
import com.jigumulmi.place.dto.ImageDto;
import com.jigumulmi.place.dto.TimeDto;
import com.jigumulmi.place.vo.CurrentOpeningStatus;
import com.jigumulmi.place.vo.NextOpeningStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlaceBasicResponseDto {

    @Getter
    @Builder
    public static class LiveOpeningInfoDto {

        @Getter
        @Builder
        public static class NextOpeningInfo {

            private NextOpeningStatus status;
            @Schema(implementation = TimeDto.class)
            private LocalTime at;
        }

        private CurrentOpeningStatus currentOpeningStatus;
        @Schema(description = "현재 상태가 휴무 혹은 영업 종료인 경우 null")
        private NextOpeningInfo nextOpeningInfo;
        private WeeklyBusinessHourDto weeklyBusinessHour;

    }

    private Long id;
    private String name;
    private String address;
    private String contact;
    private String additionalInfo;
    private SubwayStationResponseDto subwayStation;

    @Setter
    private List<ImageDto> imageList;

    @Setter
    private List<PlaceCategoryDto> categoryList;

    @Setter
    private LiveOpeningInfoDto liveOpeningInfo;
}
