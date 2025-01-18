package com.jigumulmi.admin.place.dto.request;

import com.jigumulmi.place.dto.BusinessHour;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminCreateTemporaryBusinessHourRequestDto {

    @NotNull
    @FutureOrPresent
    private LocalDate date;
    @NotNull
    private BusinessHour businessHour;
}

