package com.jigumulmi.place.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubwayStationResponseDto {

    private Long id;
    private String stationName;
    private String lineNumber;
}
