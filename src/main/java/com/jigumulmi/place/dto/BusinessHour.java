package com.jigumulmi.place.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "휴무인 경우 time 관련 필드는 모두 미포함 혹은 null")
public class BusinessHour {

    @Schema(implementation = TimeDto.class)
    private LocalTime openTime;
    @Schema(implementation = TimeDto.class)
    private LocalTime closeTime;
    @Schema(implementation = TimeDto.class)
    private LocalTime breakStart;
    @Schema(implementation = TimeDto.class)
    private LocalTime breakEnd;
    @Schema(defaultValue = "false")
    private Boolean isDayOff = false;
}
