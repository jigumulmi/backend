package com.jigumulmi.banner;

import com.jigumulmi.admin.banner.BannerPlaceMappingRepository;
import com.jigumulmi.banner.domain.Banner;
import com.jigumulmi.banner.domain.BannerPlaceMapping;
import com.jigumulmi.banner.repository.BannerRepository;
import com.jigumulmi.banner.repository.CustomBannerRepository;
import com.jigumulmi.common.annotation.RepositoryTest;
import com.jigumulmi.place.domain.FixedBusinessHour;
import com.jigumulmi.place.domain.Place;
import com.jigumulmi.place.domain.TemporaryBusinessHour;
import com.jigumulmi.place.dto.response.SurroundingDateBusinessHour;
import com.jigumulmi.place.repository.FixedBusinessHourRepository;
import com.jigumulmi.place.repository.PlaceRepository;
import com.jigumulmi.place.repository.TemporaryBusinessHourRepository;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.util.ReflectionTestUtils;

@RepositoryTest
class CustomBannerRepositoryTest {

    private static final LocalDate today = LocalDate.now();

    @Autowired
    private CustomBannerRepository customBannerRepository;

    @Autowired
    private BannerRepository bannerRepository;
    @Autowired
    private PlaceRepository placeRepository;
    @Autowired
    private BannerPlaceMappingRepository bannerPlaceMappingRepository;
    @Autowired
    private FixedBusinessHourRepository fixedBusinessHourRepository;
    @Autowired
    private TemporaryBusinessHourRepository temporaryBusinessHourRepository;

    @Test
    @DisplayName("할당된 장소 목록 조회")
    public void testGetAllMappedPlaceByBannerId() {
        // given
        Banner banner = Banner.builder().build();
        bannerRepository.save(banner);

        Place place1 = Place.builder().build();
        Place place2 = Place.builder().build();
        placeRepository.saveAll(List.of(place1, place2));

        BannerPlaceMapping bannerPlaceMapping1 = BannerPlaceMapping.builder().banner(banner)
            .place(place1)
            .build();
        BannerPlaceMapping bannerPlaceMapping2 = BannerPlaceMapping.builder().banner(banner)
            .place(place2)
            .build();
        bannerPlaceMappingRepository.saveAll(List.of(bannerPlaceMapping1, bannerPlaceMapping2));

        // when
        PageRequest pageRequest = PageRequest.ofSize(1);
        Page<Place> placePage = customBannerRepository.getAllMappedPlaceByBannerId(
            pageRequest, banner.getId());

        // then
        Assertions.assertEquals(placePage.getTotalPages(), 2L);

    }

    static Stream<Arguments> provideTemporaryBusinessHours() {
        LocalDate yesterday = today.minusDays(1);

        DayOfWeek todayDayOfWeek = today.getDayOfWeek();
        DayOfWeek yesterdayDayOfWeek = yesterday.getDayOfWeek();

        return Stream.of(
            Arguments.of(
                TemporaryBusinessHour.builder()
                    .date(yesterday)
                    .dayOfWeek(yesterdayDayOfWeek)
                    .isDayOff(true)
                    .build(),
                TemporaryBusinessHour.builder()
                    .date(today)
                    .dayOfWeek(todayDayOfWeek)
                    .isDayOff(true)
                    .build()
            ),
            Arguments.of(
                TemporaryBusinessHour.builder()
                    .date(yesterday)
                    .dayOfWeek(yesterdayDayOfWeek)
                    .isDayOff(true)
                    .build(),
                null
            ),
            Arguments.of(
                null,
                TemporaryBusinessHour.builder()
                    .date(today)
                    .dayOfWeek(todayDayOfWeek)
                    .isDayOff(true)
                    .build()
            ),
            Arguments.of(
                null, null
            )
        );
    }

    @ParameterizedTest
    @MethodSource("provideTemporaryBusinessHours")
    @DisplayName("어제, 오늘 영업시간 정보 조회")
    public void testGetSurroundingBusinessHourByPlaceIdIn(
        TemporaryBusinessHour yesterdayTempBizHour,
        TemporaryBusinessHour todayTempBizHour) {
        // given
        Place place = Place.builder().build();
        placeRepository.save(place);

        boolean fixedBizHourIsDayOff = false;
        ArrayList<FixedBusinessHour> fixedBusinessHourList = new ArrayList<>();
        for (DayOfWeek dayOfWeek : DayOfWeek.values()) {
            fixedBusinessHourList.add(
                FixedBusinessHour.builder()
                    .place(place)
                    .dayOfWeek(dayOfWeek)
                    .openTime(LocalTime.of(10, 0))
                    .closeTime(LocalTime.of(20, 0))
                    .isDayOff(fixedBizHourIsDayOff)
                    .build()
            );
        }
        fixedBusinessHourRepository.saveAll(fixedBusinessHourList);

        List<TemporaryBusinessHour> tempBizHourList = Stream.of(yesterdayTempBizHour,
                todayTempBizHour).filter(Objects::nonNull).toList();
        for (TemporaryBusinessHour tempBizHour : tempBizHourList) {
            ReflectionTestUtils.setField(tempBizHour, "place", place);
        }
        temporaryBusinessHourRepository.saveAll(tempBizHourList);

        // when
        Map<Long, SurroundingDateBusinessHour> surroundingBizHourMap = customBannerRepository.getSurroundingBusinessHourByPlaceIdIn(
            List.of(place.getId()), today);

        // then 변동 영업시간으로 바뀌었는지 확인
        SurroundingDateBusinessHour surroundingDateBusinessHour = surroundingBizHourMap.get(
            place.getId());
        if (yesterdayTempBizHour != null) {
            Assertions.assertEquals(surroundingDateBusinessHour.getYesterday().getIsDayOff(),
                yesterdayTempBizHour.getIsDayOff());
        } else {
            Assertions.assertEquals(surroundingDateBusinessHour.getYesterday().getIsDayOff(),
                fixedBizHourIsDayOff);
        }

        if (todayTempBizHour != null) {
            Assertions.assertEquals(surroundingDateBusinessHour.getToday().getIsDayOff(),
                todayTempBizHour.getIsDayOff());
        } else {
            Assertions.assertEquals(surroundingDateBusinessHour.getToday().getIsDayOff(),
                fixedBizHourIsDayOff);
        }

    }

}