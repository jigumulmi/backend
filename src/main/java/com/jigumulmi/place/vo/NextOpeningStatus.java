package com.jigumulmi.place.vo;

import com.fasterxml.jackson.annotation.JsonValue;
import com.jigumulmi.place.dto.BusinessHour;
import com.jigumulmi.place.dto.response.PlaceBasicResponseDto.LiveOpeningInfoDto.NextOpeningInfo;
import com.jigumulmi.place.dto.response.PlaceBasicResponseDto.LiveOpeningInfoDto.NextOpeningInfo.NextOpeningInfoBuilder;
import com.jigumulmi.place.dto.response.SurroundingDateBusinessHour;
import java.time.LocalTime;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum NextOpeningStatus {
    OPEN("영업 시작"),
    BREAK("브레이크 타임"),
    CLOSED("영업 종료"),
    ;

    @JsonValue
    private final String title;

    public static NextOpeningInfo getNextOpeningInfo(
        SurroundingDateBusinessHour surroundingDateBusinessHour, LocalTime currentTime) {
        BusinessHour todayBusinessHour = surroundingDateBusinessHour.getToday();
        BusinessHour yesterdayBusinessHour = surroundingDateBusinessHour.getYesterday();

        NextOpeningInfoBuilder nextOpeningInfoBuilder = NextOpeningInfo.builder();

        // 어제의 영업 종료 시간이 오늘까지 이어지는 경우
        if ((yesterdayBusinessHour.getOpenTime() != null
            && yesterdayBusinessHour.getCloseTime() != null) && (
            !yesterdayBusinessHour.getCloseTime().isAfter(yesterdayBusinessHour.getOpenTime())
                && currentTime.isBefore(yesterdayBusinessHour.getCloseTime()))) {
            return nextOpeningInfoBuilder
                .status(NextOpeningStatus.CLOSED)
                .at(yesterdayBusinessHour.getCloseTime())
                .build();
        }

        if (todayBusinessHour.getIsDayOff()) {
            return null;
        }

        if (currentTime.isAfter(todayBusinessHour.getCloseTime())) {
            return null;
        }

        if (currentTime.isBefore(todayBusinessHour.getOpenTime())) {
            return nextOpeningInfoBuilder
                .status(NextOpeningStatus.OPEN)
                .at(todayBusinessHour.getOpenTime())
                .build();
        }

        if (todayBusinessHour.getBreakStart() == null && todayBusinessHour.getBreakEnd() == null) {
            return nextOpeningInfoBuilder
                .status(NextOpeningStatus.CLOSED)
                .at(todayBusinessHour.getCloseTime())
                .build();
        }

        if (currentTime.isBefore(todayBusinessHour.getBreakStart())) {
            return nextOpeningInfoBuilder
                .status(NextOpeningStatus.BREAK)
                .at(todayBusinessHour.getBreakStart())
                .build();
        }

        if (currentTime.isBefore(todayBusinessHour.getBreakEnd())) {
            return nextOpeningInfoBuilder
                .status(NextOpeningStatus.OPEN)
                .at(todayBusinessHour.getOpenTime())
                .build();
        }

        if (currentTime.isBefore(todayBusinessHour.getCloseTime())) {
            return nextOpeningInfoBuilder
                .status(NextOpeningStatus.CLOSED)
                .at(todayBusinessHour.getCloseTime())
                .build();
        }

        return null;
    }

}
