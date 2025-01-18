package com.jigumulmi.admin.place.dto.request;

import com.jigumulmi.place.dto.BusinessHour;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.time.DayOfWeek;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AdminUpdateFixedBusinessHourRequestDto {

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

