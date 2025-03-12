package com.jigumulmi.place.dto.request;

import com.jigumulmi.place.dto.BusinessHour;
import com.jigumulmi.place.dto.validator.ValidBusinessHour;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@NoArgsConstructor
@SuperBuilder
public class AdminCreateTemporaryBusinessHourRequestDto {

    @NotNull
    @FutureOrPresent
    private LocalDate date;
    @ValidBusinessHour
    private BusinessHour businessHour;
}

