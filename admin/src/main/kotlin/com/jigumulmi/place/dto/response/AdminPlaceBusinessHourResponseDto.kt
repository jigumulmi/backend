package com.jigumulmi.place.dto.response

import com.jigumulmi.place.dto.BusinessHour
import com.jigumulmi.place.dto.WeeklyBusinessHourDto
import com.jigumulmi.place.dto.validator.ValidBusinessHour
import jakarta.validation.constraints.FutureOrPresent
import jakarta.validation.constraints.NotNull
import java.time.LocalDate

data class AdminPlaceBusinessHourResponseDto (
    val fixedBusinessHour: WeeklyBusinessHourDto,
    val temporaryBusinessHour: List<TemporaryBusinessHourDto>
) {

    data class TemporaryBusinessHourDto (
        val id: Long,
        val date: @NotNull @FutureOrPresent LocalDate,

        @ValidBusinessHour
        val businessHour: BusinessHour,
    )

    companion object {
        fun from(
            fixedBusinessHour: WeeklyBusinessHourDto,
            temporaryBusinessHourList: List<TemporaryBusinessHourDto>
        ): AdminPlaceBusinessHourResponseDto {
            return AdminPlaceBusinessHourResponseDto(
                fixedBusinessHour = fixedBusinessHour,
                temporaryBusinessHour = temporaryBusinessHourList
            )
        }
    }
}
