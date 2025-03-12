package com.jigumulmi.place.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import com.jigumulmi.place.domain.FixedBusinessHour;
import com.jigumulmi.place.domain.TemporaryBusinessHour;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import java.time.DayOfWeek;
import java.time.LocalDate;
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

    @Schema(description = "항상 오늘 기준", implementation = TimeDto.class, name = "openTime")
    private LocalTime openTime;
    @Schema(description = "익일로 이어질 수 있음", implementation = TimeDto.class, name = "closeTime")
    private LocalTime closeTime;
    @Schema(description = "항상 오늘 기준", implementation = TimeDto.class, name = "breakStart")
    private LocalTime breakStart;
    @Schema(description = "항상 오늘 기준", implementation = TimeDto.class, name = "breakEnd")
    private LocalTime breakEnd;
    @Schema(requiredMode = RequiredMode.REQUIRED)
    private Boolean isDayOff;
    @Schema(title = "요일")
    @JsonProperty(access = Access.READ_ONLY)
    private DayOfWeek dayOfWeek;
    @Schema(title = "변동 영업일", description = "값이 존재하는 경우 표기")
    @JsonProperty(access = Access.READ_ONLY)
    private LocalDate temporaryDate;

    public static BusinessHour fromFixedBusinessHour(FixedBusinessHour fixedBusinessHour) {
        return builder()
            .openTime(fixedBusinessHour.getOpenTime())
            .closeTime(fixedBusinessHour.getCloseTime())
            .breakStart(fixedBusinessHour.getBreakStart())
            .breakEnd(fixedBusinessHour.getBreakEnd())
            .isDayOff(fixedBusinessHour.getIsDayOff())
            .dayOfWeek(fixedBusinessHour.getDayOfWeek())
            .build();
    }

    public static BusinessHour fromTemporaryBusinessHour(
        TemporaryBusinessHour temporaryBusinessHour) {
        if (temporaryBusinessHour == null) {
            return builder().build();
        }

        return builder()
            .openTime(temporaryBusinessHour.getOpenTime())
            .closeTime(temporaryBusinessHour.getCloseTime())
            .breakStart(temporaryBusinessHour.getBreakStart())
            .breakEnd(temporaryBusinessHour.getBreakEnd())
            .isDayOff(temporaryBusinessHour.getIsDayOff())
            .dayOfWeek(temporaryBusinessHour.getDayOfWeek())
            .temporaryDate(temporaryBusinessHour.getDate())
            .build();
    }
}
