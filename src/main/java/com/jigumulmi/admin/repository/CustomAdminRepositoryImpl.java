package com.jigumulmi.admin.repository;


import static com.jigumulmi.place.domain.QPlace.place;
import static com.jigumulmi.place.domain.QSubwayStation.subwayStation;
import static com.jigumulmi.place.domain.QSubwayStationPlace.subwayStationPlace;

import com.jigumulmi.admin.dto.response.AdminPlaceListResponseDto.PlaceDto;
import com.jigumulmi.place.domain.Place;
import com.jigumulmi.place.dto.response.SubwayStationResponseDto;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.SimplePath;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CustomAdminRepositoryImpl implements CustomAdminRepository {

    private final JPAQueryFactory queryFactory;

    @SuppressWarnings({"rawtypes", "unchecked", "cast"})
    private OrderSpecifier[] getOrderSpecifier(Sort sort, Path<?> parent) {
        List<OrderSpecifier> orders = new ArrayList<>();

        sort.stream().forEach(
            order -> {
                Order direction = order.isAscending() ? Order.ASC : Order.DESC;
                SimplePath<Object> fieldPath = Expressions.path(Object.class, parent,
                    order.getProperty());
                orders.add(new OrderSpecifier(direction, fieldPath));
            }
        );

        return orders.toArray(OrderSpecifier[]::new);
    }


    @Override
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
            .join(place.subwayStationPlaceList, subwayStationPlace)
            .join(subwayStationPlace.subwayStation, subwayStation)
            .where(subwayStationPlace.isMain.eq(true))
            .orderBy(getOrderSpecifier(pageable.getSort(), Expressions.path(Place.class, "place")))
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        JPAQuery<Long> countQuery = queryFactory.select(place.count())
            .from(place);

        return PageableExecutionUtils.getPage(content, pageable,
            countQuery::fetchOne);
    }
}
