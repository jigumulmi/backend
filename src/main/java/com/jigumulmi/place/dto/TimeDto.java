package com.jigumulmi.place.dto;

import com.jigumulmi.admin.place.dto.request.AdminCreatedTemporaryBusinessHourRequestDto;
import java.time.LocalTime;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TimeDto {

    private int hour;
    private int minute;

    public static LocalTime from(TimeDto timeDto) {
        return timeDto != null ? LocalTime.of(timeDto.getHour(), timeDto.getMinute()) : null;
    }
}
