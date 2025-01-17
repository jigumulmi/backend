package com.jigumulmi.admin.place.dto.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.jigumulmi.admin.place.dto.validator.ValidBusinessHour;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.time.DayOfWeek;
import java.time.LocalTime;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AdminUpdateFixedBusinessHourRequestDto {

    @Getter
    @NoArgsConstructor
    public static class TimeDto {

        private int hour;
        private int minute;

        private static LocalTime from(TimeDto timeDto) {
            return timeDto != null ? LocalTime.of(timeDto.getHour(), timeDto.getMinute()) : null;
        }
    }

    @Getter
    @NoArgsConstructor
    @ValidBusinessHour
    @Schema(description = "휴무인 경우 time 관련 필드는 모두 null")
    public static class BusinessHour {

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

    @Valid
    @NotNull
    private BusinessHour sunday;
    @Valid
    @NotNull
    private BusinessHour monday;
    @Valid
    @NotNull
    private BusinessHour tuesday;
    @Valid
    @NotNull
    private BusinessHour wednesday;
    @Valid
    @NotNull
    private BusinessHour thursday;
    @Valid
    @NotNull
    private BusinessHour friday;
    @Valid
    @NotNull
    private BusinessHour saturday;

    public BusinessHour getBusinessHour(DayOfWeek dayOfWeek) {
        return switch (dayOfWeek) {
            case DayOfWeek.SUNDAY -> sunday;
            case DayOfWeek.MONDAY -> monday;
            case DayOfWeek.TUESDAY -> tuesday;
            case DayOfWeek.WEDNESDAY -> wednesday;
            case DayOfWeek.THURSDAY -> thursday;
            case DayOfWeek.FRIDAY -> friday;
            case DayOfWeek.SATURDAY -> saturday;
        };
    }

}

