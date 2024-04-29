package com.jigumulmi.place.repository;


import static com.jigumulmi.place.domain.QReview.review;
import static com.querydsl.core.group.GroupBy.groupBy;

import com.querydsl.core.types.dsl.MathExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.Map;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CustomPlaceRepositoryImpl implements CustomPlaceRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Map<Integer, Long> getReviewRatingStatsByPlaceId(Long placeId) {

        return queryFactory
            .from(review)
            .where(review.restaurant.id.eq(placeId))
            .groupBy(review.rating)
            .transform(groupBy(review.rating).as(review.rating.count()));
    }

    @Override
    public Double getAverageRatingByPlaceId(Long placeId) {
        return queryFactory
            .select(MathExpressions.round(review.rating.avg(), 2))
            .from(review)
            .where(review.restaurant.id.eq(placeId))
            .fetchOne();
    }
}
