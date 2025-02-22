package com.jigumulmi.common;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class WeekUtils {
    private static final WeekFields WEEK_FIELDS = WeekFields.SUNDAY_START;

    public static int getWeekOfYear(LocalDate date) {
        return date.get(WEEK_FIELDS.weekOfYear());
    }

    public static List<DayOfWeek> getWeekStartingFrom(DayOfWeek startDayOfWeek) {
        return IntStream.range(0, 7)
            .mapToObj(startDayOfWeek::plus)
            .collect(Collectors.toList());
    }
}
