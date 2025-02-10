package com.jigumulmi.admin.place.dto;

import com.jigumulmi.admin.place.dto.validator.ValidBusinessHour;
import com.jigumulmi.place.dto.BusinessHour;
import jakarta.validation.constraints.NotNull;
import java.time.DayOfWeek;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class WeeklyBusinessHourDto {

    @ValidBusinessHour
    @NotNull
    private BusinessHour sunday;
    @ValidBusinessHour
    @NotNull
    private BusinessHour monday;
    @ValidBusinessHour
    @NotNull
    private BusinessHour tuesday;
    @ValidBusinessHour
    @NotNull
    private BusinessHour wednesday;
    @ValidBusinessHour
    @NotNull
    private BusinessHour thursday;
    @ValidBusinessHour
    @NotNull
    private BusinessHour friday;
    @ValidBusinessHour
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

    public void updateBusinessHour(DayOfWeek dayOfWeek, BusinessHour businessHour) {
        switch (dayOfWeek) {
            case SUNDAY -> this.sunday = businessHour;
            case MONDAY -> this.monday = businessHour;
            case TUESDAY -> this.tuesday = businessHour;
            case WEDNESDAY -> this.wednesday = businessHour;
            case THURSDAY -> this.thursday = businessHour;
            case FRIDAY -> this.friday = businessHour;
            case SATURDAY -> this.saturday = businessHour;
        }
    }

}

