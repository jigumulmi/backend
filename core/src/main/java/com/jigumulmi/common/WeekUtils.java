package com.jigumulmi.common;

import java.time.LocalDate;
import java.time.temporal.WeekFields;

public class WeekUtils {
    private static final WeekFields WEEK_FIELDS = WeekFields.SUNDAY_START;

    public static int getWeekOfYear(LocalDate date) {
        return date.get(WEEK_FIELDS.weekOfYear());
    }
}
