package com.jigumulmi.admin.repository;


import com.jigumulmi.admin.dto.request.AdminGetPlaceListRequestDto;
import com.jigumulmi.admin.dto.response.AdminPlaceListResponseDto.PlaceDto;
import com.jigumulmi.place.domain.Place;
import com.jigumulmi.place.dto.response.PlaceResponseDto.CategoryMappingDto;
import com.jigumulmi.place.dto.response.SubwayStationResponseDto;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.jigumulmi.config.querydsl.Utils.getOrderSpecifier;
import static com.jigumulmi.place.domain.QPlace.place;
import static com.jigumulmi.place.domain.QPlaceCategoryMapping.placeCategoryMapping;
import static com.jigumulmi.place.domain.QSubwayStation.subwayStation;
import static com.jigumulmi.place.domain.QSubwayStationPlace.subwayStationPlace;
import static com.querydsl.core.group.GroupBy.groupBy;
import static com.querydsl.core.group.GroupBy.list;
import static com.querydsl.core.types.dsl.Expressions.TRUE;

@Repository
@RequiredArgsConstructor
public class CustomAdminRepository {

    private final JPAQueryFactory queryFactory;

    public Page<PlaceDto> getPlaceList(Pageable pageable, AdminGetPlaceListRequestDto requestDto) {
        List<PlaceDto> content = queryFactory
            .selectFrom(place)
            .join(place.categoryMappingList, placeCategoryMapping)
            .leftJoin(place.subwayStationPlaceList, subwayStationPlace)
            .on(place.id.eq(subwayStationPlace.place.id).and(subwayStationPlace.isMain.eq(true)))
            .leftJoin(subwayStationPlace.subwayStation, subwayStation)
            .where(place.isFromAdmin.eq(requestDto.getIsFromAdmin())
                .and(placeCondition(requestDto.getPlaceName())))
            .orderBy(getOrderSpecifier(pageable.getSort(), Expressions.path(Place.class, "place")))
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .transform(
                groupBy(place.id).list(
                    Projections.fields(PlaceDto.class,
                        place.id,
                        place.name,
                        Projections.fields(SubwayStationResponseDto.class,
                            subwayStation.id,
                            subwayStation.stationName,
                            subwayStationPlace.isMain
                        ).as("subwayStation"),
                        list(Projections.constructor(CategoryMappingDto.class,
                            placeCategoryMapping.categoryGroup,
                            placeCategoryMapping.category
                        )).as("categoryMappingDtoList"),
                        place.isApproved
                    )
                )
            );

        JPAQuery<Long> totalCountQuery = queryFactory
            .select(place.count())
            .from(place)
            .where(place.isFromAdmin.eq(requestDto.getIsFromAdmin())
                .and(placeCondition(requestDto.getPlaceName()))
            )
            ;

        return PageableExecutionUtils.getPage(content, pageable, totalCountQuery::fetchOne);
    }

    public BooleanExpression placeCondition(String placeName) {
        if (placeName == null) {
            return TRUE;
        } else {
            return place.name.startsWith(placeName);
        }
    }
}
