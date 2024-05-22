package com.jigumulmi.place.repository;


import static com.jigumulmi.place.domain.QRestaurant.restaurant;
import static com.jigumulmi.place.domain.QReview.review;
import static com.jigumulmi.place.domain.QReviewReply.reviewReply;
import static com.jigumulmi.place.domain.QSubwayStation.subwayStation;
import static com.jigumulmi.place.domain.QSubwayStationPlace.subwayStationPlace;
import static com.querydsl.core.group.GroupBy.groupBy;
import static com.querydsl.core.group.GroupBy.list;
import static com.querydsl.core.types.dsl.Expressions.stringTemplate;

import com.jigumulmi.config.exception.CustomException;
import com.jigumulmi.config.exception.errorCode.CommonErrorCode;
import com.jigumulmi.member.dto.response.MemberDetailResponseDto;
import com.jigumulmi.place.domain.Restaurant;
import com.jigumulmi.place.dto.response.RestaurantResponseDto;
import com.jigumulmi.place.dto.response.RestaurantResponseDto.PositionDto;
import com.jigumulmi.place.dto.response.ReviewListResponseDto;
import com.jigumulmi.place.dto.response.ReviewReplyResponseDto;
import com.jigumulmi.place.dto.response.SubwayStationResponseDto;
import com.querydsl.core.types.ConstantImpl;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.MathExpressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CustomPlaceRepositoryImpl implements CustomPlaceRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<RestaurantResponseDto> getRestaurantList(Long placeId, Long subwayStationId) {

        return queryFactory
            .from(restaurant)
            .join(restaurant.subwayStationPlaceList, subwayStationPlace)
            .join(subwayStationPlace.subwayStation)
            .where(restaurant.isApproved.eq(true),
                subwayStationCondition(placeId, subwayStationId)
            )
            .transform(
                groupBy(restaurant.id).list(
                    Projections.fields(RestaurantResponseDto.class,
                        restaurant.id,
                        restaurant.name,
                        restaurant.mainImageUrl,
                        Projections.fields(PositionDto.class,
                            restaurant.latitude,
                            restaurant.longitude
                        ).as("position"),
                        list(
                            Projections.fields(SubwayStationResponseDto.class,
                                subwayStation.id,
                                subwayStation.stationName,
                                subwayStation.lineNumber
                            )
                        ).as("subwayStationList")
                    )
                )
            );
    }

    public BooleanExpression subwayStationCondition(Long placeId, Long subwayStationId) {
        if (placeId != null && subwayStationId == null) {
            JPQLQuery<Long> subwayStationIdListSubquery = JPAExpressions
                .select(subwayStationPlace.subwayStation.id)
                .from(subwayStationPlace)
                .where(subwayStationPlace.restaurant.id.eq(placeId));

            return subwayStation.id.in(subwayStationIdListSubquery);

        } else if (subwayStationId != null && placeId == null) {
            return subwayStation.id.eq(subwayStationId);
        } else if (subwayStationId == null && placeId == null) {
            return null;
        } else {
            throw new CustomException(CommonErrorCode.INVALID_PARAMETER);
        }

    }

    @Override
    public Restaurant getRestaurantDetail(Long placeId) {
        // 둘 이상의 컬렉션에 fetchjoin() 불가
        Restaurant restaurantDetail = queryFactory
            .selectFrom(restaurant)
            .join(restaurant.menuList)
            .join(restaurant.subwayStationPlaceList, subwayStationPlace)
            .join(subwayStationPlace.subwayStation)
            .where(restaurant.isApproved.eq(true),
                restaurant.id.eq(placeId))
            .fetchOne();

        Hibernate.initialize(Objects.requireNonNull(restaurantDetail).getMenuList());
        Hibernate.initialize(Objects.requireNonNull(restaurantDetail).getSubwayStationPlaceList());
        //Objects.requireNonNull(restaurant).getSubwayStationPlaceList().stream()
        //    .map(SubwayStationPlace::getSubwayStation).forEach(Hibernate::initialize);

        return restaurantDetail;

    }

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
                    review.deletedAt,
                    review.id,
                    review.rating,
                    review.content,
                    review.reviewReplyList.size().as("replyCount"),
                    new CaseBuilder()
                        .when(review.member.id.eq(requestMemberId)).then(true)
                        .otherwise(false).as("isEditable"),
                    Projections.fields(MemberDetailResponseDto.class,
                        review.member.createdAt,
                        review.member.deregisteredAt,
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
                        .when(reviewReply.member.id.eq(requestMemberId)).then(true)
                        .otherwise(false).as("isEditable"),
                    Projections.fields(MemberDetailResponseDto.class,
                        reviewReply.member.createdAt,
                        reviewReply.member.id,
                        reviewReply.member.nickname,
                        reviewReply.member.email).as("member"))
            ).distinct()
            .from(reviewReply)
            .where(reviewReply.review.id.eq(reviewId))
            .join(reviewReply.member)
            .orderBy(reviewReply.modifiedAt.desc())
            .fetch();
    }

}
