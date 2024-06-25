package com.jigumulmi.admin.repository;


import static com.jigumulmi.config.querydsl.Utils.getOrderSpecifier;
import static com.jigumulmi.place.domain.QPlace.place;
import static com.jigumulmi.place.domain.QSubwayStation.subwayStation;
import static com.jigumulmi.place.domain.QSubwayStationPlace.subwayStationPlace;

import com.jigumulmi.admin.dto.response.AdminPlaceListResponseDto.PlaceDto;
import com.jigumulmi.place.domain.Place;
import com.jigumulmi.place.dto.response.SubwayStationResponseDto;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CustomAdminRepository {

    private final JPAQueryFactory queryFactory;

    public Page<PlaceDto> getPlaceList(Pageable pageable) {
        List<PlaceDto> content = queryFactory
            .select(
                Projections.fields(PlaceDto.class,
                    place.id,
                    place.name,
                    Projections.fields(SubwayStationResponseDto.class,
                        subwayStation.id,
                        subwayStation.stationName,
                        subwayStationPlace.isMain
                    ).as("subwayStation"),
                    place.category,
                    place.isApproved
                )
            )
            .from(place)
            .leftJoin(place.subwayStationPlaceList, subwayStationPlace)
            .on(place.id.eq(subwayStationPlace.place.id).and(subwayStationPlace.isMain.eq(true)))
            .leftJoin(subwayStationPlace.subwayStation, subwayStation)
            .orderBy(getOrderSpecifier(pageable.getSort(), Expressions.path(Place.class, "place")))
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        JPAQuery<Long> totalCountQuery = queryFactory
            .select(place.count())
            .from(place);

        return PageableExecutionUtils.getPage(content, pageable, totalCountQuery::fetchOne);
    }
}
