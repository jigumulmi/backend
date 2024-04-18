package com.jigumulmi.place.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class GetPlaceListRequestDto {
    private Long subwayStationId;
    private Long placeId;
}
