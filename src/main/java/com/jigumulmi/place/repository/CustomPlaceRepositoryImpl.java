package com.jigumulmi.place.repository;


import static com.jigumulmi.place.domain.QReview.review;
import static com.jigumulmi.place.domain.QReviewReply.reviewReply;
import static com.querydsl.core.group.GroupBy.groupBy;
import static com.querydsl.core.types.dsl.Expressions.stringTemplate;

import com.jigumulmi.member.dto.response.MemberDetailResponseDto;
import com.jigumulmi.place.dto.response.ReviewListResponseDto;
import com.jigumulmi.place.dto.response.ReviewReplyResponseDto;
import com.querydsl.core.types.ConstantImpl;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.MathExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
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

    @Override
    public List<ReviewListResponseDto> getReviewListByPlaceId(Long placeId, Long requestMemberId) {
        // fetchJoin() 과 Projections 동시 사용 불가
        // 엔티티인 상태로 탐색하는 것이 fetchJoin이기 때문
        return queryFactory
            .select(
                Projections.fields(ReviewListResponseDto.class,
                    stringTemplate(
                        "DATE_FORMAT({0}, {1})",
                        review.modifiedAt,
                        ConstantImpl.create("%Y.%m.%d")
                    ).as("reviewedAt"),
                    review.id,
                    review.rating,
                    review.content,
                    review.reviewReplyList.size().as("replyCount"),
                    new CaseBuilder()
                        .when(review.member.id.eq(requestMemberId)).then(true)
                        .otherwise(false).as("isEditable"),
                    Projections.fields(MemberDetailResponseDto.class,
                        review.member.createdAt,
                        review.member.id,
                        review.member.nickname,
                        review.member.email).as("member"))
            ).distinct()
            .from(review)
            .join(review.member)
            .leftJoin(review.reviewReplyList)
            .where(review.restaurant.id.eq(placeId))
            .orderBy(review.modifiedAt.desc())
            .fetch();
    }

    @Override
    public List<ReviewReplyResponseDto> getReviewReplyListByReviewId(Long requestMemberId,
        Long reviewId) {

        return queryFactory
            .select(
                Projections.fields(
                    ReviewReplyResponseDto.class,
                    stringTemplate(
                        "DATE_FORMAT({0}, {1})",
                        reviewReply.modifiedAt,
                        ConstantImpl.create("%Y.%m.%d")
                    ).as("repliedAt"),
                    reviewReply.id,
                    reviewReply.content,
                    new CaseBuilder()
                        .when(review.member.id.eq(requestMemberId)).then(true)
                        .otherwise(false).as("isEditable"),
                    Projections.fields(MemberDetailResponseDto.class,
                        review.member.createdAt,
                        review.member.id,
                        review.member.nickname,
                        review.member.email).as("member"))
            ).distinct()
            .from(reviewReply)
            .where(reviewReply.review.id.eq(reviewId))
            .join(reviewReply.member)
            .orderBy(reviewReply.modifiedAt.desc())
            .fetch();
    }

}
