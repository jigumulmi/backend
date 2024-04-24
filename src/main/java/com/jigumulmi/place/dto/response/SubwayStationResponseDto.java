package com.jigumulmi.place.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SubwayStationResponseDto {

    private Long id;
    private String stationName;
    private String lineNumber;
}
