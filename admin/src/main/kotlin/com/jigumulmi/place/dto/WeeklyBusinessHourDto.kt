package com.jigumulmi.place.dto

import com.jigumulmi.place.dto.validator.ValidBusinessHour
import java.time.DayOfWeek

data class WeeklyBusinessHourDto(
    @ValidBusinessHour
    var sunday: BusinessHour,

    @ValidBusinessHour
    var monday: BusinessHour,

    @ValidBusinessHour
    var tuesday: BusinessHour,

    @ValidBusinessHour
    var wednesday: BusinessHour,

    @ValidBusinessHour
    var thursday: BusinessHour,

    @ValidBusinessHour
    var friday: BusinessHour,

    @ValidBusinessHour
    var saturday: BusinessHour,
) {

    constructor() : this(
        BusinessHour(),
        BusinessHour(),
        BusinessHour(),
        BusinessHour(),
        BusinessHour(),
        BusinessHour(),
        BusinessHour()
    )

    fun getBusinessHour(dayOfWeek: DayOfWeek): BusinessHour {
        return when (dayOfWeek) {
            DayOfWeek.SUNDAY -> sunday
            DayOfWeek.MONDAY -> monday
            DayOfWeek.TUESDAY -> tuesday
            DayOfWeek.WEDNESDAY -> wednesday
            DayOfWeek.THURSDAY -> thursday
            DayOfWeek.FRIDAY -> friday
            DayOfWeek.SATURDAY -> saturday
        }
    }

    fun updateBusinessHour(dayOfWeek: DayOfWeek, businessHour: BusinessHour) {
        when (dayOfWeek) {
            DayOfWeek.SUNDAY -> this.sunday = businessHour
            DayOfWeek.MONDAY -> this.monday = businessHour
            DayOfWeek.TUESDAY -> this.tuesday = businessHour
            DayOfWeek.WEDNESDAY -> this.wednesday = businessHour
            DayOfWeek.THURSDAY -> this.thursday = businessHour
            DayOfWeek.FRIDAY -> this.friday = businessHour
            DayOfWeek.SATURDAY -> this.saturday = businessHour
        }
    }
}

