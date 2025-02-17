package com.jigumulmi.place.dto;

import com.jigumulmi.place.domain.FixedBusinessHour;
import com.jigumulmi.place.domain.TemporaryBusinessHour;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
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
    @Schema(requiredMode = RequiredMode.REQUIRED)
    private Boolean isDayOff;

    public static BusinessHour fromFixedBusinessHour(FixedBusinessHour fixedBusinessHour) {
        return builder()
            .openTime(fixedBusinessHour.getOpenTime())
            .closeTime(fixedBusinessHour.getCloseTime())
            .breakStart(fixedBusinessHour.getBreakStart())
            .breakEnd(fixedBusinessHour.getBreakEnd())
            .isDayOff(fixedBusinessHour.getIsDayOff())
            .build();
    }

    public static BusinessHour fromTemporaryBusinessHour(TemporaryBusinessHour temporaryBusinessHour) {
        return builder()
            .openTime(temporaryBusinessHour.getOpenTime())
            .closeTime(temporaryBusinessHour.getCloseTime())
            .breakStart(temporaryBusinessHour.getBreakStart())
            .breakEnd(temporaryBusinessHour.getBreakEnd())
            .isDayOff(temporaryBusinessHour.getIsDayOff())
            .build();
    }
}
