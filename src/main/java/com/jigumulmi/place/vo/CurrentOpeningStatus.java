package com.jigumulmi.place.vo;

import com.fasterxml.jackson.annotation.JsonValue;
import com.jigumulmi.place.dto.BusinessHour;
import com.jigumulmi.place.dto.response.SurroundingDateBusinessHour;
import java.time.LocalTime;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CurrentOpeningStatus {
    DAY_OFF("오늘 휴무"),
    BEFORE_OPEN("영업 전"),
    OVERNIGHT_OPEN ("영업 중"),
    OPEN("영업 중"),
    BREAK("브레이크 타임"),
    CLOSED("영업 종료"),
    ;

    @JsonValue
    private final String title;

    public static CurrentOpeningStatus getLiveOpeningStatus(
        SurroundingDateBusinessHour surroundingDateBusinessHour, LocalTime currentTime) {
        BusinessHour todayBusinessHour = surroundingDateBusinessHour.getToday();
        BusinessHour yesterdayBusinessHour = surroundingDateBusinessHour.getYesterday();

        // 어제의 영업 종료 시간이 오늘까지 이어지는 경우
        if ((yesterdayBusinessHour.getOpenTime() != null
            && yesterdayBusinessHour.getCloseTime() != null) && (
            !yesterdayBusinessHour.getCloseTime().isAfter(yesterdayBusinessHour.getOpenTime())
                && currentTime.isBefore(yesterdayBusinessHour.getCloseTime()))) {
            return CurrentOpeningStatus.OVERNIGHT_OPEN;
        }

        if (todayBusinessHour.getIsDayOff()) {
            return CurrentOpeningStatus.DAY_OFF;
        }

        if (currentTime.isBefore(todayBusinessHour.getOpenTime())) {
            return CurrentOpeningStatus.BEFORE_OPEN;
        }

        if ((todayBusinessHour.getBreakStart() != null && todayBusinessHour.getBreakEnd() != null)
            && (currentTime.isAfter(todayBusinessHour.getBreakStart()) && currentTime.isBefore(
            todayBusinessHour.getBreakEnd()))) {
            return CurrentOpeningStatus.BREAK;
        }

        if (currentTime.isAfter(todayBusinessHour.getOpenTime()) && currentTime.isBefore(
            todayBusinessHour.getCloseTime())) {
            return CurrentOpeningStatus.OPEN;
        }

        return CurrentOpeningStatus.CLOSED;
    }
}
