package com.jigumulmi.admin.place.dto.request;

import com.jigumulmi.place.dto.BusinessHour;
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

    private LocalDate date;
    private BusinessHour businessHour;
}

