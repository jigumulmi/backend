package com.jigumulmi.place.vo;

import static com.jigumulmi.place.domain.QPlace.place;

import com.jigumulmi.place.dto.response.PlaceResponseDto.SurroundingDateOpeningHour;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.dsl.StringExpression;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
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
    private static final String CLOSING_DAY = "정기휴무";

    public static String getCurrentOpeningInfo(
        SurroundingDateOpeningHour surroundingDateOpeningHour
    ) {
        LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Seoul"));

        // "오늘 휴뮤" 판단
        if (CLOSING_DAY.equals(surroundingDateOpeningHour.getToday())) {
            if (isOpenNow(surroundingDateOpeningHour.getYesterday(), now)) {
                return CurrentOpeningInfo.OPEN_NOW.getResponse();
            }
            return CurrentOpeningInfo.HOLIDAY.getResponse();
        }

        return getOpeningStatus(surroundingDateOpeningHour.getToday(), now);
    }

    private static boolean isOpenNow(String openingHours, LocalDateTime now) {
        if (CLOSING_DAY.equals(openingHours)) {
            return false;
        }
        String[] times = openingHours.split(" - ");
        LocalTime openTime = LocalTime.parse(times[0]);
        LocalTime closeTime = LocalTime.parse(times[1]);
        LocalTime currentTime = now.toLocalTime();

        if (closeTime.isBefore(openTime)) {
            return currentTime.isAfter(openTime) || currentTime.isBefore(closeTime);
        } else {
            return currentTime.isAfter(openTime) && currentTime.isBefore(closeTime);
        }
    }

    private static String getOpeningStatus(String openingHours, LocalDateTime now) {
        String[] times = openingHours.split(" - ");
        LocalTime openTime = LocalTime.parse(times[0]);
        LocalTime closeTime = LocalTime.parse(times[1]);
        LocalTime currentTime = now.toLocalTime();

        if (closeTime.isBefore(openTime)) { // 오늘 영업시간이 내일까지
            if (currentTime.isBefore(closeTime)) {
                return CurrentOpeningInfo.OPEN_NOW.getResponse();
            } else if (currentTime.isBefore(
                openTime.minusMinutes(CurrentOpeningInfo.SOON_STANDARD))) {
                return CurrentOpeningInfo.BEFORE_OPENING.getResponse();
            } else if (currentTime.isBefore(openTime)) {
                return CurrentOpeningInfo.OPENING_SOON.getResponse();
            }
        } else {
            if (currentTime.isBefore(openTime.minusMinutes(CurrentOpeningInfo.SOON_STANDARD))) {
                return CurrentOpeningInfo.BEFORE_OPENING.getResponse();
            } else if (currentTime.isBefore(openTime)) {
                return CurrentOpeningInfo.OPENING_SOON.getResponse();
            } else if (currentTime.isBefore(
                closeTime.minusMinutes(CurrentOpeningInfo.SOON_STANDARD))) {
                return CurrentOpeningInfo.OPEN_NOW.getResponse();
            } else if (currentTime.isBefore(closeTime)) {
                return CurrentOpeningInfo.CLOSING_SOON.getResponse();
            }
        }

        return CurrentOpeningInfo.CLOSED_NOW.getResponse();
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
