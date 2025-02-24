package com.jigumulmi.place;

import com.jigumulmi.place.dto.BusinessHour;
import com.jigumulmi.place.dto.response.PlaceBasicResponseDto.LiveOpeningInfoDto.NextOpeningInfo;
import com.jigumulmi.place.dto.response.SurroundingDateBusinessHour;
import com.jigumulmi.place.vo.CurrentOpeningStatus;
import com.jigumulmi.place.vo.NextOpeningStatus;
import java.time.LocalTime;
import java.util.stream.Stream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OpeningStatusTest {

    static Stream<Arguments> provideBusinessHoursForCurrentStatus() {
        return Stream.of(
            Arguments.of(
                BusinessHour.builder().openTime(LocalTime.of(22, 0)).closeTime(LocalTime.of(2, 0))
                    .isDayOff(false).build(),
                BusinessHour.builder().openTime(LocalTime.of(9, 0)).closeTime(LocalTime.of(18, 0))
                    .isDayOff(false).build(),
                LocalTime.of(1, 30),
                CurrentOpeningStatus.OVERNIGHT_OPEN
            ),
            Arguments.of(
                BusinessHour.builder().openTime(LocalTime.of(2, 0)).closeTime(LocalTime.of(2, 0))
                    .isDayOff(false).build(),
                BusinessHour.builder().openTime(LocalTime.of(9, 0)).closeTime(LocalTime.of(18, 0))
                    .isDayOff(false).build(),
                LocalTime.of(1, 30),
                CurrentOpeningStatus.OVERNIGHT_OPEN
            ),
            Arguments.of(
                BusinessHour.builder().openTime(LocalTime.of(22, 0)).closeTime(LocalTime.of(2, 0))
                    .isDayOff(false).build(),
                BusinessHour.builder().openTime(LocalTime.of(9, 0)).closeTime(LocalTime.of(18, 0))
                    .isDayOff(false).build(),
                LocalTime.of(3, 0),
                CurrentOpeningStatus.BEFORE_OPEN
            ),
            Arguments.of(
                BusinessHour.builder().openTime(LocalTime.of(9, 0)).closeTime(LocalTime.of(18, 0))
                    .isDayOff(false).build(),
                BusinessHour.builder().isDayOff(true).build(),
                LocalTime.of(14, 30),
                CurrentOpeningStatus.DAY_OFF
            ),
            Arguments.of(
                BusinessHour.builder().openTime(LocalTime.of(9, 0)).closeTime(LocalTime.of(18, 0))
                    .isDayOff(false).build(),
                BusinessHour.builder().openTime(LocalTime.of(15, 0)).closeTime(LocalTime.of(22, 0))
                    .isDayOff(false).build(),
                LocalTime.of(14, 30),
                CurrentOpeningStatus.BEFORE_OPEN
            ),
            Arguments.of(
                BusinessHour.builder().openTime(LocalTime.of(9, 0)).closeTime(LocalTime.of(18, 0))
                    .isDayOff(false).build(),
                BusinessHour.builder()
                    .openTime(LocalTime.of(15, 0))
                    .closeTime(LocalTime.of(2, 0))
                    .isDayOff(false).build(),
                LocalTime.of(22, 30),
                CurrentOpeningStatus.OPEN
            ),
            Arguments.of(
                BusinessHour.builder().openTime(LocalTime.of(9, 0)).closeTime(LocalTime.of(18, 0))
                    .isDayOff(false).build(),
                BusinessHour.builder()
                    .openTime(LocalTime.of(10, 0))
                    .closeTime(LocalTime.of(20, 0))
                    .breakStart(LocalTime.of(14, 0))
                    .breakEnd(LocalTime.of(15, 0))
                    .isDayOff(false).build(),
                LocalTime.of(11, 30),
                CurrentOpeningStatus.OPEN
            ),
            Arguments.of(
                BusinessHour.builder().openTime(LocalTime.of(9, 0)).closeTime(LocalTime.of(18, 0))
                    .isDayOff(false).build(),
                BusinessHour.builder()
                    .openTime(LocalTime.of(10, 0))
                    .closeTime(LocalTime.of(20, 0))
                    .breakStart(LocalTime.of(14, 0))
                    .breakEnd(LocalTime.of(15, 0))
                    .isDayOff(false).build(),
                LocalTime.of(14, 30),
                CurrentOpeningStatus.BREAK
            ),
            Arguments.of(
                BusinessHour.builder().openTime(LocalTime.of(9, 0)).closeTime(LocalTime.of(18, 0))
                    .isDayOff(false).build(),
                BusinessHour.builder()
                    .openTime(LocalTime.of(10, 0))
                    .closeTime(LocalTime.of(20, 0))
                    .breakStart(LocalTime.of(14, 0))
                    .breakEnd(LocalTime.of(15, 0))
                    .isDayOff(false).build(),
                LocalTime.of(16, 30),
                CurrentOpeningStatus.OPEN
            ),
            Arguments.of(
                BusinessHour.builder().openTime(LocalTime.of(9, 0)).closeTime(LocalTime.of(18, 0))
                    .isDayOff(false).build(),
                BusinessHour.builder().openTime(LocalTime.of(10, 0)).closeTime(LocalTime.of(14, 0))
                    .isDayOff(false).build(),
                LocalTime.of(14, 30),
                CurrentOpeningStatus.CLOSED
            )
        );
    }

    @ParameterizedTest
    @MethodSource("provideBusinessHoursForCurrentStatus")
    @DisplayName("실시간 현재 영업 정보")
    public void testDetermineCurrentStatus(BusinessHour yesterday, BusinessHour today,
        LocalTime currentTime, CurrentOpeningStatus expectedStatus) {
        // given
        SurroundingDateBusinessHour surroundingDateBusinessHour = SurroundingDateBusinessHour.builder()
            .yesterday(yesterday)
            .today(today)
            .build();

        // when
        CurrentOpeningStatus actualStatus = CurrentOpeningStatus.determineStatus(
            surroundingDateBusinessHour,
            currentTime);

        // then
        Assertions.assertEquals(expectedStatus, actualStatus);

    }

    static Stream<Arguments> provideBusinessHoursForNextStatus() {
        return Stream.of(
            Arguments.of(
                BusinessHour.builder().openTime(LocalTime.of(22, 0)).closeTime(LocalTime.of(2, 0))
                    .isDayOff(false).build(),
                BusinessHour.builder().openTime(LocalTime.of(9, 0)).closeTime(LocalTime.of(18, 0))
                    .isDayOff(false).build(),
                LocalTime.of(1, 30),
                NextOpeningStatus.CLOSED
            ),
            Arguments.of(
                BusinessHour.builder().openTime(LocalTime.of(2, 0)).closeTime(LocalTime.of(2, 0))
                    .isDayOff(false).build(),
                BusinessHour.builder().openTime(LocalTime.of(9, 0)).closeTime(LocalTime.of(18, 0))
                    .isDayOff(false).build(),
                LocalTime.of(1, 30),
                NextOpeningStatus.CLOSED
            ),
            Arguments.of(
                BusinessHour.builder().openTime(LocalTime.of(22, 0)).closeTime(LocalTime.of(2, 0))
                    .isDayOff(false).build(),
                BusinessHour.builder().openTime(LocalTime.of(9, 0)).closeTime(LocalTime.of(18, 0))
                    .isDayOff(false).build(),
                LocalTime.of(3, 0),
                NextOpeningStatus.OPEN
            ),
            Arguments.of(
                BusinessHour.builder().openTime(LocalTime.of(9, 0)).closeTime(LocalTime.of(18, 0))
                    .isDayOff(false).build(),
                BusinessHour.builder().openTime(LocalTime.of(15, 0)).closeTime(LocalTime.of(22, 0))
                    .isDayOff(false).build(),
                LocalTime.of(14, 30),
                NextOpeningStatus.OPEN
            ),
            Arguments.of(
                BusinessHour.builder().openTime(LocalTime.of(9, 0)).closeTime(LocalTime.of(18, 0))
                    .isDayOff(false).build(),
                BusinessHour.builder()
                    .openTime(LocalTime.of(10, 0))
                    .closeTime(LocalTime.of(20, 0))
                    .breakStart(LocalTime.of(14, 0))
                    .breakEnd(LocalTime.of(15, 0))
                    .isDayOff(false).build(),
                LocalTime.of(11, 30),
                NextOpeningStatus.BREAK
            ),
            Arguments.of(
                BusinessHour.builder().openTime(LocalTime.of(9, 0)).closeTime(LocalTime.of(18, 0))
                    .isDayOff(false).build(),
                BusinessHour.builder()
                    .openTime(LocalTime.of(10, 0))
                    .closeTime(LocalTime.of(20, 0))
                    .breakStart(LocalTime.of(14, 0))
                    .breakEnd(LocalTime.of(15, 0))
                    .isDayOff(false).build(),
                LocalTime.of(14, 30),
                NextOpeningStatus.OPEN
            ),
            Arguments.of(
                BusinessHour.builder().openTime(LocalTime.of(9, 0)).closeTime(LocalTime.of(18, 0))
                    .isDayOff(false).build(),
                BusinessHour.builder()
                    .openTime(LocalTime.of(10, 0))
                    .closeTime(LocalTime.of(20, 0))
                    .breakStart(LocalTime.of(14, 0))
                    .breakEnd(LocalTime.of(15, 0))
                    .isDayOff(false).build(),
                LocalTime.of(16, 30),
                NextOpeningStatus.CLOSED
            ),
            Arguments.of(
                BusinessHour.builder().openTime(LocalTime.of(9, 0)).closeTime(LocalTime.of(18, 0))
                    .isDayOff(false).build(),
                BusinessHour.builder()
                    .openTime(LocalTime.of(15, 0))
                    .closeTime(LocalTime.of(2, 0))
                    .breakStart(LocalTime.of(14, 0))
                    .breakEnd(LocalTime.of(15, 0))
                    .isDayOff(false).build(),
                LocalTime.of(22, 30),
                NextOpeningStatus.CLOSED
            )
        );
    }

    @ParameterizedTest
    @MethodSource("provideBusinessHoursForNextStatus")
    @DisplayName("실시간 다음 영업 정보")
    public void testDetermineNextOpeningInfo(BusinessHour yesterday, BusinessHour today,
        LocalTime currentTime, NextOpeningStatus expectedStatus) {
        // given
        SurroundingDateBusinessHour surroundingDateBusinessHour = SurroundingDateBusinessHour.builder()
            .yesterday(yesterday)
            .today(today)
            .build();

        // when
        NextOpeningInfo nextOpeningInfo = NextOpeningStatus.determineNextOpeningInfo(
            surroundingDateBusinessHour, currentTime);

        // then
        Assertions.assertNotNull(nextOpeningInfo);
        NextOpeningStatus actualStatus = nextOpeningInfo.getStatus();
        Assertions.assertEquals(expectedStatus, actualStatus);

    }

}