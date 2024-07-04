package com.jigumulmi.place.vo;

import static com.jigumulmi.place.domain.QPlace.place;

import com.jigumulmi.place.dto.response.PlaceResponseDto.SurroundingDateOpeningHour;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.dsl.StringExpression;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CurrentOpeningInfo {
    HOLIDAY("오늘 휴무"),
    BEFORE_OPENING("영업 전"),
    OPENING_SOON("곧 영업 시작"),
    OPEN_NOW("영업 중"),
    CLOSING_SOON("곧 영업 종료"),
    CLOSED_NOW("영업 종료"),
    ;

    private final String response;

    private static final Long SOON_STANDARD = 30L;

    private static boolean isBeforeOpening(LocalTime currentTime, LocalTime openTime) {
        if (openTime == null) {
            return false;
        }

        LocalTime soonBeforeOpening = openTime.minusMinutes(SOON_STANDARD);
        return currentTime.isBefore(soonBeforeOpening);
    }

    private static boolean isOpeningSoon(LocalTime currentTime, LocalTime openTime) {
        if (openTime == null) {
            return false;
        }

        LocalTime soonBeforeOpening = openTime.minusMinutes(SOON_STANDARD);
        return currentTime.isAfter(soonBeforeOpening) && currentTime.isBefore(openTime);
    }

    private static boolean isClosingSoon(LocalTime currentTime, LocalTime closeTime) {
        if (closeTime == null) {
            return false;
        }

        LocalTime soonBeforeClosing = closeTime.minusMinutes(SOON_STANDARD);
        return currentTime.isAfter(soonBeforeClosing) && currentTime.isBefore(closeTime);
    }

    private static boolean isOpen(LocalTime currentTime, LocalTime openTime, LocalTime closeTime) {
        if (openTime == null || closeTime == null) {
            return false;
        }

        if (openTime.isBefore(closeTime)) {
            return !currentTime.isBefore(openTime) && currentTime.isBefore(closeTime);
        } else { // 종료 시간이 다음날 또는 24시간 영업
            return currentTime.isAfter(openTime) || !currentTime.isAfter(closeTime);
        }
    }


    public static String getCurrentOpeningInfo(
        SurroundingDateOpeningHour surroundingDateOpeningHour) {
        String HOLIDAY_IN_DB = "정기휴무";

        String yesterdayOpeningHour = surroundingDateOpeningHour.getYesterday();
        String todayOpeningHour = surroundingDateOpeningHour.getToday();
        String tomorrowOpeningHour = surroundingDateOpeningHour.getTomorrow();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        LocalTime now = LocalTime.now(ZoneId.of("Asia/Seoul"));

        LocalTime yesterdayOpenTime = null;
        LocalTime yesterdayCloseTime = null;
        if (!Objects.equals(yesterdayOpeningHour, HOLIDAY_IN_DB)) {
            String[] splitYesterdayOpeningHour = yesterdayOpeningHour.split(" - ");
            yesterdayOpenTime = LocalTime.parse(splitYesterdayOpeningHour[0], formatter);
            yesterdayCloseTime = LocalTime.parse(splitYesterdayOpeningHour[1], formatter);
        }

        LocalTime todayOpenTime = null;
        LocalTime todayCloseTime = null;
        if (!Objects.equals(todayOpeningHour, HOLIDAY_IN_DB)) {
            String[] splitTodayOpeningHour = todayOpeningHour.split(" - ");
            todayOpenTime = LocalTime.parse(splitTodayOpeningHour[0], formatter);
            todayCloseTime = LocalTime.parse(splitTodayOpeningHour[1], formatter);
        }

        LocalTime tomorrowOpenTime = null;
        LocalTime tomorrowCloseTime = null;
        if (!Objects.equals(tomorrowOpeningHour, HOLIDAY_IN_DB)) {
            String[] splitTomorrowOpeningHour = tomorrowOpeningHour.split(" - ");
            tomorrowOpenTime = LocalTime.parse(splitTomorrowOpeningHour[0], formatter);
            tomorrowCloseTime = LocalTime.parse(splitTomorrowOpeningHour[1], formatter);
        }

        boolean isOpenYesterday = isOpen(now, yesterdayOpenTime, yesterdayCloseTime);
        boolean isOpenToday = isOpen(now, todayOpenTime, todayCloseTime);
        boolean isOpenTomorrow = isOpen(now, tomorrowOpenTime, tomorrowCloseTime);

        boolean isOpeningSoonToday = isOpeningSoon(now, todayOpenTime);
        boolean isClosingSoonToday = isClosingSoon(now, todayCloseTime);
        boolean isBeforeOpeningToday = isBeforeOpening(now, todayOpenTime);

        boolean isOpeningSoonTomorrow = isOpeningSoon(now, tomorrowOpenTime);
        boolean isBeforeOpeningTomorrow = isBeforeOpening(now, tomorrowOpenTime);

        CurrentOpeningInfo info;
        if (isOpenYesterday) {
            if (isClosingSoonToday && !isOpenToday) {
                info = CurrentOpeningInfo.CLOSING_SOON;
            } else {
                info = CurrentOpeningInfo.OPEN_NOW;
            }
        } else if (todayOpeningHour.equals(HOLIDAY_IN_DB)) {
            if (isOpenToday || isOpenTomorrow) {
                info = CurrentOpeningInfo.OPEN_NOW;
            } else {
                info = CurrentOpeningInfo.HOLIDAY;
            }
        } else if (isOpeningSoonToday) {
            info = CurrentOpeningInfo.OPENING_SOON;
        } else if (isBeforeOpeningToday) {
            info = CurrentOpeningInfo.BEFORE_OPENING;
        } else if (isOpenToday) {
            if (isClosingSoonToday) {
                info = CurrentOpeningInfo.CLOSING_SOON;
            } else {
                info = CurrentOpeningInfo.OPEN_NOW;
            }
        } else if (isOpeningSoonTomorrow) {
            info = CurrentOpeningInfo.OPENING_SOON;
        } else if (isBeforeOpeningTomorrow) {
            info = CurrentOpeningInfo.BEFORE_OPENING;
        } else {
            info = CurrentOpeningInfo.CLOSED_NOW;
        }

        return info.response;
    }

    public static Expression<?>[] getSurroundingDateOpeningHourExpressions() {
        LocalDate now = LocalDate.now(ZoneId.of("Asia/Seoul"));
        DayOfWeek todayOfWeek = now.getDayOfWeek();

        if (todayOfWeek == DayOfWeek.MONDAY) {
            return new StringExpression[]{
                place.openingHourSun.as("yesterday"),
                place.openingHourMon.as("today"),
                place.openingHourTue.as("tomorrow")
            };
        } else if (todayOfWeek == DayOfWeek.TUESDAY) {
            return new StringExpression[]{
                place.openingHourMon.as("yesterday"),
                place.openingHourTue.as("today"),
                place.openingHourWed.as("tomorrow")
            };
        } else if (todayOfWeek == DayOfWeek.WEDNESDAY) {
            return new StringExpression[]{
                place.openingHourTue.as("yesterday"),
                place.openingHourWed.as("today"),
                place.openingHourThu.as("tomorrow")
            };
        } else if (todayOfWeek == DayOfWeek.THURSDAY) {
            return new StringExpression[]{
                place.openingHourWed.as("yesterday"),
                place.openingHourThu.as("today"),
                place.openingHourFri.as("tomorrow")
            };
        } else if (todayOfWeek == DayOfWeek.FRIDAY) {
            return new StringExpression[]{
                place.openingHourThu.as("yesterday"),
                place.openingHourFri.as("today"),
                place.openingHourSat.as("tomorrow")
            };
        } else if (todayOfWeek == DayOfWeek.SATURDAY) {
            return new StringExpression[]{
                place.openingHourFri.as("yesterday"),
                place.openingHourSat.as("today"),
                place.openingHourSun.as("tomorrow")
            };
        } else {
            return new StringExpression[]{
                place.openingHourSat.as("yesterday"),
                place.openingHourSun.as("today"),
                place.openingHourMon.as("tomorrow")
            };
        }
    }
}
