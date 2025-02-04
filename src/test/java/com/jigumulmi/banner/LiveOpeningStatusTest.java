package com.jigumulmi.banner;

import com.jigumulmi.place.dto.BusinessHour;
import com.jigumulmi.place.dto.response.SurroundingDateBusinessHour;
import com.jigumulmi.place.vo.LiveOpeningStatus;
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
class LiveOpeningStatusTest {

    static Stream<Arguments> provideBusinessHours() {
        return Stream.of(
            // 어제의 영업시간이 자정을 넘기는 경우
            Arguments.of(
                BusinessHour.builder().openTime(LocalTime.of(22, 0)).closeTime(LocalTime.of(2, 0))
                    .build(),
                BusinessHour.builder().openTime(LocalTime.of(9, 0)).closeTime(LocalTime.of(18, 0))
                    .build(),
                LocalTime.of(1, 30),
                LiveOpeningStatus.OPEN
            ),
            Arguments.of(
                BusinessHour.builder().openTime(LocalTime.of(22, 0)).closeTime(LocalTime.of(2, 0))
                    .build(),
                BusinessHour.builder().openTime(LocalTime.of(9, 0)).closeTime(LocalTime.of(18, 0))
                    .build(),
                LocalTime.of(3, 0),
                LiveOpeningStatus.BEFORE_OPEN
            ),
            //
            Arguments.of(
                BusinessHour.builder().openTime(LocalTime.of(9, 0)).closeTime(LocalTime.of(18, 0))
                    .build(),
                BusinessHour.builder().isDayOff(true).build(),
                LocalTime.of(14, 30),
                LiveOpeningStatus.DAY_OFF
            ),
            Arguments.of(
                BusinessHour.builder().openTime(LocalTime.of(9, 0)).closeTime(LocalTime.of(18, 0))
                    .build(),
                BusinessHour.builder().openTime(LocalTime.of(15, 0)).closeTime(LocalTime.of(22, 0))
                    .build(),
                LocalTime.of(14, 30),
                LiveOpeningStatus.BEFORE_OPEN
            ),
            Arguments.of(
                BusinessHour.builder().openTime(LocalTime.of(9, 0)).closeTime(LocalTime.of(18, 0))
                    .build(),
                BusinessHour.builder()
                    .openTime(LocalTime.of(10, 0))
                    .closeTime(LocalTime.of(20, 0))
                    .breakStart(LocalTime.of(14, 0))
                    .breakEnd(LocalTime.of(15, 0))
                    .build(),
                LocalTime.of(11, 30),
                LiveOpeningStatus.OPEN
            ),
            Arguments.of(
                BusinessHour.builder().openTime(LocalTime.of(9, 0)).closeTime(LocalTime.of(18, 0))
                    .build(),
                BusinessHour.builder()
                    .openTime(LocalTime.of(10, 0))
                    .closeTime(LocalTime.of(20, 0))
                    .breakStart(LocalTime.of(14, 0))
                    .breakEnd(LocalTime.of(15, 0))
                    .build(),
                LocalTime.of(14, 30),
                LiveOpeningStatus.BREAK
            ),
            Arguments.of(
                BusinessHour.builder().openTime(LocalTime.of(9, 0)).closeTime(LocalTime.of(18, 0))
                    .build(),
                BusinessHour.builder()
                    .openTime(LocalTime.of(10, 0))
                    .closeTime(LocalTime.of(20, 0))
                    .breakStart(LocalTime.of(14, 0))
                    .breakEnd(LocalTime.of(15, 0))
                    .build(),
                LocalTime.of(16, 30),
                LiveOpeningStatus.OPEN
            ),
            Arguments.of(
                BusinessHour.builder().openTime(LocalTime.of(9, 0)).closeTime(LocalTime.of(18, 0))
                    .build(),
                BusinessHour.builder().openTime(LocalTime.of(10, 0)).closeTime(LocalTime.of(14, 0))
                    .build(),
                LocalTime.of(14, 30),
                LiveOpeningStatus.CLOSED
            )
        );
    }

    @ParameterizedTest
    @MethodSource("provideBusinessHours")
    @DisplayName("실시간 영업 정보")
    public void testGetCurrentOpeningInfo(BusinessHour yesterday, BusinessHour today, LocalTime currentTime,
        LiveOpeningStatus expectedStatus) {
        // given
        SurroundingDateBusinessHour surroundingDateBusinessHour = SurroundingDateBusinessHour.builder()
            .yesterday(yesterday)
            .today(today)
            .build();

        // when
        LiveOpeningStatus actualStatus = LiveOpeningStatus.getCurrentOpeningInfo(surroundingDateBusinessHour,
            currentTime);

        // then
        Assertions.assertEquals(expectedStatus, actualStatus);

    }

}