package com.jigumulmi.place.vo;

import com.querydsl.core.types.dsl.BooleanTemplate;
import com.querydsl.core.types.dsl.Expressions;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Getter
@AllArgsConstructor
public enum CurrentOpeningInfo {
    OPEN_NOW("영업 중"),
    OPENING_SOON("곧 영업 시작"),
    CLOSED_NOW("영업 종료"),
    CLOSING_SOON("곧 영업 종료"),
    ;

    private final String response;

    private static final Long SOON_STANDARD = 1L;

    private static boolean isOpen(LocalTime currentTime, LocalTime openTime, LocalTime closeTime) {
        if (openTime.isBefore(closeTime)) {
            return !currentTime.isBefore(openTime) && currentTime.isBefore(closeTime);
        } else { // 종료 시간이 다음날
            return currentTime.isAfter(openTime) || !currentTime.isAfter(closeTime);
        }
    }

    private static boolean isOpeningSoon(LocalTime currentTime, LocalTime openTime) {
        LocalTime soonBeforeOpening = openTime.minusHours(SOON_STANDARD);
        return currentTime.isAfter(soonBeforeOpening) && currentTime.isBefore(openTime);
    }

    private static boolean isClosingSoon(LocalTime currentTime, LocalTime closeTime) {
        LocalTime soonBeforeClosing = closeTime.minusHours(SOON_STANDARD);
        return currentTime.isAfter(soonBeforeClosing) && currentTime.isBefore(closeTime);
    }

    public static String getCurrentOpeningInfo(String todayOpeningHour) {
        String[] splitOpeningHour = todayOpeningHour.split(" - ");
        if (splitOpeningHour.length != 2) { // 정기휴무일
            return CLOSED_NOW.response;
        }

        String openTimeString = splitOpeningHour[0];
        String closeTimeString = splitOpeningHour[1];
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");

        LocalTime openTime = LocalTime.parse(openTimeString, formatter);
        LocalTime closeTime = LocalTime.parse(closeTimeString, formatter);
        LocalTime now = LocalTime.now(ZoneId.of("Asia/Seoul"));

        boolean isOpen = isOpen(now, openTime, closeTime);
        boolean isOpeningSoon = isOpeningSoon(now, openTime);
        boolean isClosingSoon = isClosingSoon(now, closeTime);

        CurrentOpeningInfo info;
        if (isOpeningSoon) {
            info = CurrentOpeningInfo.OPENING_SOON;
        } else if (isClosingSoon) {
            info = CurrentOpeningInfo.CLOSING_SOON;
        } else if (isOpen) {
            info = CurrentOpeningInfo.OPEN_NOW;
        } else {
            info = CurrentOpeningInfo.CLOSED_NOW;
        }

        return info.response;
    }

    public static DayOfWeek getTodayOfWeek() {
        LocalDate now = LocalDate.now(ZoneId.of("Asia/Seoul"));
        return now.getDayOfWeek();
    }

    public static BooleanTemplate isMonday(DayOfWeek dayOfWeek) {
        return Expressions.booleanTemplate(String.valueOf(dayOfWeek == DayOfWeek.MONDAY));
    }

    public static BooleanTemplate isTuesday(DayOfWeek dayOfWeek) {
        return Expressions.booleanTemplate(String.valueOf(dayOfWeek == DayOfWeek.TUESDAY));
    }

    public static BooleanTemplate isWednesday(DayOfWeek dayOfWeek) {
        return Expressions.booleanTemplate(String.valueOf(dayOfWeek == DayOfWeek.WEDNESDAY));
    }

    public static BooleanTemplate isThursday(DayOfWeek dayOfWeek) {
        return Expressions.booleanTemplate(String.valueOf(dayOfWeek == DayOfWeek.THURSDAY));
    }

    public static BooleanTemplate isFriday(DayOfWeek dayOfWeek) {
        return Expressions.booleanTemplate(String.valueOf(dayOfWeek == DayOfWeek.FRIDAY));
    }

    public static BooleanTemplate isSaturday(DayOfWeek dayOfWeek) {
        return Expressions.booleanTemplate(String.valueOf(dayOfWeek == DayOfWeek.SATURDAY));
    }

    public static BooleanTemplate isSunday(DayOfWeek dayOfWeek) {
        return Expressions.booleanTemplate(String.valueOf(dayOfWeek == DayOfWeek.SUNDAY));
    }
}
