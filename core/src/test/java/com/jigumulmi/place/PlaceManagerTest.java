package com.jigumulmi.place;

import static org.mockito.BDDMockito.given;

import com.jigumulmi.place.domain.FixedBusinessHour;
import com.jigumulmi.place.dto.BusinessHour;
import com.jigumulmi.place.dto.response.PlaceBasicResponseDto;
import com.jigumulmi.place.repository.CustomPlaceRepository;
import com.jigumulmi.place.repository.FixedBusinessHourRepository;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PlaceManagerTest {

    @InjectMocks
    private PlaceManager placeManager;

    @Mock
    private CustomPlaceRepository customPlaceRepository;
    @Mock
    private FixedBusinessHourRepository fixedBusinessHourRepository;

    private static Stream<Arguments> getYesterdayCloseTime() {
        return Stream.of(
            Arguments.of(LocalTime.of(21, 0), DayOfWeek.SUNDAY),
            Arguments.of(LocalTime.of(2, 0), DayOfWeek.SATURDAY)
        );
    }

    @ParameterizedTest
    @MethodSource("getYesterdayCloseTime")
    @DisplayName("어제의 영업 종료 시간에 따른 주간 영업 첫 요일 결졍")
    public void testDetermineLiveOpeningInfo(LocalTime closeTime,
        DayOfWeek expectedFirstDayOfWeek) {
        // given
        LocalDate today = LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY));
        LocalTime currentTime = LocalTime.of(1, 0);
        LocalDateTime now = LocalDateTime.of(today, currentTime);  // 일요일 새벽 1시

        long placeId = 1L;
        PlaceBasicResponseDto placeBasicResponseDto = PlaceBasicResponseDto.builder().id(placeId)
            .build();

        List<FixedBusinessHour> fixedBusinessHourList = Stream.of(DayOfWeek.values())
            .map(day -> FixedBusinessHour.builder()
                .dayOfWeek(day)
                .openTime(LocalTime.of(13, 0))
                .closeTime(closeTime)
                .isDayOff(false)
                .build()
            ).toList();
        given(fixedBusinessHourRepository.findAllByPlaceId(placeId)).willReturn(
            fixedBusinessHourList);

        given(customPlaceRepository.getWeeklyTemporaryBusinessHourByPlaceId(placeId,
            today)).willReturn(Collections.emptyMap());

        // when
        PlaceBasicResponseDto finalResponseDto = placeManager.determineLiveOpeningInfo(
            placeBasicResponseDto, now);

        // then
        List<BusinessHour> weeklyBusinessHour = finalResponseDto.getLiveOpeningInfo()
            .getWeeklyBusinessHour();
        Assertions.assertEquals(weeklyBusinessHour.size(), 7);
        DayOfWeek actualFirstDayOfWeek = weeklyBusinessHour.getFirst().getDayOfWeek();
        Assertions.assertEquals(expectedFirstDayOfWeek, actualFirstDayOfWeek);

    }
}