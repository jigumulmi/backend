package com.jigumulmi.place.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.jigumulmi.admin.place.dto.validator.ValidBusinessHour;
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
@ValidBusinessHour
@Schema(description = "휴무인 경우 time 관련 필드는 모두 null")
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

    @JsonCreator
    public BusinessHour(TimeDto openTime, TimeDto closeTime, TimeDto breakStart,
        TimeDto breakEnd, Boolean isDayOff) {
        this.openTime = TimeDto.from(openTime);
        this.closeTime = TimeDto.from(closeTime);
        this.breakStart = TimeDto.from(breakStart);
        this.breakEnd = TimeDto.from(breakEnd);
        this.isDayOff = isDayOff == null ? false : isDayOff;
    }
}
