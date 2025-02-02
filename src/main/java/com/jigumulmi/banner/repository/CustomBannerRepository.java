package com.jigumulmi.banner.repository;

import static com.jigumulmi.banner.domain.QBannerPlaceMapping.bannerPlaceMapping;
import static com.jigumulmi.config.querydsl.Utils.getOrderSpecifier;
import static com.jigumulmi.place.domain.QFixedBusinessHour.fixedBusinessHour;
import static com.jigumulmi.place.domain.QPlace.place;
import static com.jigumulmi.place.domain.QTemporaryBusinessHour.temporaryBusinessHour;

import com.jigumulmi.place.domain.Place;
import com.jigumulmi.place.dto.BusinessHour;
import com.jigumulmi.place.dto.response.SurroundingDateBusinessHour;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CustomBannerRepository {

    private final JPAQueryFactory queryFactory;

    public Page<Place> getAllMappedPlaceByBannerId(Pageable pageable, Long bannerId) {
        List<Place> content = queryFactory
            .selectFrom(place)
            .join(place.bannerPlaceMappingList, bannerPlaceMapping)
            .where(bannerPlaceMapping.banner.id.eq(bannerId))
            .orderBy(getOrderSpecifier(pageable.getSort(), Expressions.path(Place.class, "place")))
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        JPAQuery<Long> totalCountQuery = queryFactory
            .select(place.count())
            .from(place)
            .join(place.bannerPlaceMappingList, bannerPlaceMapping)
            .where(bannerPlaceMapping.banner.id.eq(bannerId));

        return PageableExecutionUtils.getPage(content, pageable, totalCountQuery::fetchOne);
    }

    public Map<Long, SurroundingDateBusinessHour> getSurroundingBusinessHourByPlaceIdIn(
        List<Long> idList, LocalDate today) {
        LocalDate yesterday = today.minusDays(1);

        DayOfWeek todayDayOfWeek = today.getDayOfWeek();
        DayOfWeek yesterdayDayOfWeek = yesterday.getDayOfWeek();

        List<Tuple> results = queryFactory
            .select(fixedBusinessHour.place.id,
                fixedBusinessHour.openTime,
                fixedBusinessHour.closeTime,
                fixedBusinessHour.breakStart,
                fixedBusinessHour.breakEnd,
                fixedBusinessHour.isDayOff,
                fixedBusinessHour.dayOfWeek,
                temporaryBusinessHour.openTime,
                temporaryBusinessHour.closeTime,
                temporaryBusinessHour.breakStart,
                temporaryBusinessHour.breakEnd,
                temporaryBusinessHour.isDayOff
            )
            .from(fixedBusinessHour)
            .leftJoin(temporaryBusinessHour)
            .on(fixedBusinessHour.place.id.eq(temporaryBusinessHour.place.id)
                .and(temporaryBusinessHour.date.in(yesterday, today))
                .and(fixedBusinessHour.dayOfWeek.eq(temporaryBusinessHour.dayOfWeek)))
            .where(fixedBusinessHour.place.id.in(idList)
                .and(fixedBusinessHour.dayOfWeek.in(yesterdayDayOfWeek, todayDayOfWeek)))
            .fetch();

        Map<Long, SurroundingDateBusinessHour> resultMap = new HashMap<>();
        for (Tuple row : results) {
            Long placeId = row.get(fixedBusinessHour.place.id);
            SurroundingDateBusinessHour surroundingDateBusinessHour = resultMap.getOrDefault(
                placeId, new SurroundingDateBusinessHour());

            DayOfWeek dayOfWeek = row.get(fixedBusinessHour.dayOfWeek);
            BusinessHour businessHour = buildBusinessHour(row);
            if (Objects.equals(dayOfWeek, todayDayOfWeek)) {
                surroundingDateBusinessHour.setToday(businessHour);
            } else {
                surroundingDateBusinessHour.setYesterday(businessHour);
            }
            resultMap.put(placeId, surroundingDateBusinessHour);
        }

        return resultMap;
    }

    private BusinessHour buildBusinessHour(Tuple row) {
        if ((row.get(temporaryBusinessHour.openTime) != null
            && row.get(temporaryBusinessHour.closeTime) != null)
            || row.get(temporaryBusinessHour.isDayOff) != null) {
            return BusinessHour.builder()
                .openTime(row.get(temporaryBusinessHour.openTime))
                .closeTime(row.get(temporaryBusinessHour.closeTime))
                .breakStart(row.get(temporaryBusinessHour.breakStart))
                .breakEnd(row.get(temporaryBusinessHour.breakEnd))
                .isDayOff(row.get(temporaryBusinessHour.isDayOff))
                .build();
        } else {
            return BusinessHour.builder()
                .openTime(row.get(fixedBusinessHour.openTime))
                .closeTime(row.get(fixedBusinessHour.closeTime))
                .breakStart(row.get(fixedBusinessHour.breakStart))
                .breakEnd(row.get(fixedBusinessHour.breakEnd))
                .isDayOff(row.get(fixedBusinessHour.isDayOff))
                .build();
        }
    }

}
