package com.jigumulmi.admin.repository;


import static com.jigumulmi.config.querydsl.Utils.getOrderSpecifier;
import static com.jigumulmi.place.domain.QPlace.place;
import static com.querydsl.core.types.dsl.Expressions.TRUE;

import com.jigumulmi.admin.dto.request.AdminGetPlaceListRequestDto;
import com.jigumulmi.place.domain.Place;
import com.querydsl.core.types.dsl.BooleanExpression;
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

    public Page<Place> getPlaceList(Pageable pageable, AdminGetPlaceListRequestDto requestDto) {
        List<Place> content = queryFactory
            .selectFrom(place)
            .where(place.isFromAdmin.eq(requestDto.getIsFromAdmin())
                .and(placeCondition(requestDto.getPlaceName())))
            .orderBy(getOrderSpecifier(pageable.getSort(), Expressions.path(Place.class, "place")))
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch()
            ;

        JPAQuery<Long> totalCountQuery = queryFactory
            .select(place.countDistinct())
            .from(place)
            .where(place.isFromAdmin.eq(requestDto.getIsFromAdmin())
                .and(placeCondition(requestDto.getPlaceName()))
            );

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
