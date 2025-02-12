package com.jigumulmi.place.dto.repository;

import com.jigumulmi.place.dto.BusinessHour;
import java.time.DayOfWeek;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class BusinessHourQueryDto {

    private Long placeId;
    private DayOfWeek dayOfWeek;
    private BusinessHour fixedBusinessHour;
    private BusinessHour temporaryBusinessHour;
}
