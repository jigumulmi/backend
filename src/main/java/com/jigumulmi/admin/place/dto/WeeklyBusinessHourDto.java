package com.jigumulmi.admin.place.dto;

import com.jigumulmi.admin.place.dto.validator.ValidBusinessHour;
import com.jigumulmi.place.dto.BusinessHour;
import com.jigumulmi.place.vo.CurrentOpeningStatus;
import java.time.DayOfWeek;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class WeeklyBusinessHourDto {

    @ValidBusinessHour
    private BusinessHour sunday;
    @ValidBusinessHour
    private BusinessHour monday;
    @ValidBusinessHour
    private BusinessHour tuesday;
    @ValidBusinessHour
    private BusinessHour wednesday;
    @ValidBusinessHour
    private BusinessHour thursday;
    @ValidBusinessHour
    private BusinessHour friday;
    @ValidBusinessHour
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

    public void highlightDayOfWeek(CurrentOpeningStatus currentOpeningStatus, DayOfWeek todayDayOfWeek) {
        if (currentOpeningStatus == CurrentOpeningStatus.OVERNIGHT_OPEN) {
            DayOfWeek yesterdayDayOfWeek = todayDayOfWeek.minus(1);
            BusinessHour businessHour = getBusinessHour(yesterdayDayOfWeek);
            businessHour.setHighlightTrue();
            updateBusinessHour(yesterdayDayOfWeek, businessHour);
        } else {
            BusinessHour businessHour = getBusinessHour(todayDayOfWeek);
            businessHour.setHighlightTrue();
            updateBusinessHour(todayDayOfWeek, businessHour);
        }
    }

}

