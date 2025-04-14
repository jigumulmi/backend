package com.jigumulmi.place.dto.request

import com.jigumulmi.place.dto.BusinessHour
import com.jigumulmi.place.dto.validator.ValidBusinessHour
import jakarta.validation.constraints.FutureOrPresent
import jakarta.validation.constraints.NotNull
import java.time.LocalDate

data class AdminCreateTemporaryBusinessHourRequestDto (
    val date: @NotNull @FutureOrPresent LocalDate,

    @ValidBusinessHour
    val businessHour: BusinessHour,
)

