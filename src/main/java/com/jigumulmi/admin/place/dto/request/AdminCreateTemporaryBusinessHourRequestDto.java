package com.jigumulmi.admin.place.dto.request;

import com.jigumulmi.admin.place.dto.request.AdminUpdateFixedBusinessHourRequestDto.BusinessHour;
import java.time.LocalDate;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AdminCreateTemporaryBusinessHourRequestDto {

    private LocalDate date;
    private BusinessHour businessHour;
}

