package com.jigumulmi.place.dto.response;

import com.jigumulmi.place.domain.SubwayStation;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubwayStationResponseDto {

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode
    public static class SubwayStationLineDto {

        private Long id;
        private String lineNumber;
    }

    private Long id;
    private String stationName;
    private Boolean isMain;
    @Setter
    private List<SubwayStationLineDto> subwayStationLineList;

    public static SubwayStationResponseDto fromMainStation(SubwayStation subwayStation) {
        return SubwayStationResponseDto.builder()
            .id(subwayStation.getId())
            .stationName(subwayStation.getStationName())
            .build();
    }
}
