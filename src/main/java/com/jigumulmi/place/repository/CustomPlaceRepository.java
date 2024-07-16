package com.jigumulmi.place.repository;


import static com.jigumulmi.place.domain.QPlace.place;
import static com.jigumulmi.place.domain.QPlaceImage.placeImage;
import static com.jigumulmi.place.domain.QReview.review;
import static com.jigumulmi.place.domain.QReviewReaction.reviewReaction;
import static com.jigumulmi.place.domain.QReviewReply.reviewReply;
import static com.jigumulmi.place.domain.QReviewReplyReaction.reviewReplyReaction;
import static com.jigumulmi.place.domain.QSubwayStation.subwayStation;
import static com.jigumulmi.place.domain.QSubwayStationLine.subwayStationLine;
import static com.jigumulmi.place.domain.QSubwayStationLineMapping.subwayStationLineMapping;
import static com.jigumulmi.place.domain.QSubwayStationPlace.subwayStationPlace;
import static com.jigumulmi.place.vo.CurrentOpeningInfo.getSurroundingDateOpeningHourExpressions;
import static com.querydsl.core.group.GroupBy.groupBy;
import static com.querydsl.core.group.GroupBy.list;
import static com.querydsl.core.types.dsl.Expressions.TRUE;
import static com.querydsl.core.types.dsl.Expressions.stringTemplate;

