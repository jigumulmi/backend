package com.jigumulmi.place.dto.response;

import com.jigumulmi.place.domain.SubwayStation;
import com.jigumulmi.place.domain.SubwayStationLine;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubwayStationResponseDto {

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SubwayStationLineDto {

        private Long id;
        private String lineNumber;

        public static SubwayStationLineDto from(SubwayStationLine subwayStationLine) {
            return SubwayStationLineDto.builder()
                .id(subwayStationLine.getId())
                .lineNumber(subwayStationLine.getLineNumber())
                .build();
        }
    }

    private Long id;
    private String stationName;
    private Boolean isMain;
    private List<SubwayStationLineDto> subwayStationLineList;

    public static SubwayStationResponseDto from(SubwayStation subwayStation) {
        return SubwayStationResponseDto.builder()
            .id(subwayStation.getId())
            .stationName(subwayStation.getStationName())
            .build();
    }
}
