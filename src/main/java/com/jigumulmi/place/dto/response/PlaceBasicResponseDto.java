package com.jigumulmi.place.dto.response;

import com.jigumulmi.admin.place.dto.WeeklyBusinessHourDto;
import com.jigumulmi.place.dto.ImageDto;
import com.jigumulmi.place.dto.PositionDto;
import com.jigumulmi.place.vo.LiveOpeningStatus;
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
    public static class LiveBusinessInfoDto {

        private LiveOpeningStatus liveOpeningStatus;
        // TODO 가장 빠른 미래의 변동사항 관련 필드
        private WeeklyBusinessHourDto weeklyBusinessHour;
        
    }

    private Long id;
    private String name;
    private PositionDto position;
    private String address;
    private String contact;
    private String additionalInfo;
    private SubwayStationResponseDto subwayStation;

    @Setter
    private List<ImageDto> imageList;

    @Setter
    private List<PlaceCategoryDto> categoryList;

    @Setter
    private LiveBusinessInfoDto liveBusinessInfo;
}
