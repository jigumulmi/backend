package com.jigumulmi.place.repository;


import static com.jigumulmi.place.domain.QReview.review;

import com.jigumulmi.place.dto.response.OverallReviewResponseDto;
import com.jigumulmi.place.dto.response.OverallReviewResponseDto.ReviewRatingStatsDto;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.MathExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CustomPlaceRepositoryImpl implements CustomPlaceRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<ReviewRatingStatsDto> getReviewRatingStatsByPlaceId(Long placeId) {
        return queryFactory
            .select(Projections.fields(OverallReviewResponseDto.ReviewRatingStatsDto.class,
                review.rating, review.rating.count().as("count")
            ))
            .from(review)
            .where(review.restaurant.id.eq(placeId))
            .groupBy(review.rating)
            .orderBy(review.rating.asc())
            .fetch();
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