import com.jigumulmi.config.exception.CustomException;
import com.jigumulmi.config.exception.errorCode.CommonErrorCode;
import com.jigumulmi.member.dto.response.MemberDetailResponseDto;
import com.jigumulmi.place.dto.response.PlaceDetailResponseDto;
import com.jigumulmi.place.dto.response.PlaceResponseDto;
import com.jigumulmi.place.dto.response.PlaceResponseDto.ImageDto;
import com.jigumulmi.place.dto.response.PlaceResponseDto.PositionDto;
import com.jigumulmi.place.dto.response.PlaceResponseDto.SurroundingDateOpeningHour;
import com.jigumulmi.place.dto.response.ReactionDto;
import com.jigumulmi.place.dto.response.ReviewReplyResponseDto;
import com.jigumulmi.place.dto.response.ReviewResponseDto;
import com.jigumulmi.place.dto.response.SubwayStationResponseDto;
import com.jigumulmi.place.dto.response.SubwayStationResponseDto.SubwayStationLineDto;
import com.querydsl.core.types.ConstantImpl;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CustomPlaceRepository {

    private final JPAQueryFactory queryFactory;

    public List<PlaceResponseDto> getPlaceList(Long subwayStationId) {

        return queryFactory
            .from(place)
            .join(place.placeImageList, placeImage)
            .on(place.id.eq(placeImage.place.id).and(placeImage.isMain.eq(true)))
            .join(place.subwayStationPlaceList, subwayStationPlace)
            .on(place.id.eq(subwayStationPlace.place.id).and(subwayStationPlace.isMain.eq(true)))
            .join(subwayStationPlace.subwayStation, subwayStation)
            .join(subwayStation.subwayStationLineMappingList, subwayStationLineMapping)
            .join(subwayStationLineMapping.subwayStationLine, subwayStationLine)
            .where(place.isApproved.eq(true), placeCondition(subwayStationId))
            .transform(
                groupBy(place.id).list(
                    Projections.fields(PlaceResponseDto.class,
                        place.id,
                        place.name,
                        list(Projections.fields(ImageDto.class,
                            placeImage.id,
                            placeImage.url,
                            TRUE.as("isMain")
                        )).as("imageList"),
                        Projections.fields(PositionDto.class,
                            place.latitude,
                            place.longitude
                        ).as("position"),
                        Projections.fields(SubwayStationResponseDto.class,
                            subwayStation.id,
                            subwayStation.stationName,
                            TRUE.as("isMain"),
                            list(
                                Projections.fields(
                                    SubwayStationLineDto.class,
                                    subwayStationLine.id,
                                    subwayStationLine.lineNumber
                                )
                            ).as("subwayStationLineList")
                        ).as("subwayStation"),
                        place.category,
                        Projections.fields(SurroundingDateOpeningHour.class,
                            getSurroundingDateOpeningHourExpressions()
                        ).as("surroundingDateOpeningHour")
                    )
                )
            );
    }

    public BooleanExpression placeCondition(Long subwayStationId) {
        if (subwayStationId == null) {
            return null;
        } else {
            return place.id.in(
                JPAExpressions
                    .select(place.id)
                    .from(place)
                    .join(place.subwayStationPlaceList, subwayStationPlace)
                    .where(subwayStationPlace.subwayStation.id.eq(subwayStationId))
            );
        }

    }


    public PlaceDetailResponseDto getPlaceDetail(Long placeId) {
        // 둘 이상의 컬렉션에 fetchjoin() 불가
        // 중첩 리스트 프로젝션 안되는 듯...

        return queryFactory
            .from(place)
            .join(place.subwayStationPlaceList, subwayStationPlace)
            .on(subwayStationPlace.place.id.eq(place.id).and(subwayStationPlace.isMain.eq(true)))
            .join(subwayStationPlace.subwayStation, subwayStation)
            .join(subwayStation.subwayStationLineMappingList, subwayStationLineMapping)
            .join(subwayStationLineMapping.subwayStationLine)
            .where(place.id.eq(placeId).and(place.isApproved.eq(true)))
            .transform(
                groupBy(place.id).list(
                    Projections.fields(PlaceDetailResponseDto.class,
                        place.id,
                        place.name,
                        Projections.fields(PositionDto.class,
                            place.latitude,
                            place.longitude
                        ).as("position"),
                        Projections.fields(SubwayStationResponseDto.class,
                            subwayStation.id,
                            subwayStation.stationName,
                            TRUE.as("isMain"),
                            list(
                                Projections.fields(
                                    SubwayStationLineDto.class,
                                    subwayStationLine.id,
                                    subwayStationLine.lineNumber
                                )
                            ).as("subwayStationLineList")
                        ).as("subwayStation"),
                        place.category,
                        place.address,
                        place.contact,
                        Projections.fields(PlaceDetailResponseDto.OpeningHourDto.class,
                            place.openingHourSun,
                            place.openingHourMon,
                            place.openingHourTue,
                            place.openingHourWed,
                            place.openingHourThu,
                            place.openingHourFri,
                            place.openingHourSat
                        ).as("openingHour"),
                        place.additionalInfo,
                        Projections.fields(SurroundingDateOpeningHour.class,
                            getSurroundingDateOpeningHourExpressions()
                        ).as("surroundingDateOpeningHour")
                    )
                )
            ).stream().findFirst()
            .orElseThrow(() -> new CustomException(CommonErrorCode.RESOURCE_NOT_FOUND));
    }


    public Map<Integer, Long> getReviewRatingStatsByPlaceId(Long placeId) {

        return queryFactory
            .from(review)
            .where(review.place.id.eq(placeId).and(review.deletedAt.isNull()))
            .groupBy(review.rating)
            .transform(groupBy(review.rating).as(review.rating.count()));
    }


    public List<ReviewResponseDto> getReviewListByPlaceId(Long placeId, Long requestMemberId) {
        // fetchJoin() 과 Projections 동시 사용 불가
        // 엔티티인 상태로 탐색하는 것이 fetchJoin이기 때문

        return queryFactory
            .select(
                Projections.fields(ReviewResponseDto.class,
                    stringTemplate(
                        "DATE_FORMAT({0}, {1})",
                        review.modifiedAt,
                        ConstantImpl.create("%Y.%m.%d")
                    ).as("reviewedAt"),
                    review.deletedAt,
                    review.createdAt,
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
                        review.member.email).as("member"),
                    Projections.fields(ReactionDto.class,
                        ExpressionUtils.as(JPAExpressions.select(reviewReaction.count())
                                .from(reviewReaction)
                                .where(reviewReaction.review.id.eq(review.id)),
                            "likeReactionCount"),
                        reviewReaction.id.as("likeReactionId")
                    ).as("reaction"),
                    new CaseBuilder()
                        .when(review.createdAt.eq(review.modifiedAt)).then(false)
                        .otherwise(true).as("isEdited")
                )
            ).distinct()
            .from(review)
            .join(review.member)
            .leftJoin(review.reviewReplyList)
            .leftJoin(review.reviewReactionList, reviewReaction)
            .on(reviewReaction.review.id.eq(review.id)
                .and(reviewReaction.member.id.eq(requestMemberId)))
            .where(review.place.id.eq(placeId))
            .orderBy(review.createdAt.desc())
            .fetch();
    }


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
                    reviewReply.createdAt,
                    reviewReply.id,
                    reviewReply.content,
                    new CaseBuilder()
                        .when(reviewReply.member.id.eq(requestMemberId)).then(true)
                        .otherwise(false).as("isEditable"),
                    Projections.fields(MemberDetailResponseDto.class,
                        reviewReply.member.createdAt,
                        reviewReply.member.id,
                        reviewReply.member.nickname,
                        reviewReply.member.email).as("member"),
                    Projections.fields(ReactionDto.class,
                        ExpressionUtils.as(JPAExpressions.select(reviewReplyReaction.count())
                                .from(reviewReplyReaction)
                                .where(reviewReplyReaction.reviewReply.id.eq(reviewReply.id)),
                            "likeReactionCount"),
                        reviewReplyReaction.id.as("likeReactionId")
                    ).as("reaction"),
                    new CaseBuilder()
                        .when(reviewReply.createdAt.eq(reviewReply.modifiedAt)).then(false)
                        .otherwise(true).as("isEdited")
                )
            ).distinct()
            .from(reviewReply)
            .where(reviewReply.review.id.eq(reviewId))
            .join(reviewReply.member)
            .leftJoin(reviewReply.reviewReplyReactionList, reviewReplyReaction)
            .on(reviewReplyReaction.reviewReply.id.eq(reviewReply.id)
                .and(reviewReplyReaction.member.id.eq(requestMemberId)))
            .orderBy(reviewReply.createdAt.asc())
            .fetch();
    }

}
