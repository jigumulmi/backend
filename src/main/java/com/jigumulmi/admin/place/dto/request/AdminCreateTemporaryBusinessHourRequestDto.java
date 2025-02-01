package com.jigumulmi.admin.place.dto.request;

import com.jigumulmi.admin.place.dto.validator.ValidBusinessHour;
import com.jigumulmi.place.dto.BusinessHour;
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
    @NotNull
    @ValidBusinessHour
    private BusinessHour businessHour;
}

