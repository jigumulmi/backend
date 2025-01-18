package com.jigumulmi.place.dto.time;

import java.time.LocalTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TimeDto {

    private int hour;
    private int minute;

    public static LocalTime toLocalTime(TimeDto timeDto) {
        if (timeDto == null) {
            return null;
        }
        return LocalTime.of(timeDto.getHour(), timeDto.getMinute());
    }

    public static TimeDto from(LocalTime localTime) {
        if (localTime == null) {
            return null;
        }
        return new TimeDto(localTime.getHour(), localTime.getMinute());
    }
}
