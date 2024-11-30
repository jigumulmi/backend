package com.jigumulmi.place.repository;


import static com.jigumulmi.config.querydsl.Utils.nullSafeBuilder;
import static com.jigumulmi.member.domain.QMember.member;
import static com.jigumulmi.place.domain.QPlace.place;
import static com.jigumulmi.place.domain.QPlaceCategoryMapping.placeCategoryMapping;
import static com.jigumulmi.place.domain.QPlaceImage.placeImage;
import static com.jigumulmi.place.domain.QPlaceLike.placeLike;
import static com.jigumulmi.place.domain.QReview.review;
import static com.jigumulmi.place.domain.QReviewImage.reviewImage;
import static com.jigumulmi.place.domain.QReviewReply.reviewReply;
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
import com.jigumulmi.member.domain.Member;
import com.jigumulmi.member.dto.response.MemberDetailResponseDto;
import com.jigumulmi.place.dto.request.GetPlaceListRequestDto;
import com.jigumulmi.place.dto.response.PlaceDetailResponseDto;
import com.jigumulmi.place.dto.response.PlaceResponseDto;
import com.jigumulmi.place.dto.response.PlaceResponseDto.CategoryDto;
import com.jigumulmi.place.dto.response.PlaceResponseDto.ImageDto;
import com.jigumulmi.place.dto.response.PlaceResponseDto.PositionDto;
import com.jigumulmi.place.dto.response.PlaceResponseDto.SurroundingDateOpeningHour;
import com.jigumulmi.place.dto.response.ReviewImageResponseDto;
import com.jigumulmi.place.dto.response.ReviewReplyResponseDto;
import com.jigumulmi.place.dto.response.ReviewResponseDto;
import com.jigumulmi.place.dto.response.SubwayStationResponseDto;
import com.jigumulmi.place.dto.response.SubwayStationResponseDto.SubwayStationLineDto;
import com.jigumulmi.place.vo.PlaceCategoryGroup;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.ConstantImpl;
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

    public List<PlaceResponseDto> getPlaceList(GetPlaceListRequestDto requestDto) {

        return queryFactory
            .selectFrom(place)
            .join(place.categoryMappingList, placeCategoryMapping)
            .join(place.placeImageList, placeImage)
            .on(place.id.eq(placeImage.place.id).and(placeImage.isMain.eq(true)))
            .join(place.subwayStationPlaceList, subwayStationPlace)
            .on(place.id.eq(subwayStationPlace.place.id).and(subwayStationPlace.isMain.eq(true)))
            .join(subwayStationPlace.subwayStation, subwayStation)
            .join(subwayStation.subwayStationLineMappingList, subwayStationLineMapping)
            .join(subwayStationLineMapping.subwayStationLine, subwayStationLine)
            .where(place.isApproved.eq(true),
                subwayStationCondition(requestDto.getSubwayStationId()),
                categoryGroupCondition(requestDto.getCategoryGroup()),
                nameCondition(requestDto.getPlaceName())
            )
            .transform(
                groupBy(place.id).list(
                    Projections.fields(PlaceResponseDto.class,
                        place.id,
                        place.name,
                        list(Projections.fields(CategoryDto.class,
                            placeCategoryMapping.categoryGroup,
                            placeCategoryMapping.category
                        )).as("categoryList"),
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
                        Projections.fields(SurroundingDateOpeningHour.class,
                            getSurroundingDateOpeningHourExpressions()
                        ).as("surroundingDateOpeningHour")
                    )
                )
            );
    }

    public BooleanExpression subwayStationCondition(Long subwayStationId) {
        if (subwayStationId == null) {
            return null;
        } else {
            return place.id.in(
                JPAExpressions
                    .select(subwayStationPlace.place.id)
                    .from(subwayStationPlace)
                    .where(subwayStationPlace.subwayStation.id.eq(subwayStationId))
            );
        }
    }

    public BooleanExpression nameCondition(String placeName) {
        if (placeName == null) {
            return null;
        } else {
            return place.name.startsWith(placeName);
        }
    }

    public BooleanExpression categoryGroupCondition(PlaceCategoryGroup categoryGroup) {
        if (categoryGroup == null) {
            return null;
        } else {
            return place.id.in(
                JPAExpressions
                    .select(placeCategoryMapping.place.id)
                    .from(placeCategoryMapping)
                    .where(placeCategoryMapping.categoryGroup.eq(categoryGroup))
            );
        }
    }


    public PlaceDetailResponseDto getPlaceDetail(Long placeId) {
        // 둘 이상의 컬렉션에 fetchjoin() 불가
        // 중첩 리스트 프로젝션 안되는 듯...

        return queryFactory
            .from(place)
            .join(place.categoryMappingList, placeCategoryMapping)
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
                        list(Projections.fields(CategoryDto.class,
                            placeCategoryMapping.categoryGroup,
                            placeCategoryMapping.category
                        )).as("categoryList"),
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

    public Long getPlaceLikeCount(Long placeId) {
        return queryFactory
            .select(placeLike.id.count())
            .from(placeLike)
            .where(placeLike.place.id.eq(placeId))
            .fetchOne();
    }


    public Map<Integer, Long> getReviewRatingStatsByPlaceId(Long placeId) {

        return queryFactory
            .from(review)
            .where(review.place.id.eq(placeId).and(review.deletedAt.isNull()))
            .groupBy(review.rating)
            .transform(groupBy(review.rating).as(review.rating.count()));
    }


    public List<ReviewResponseDto> getReviewListByPlaceId(Long placeId, Member requestMember) {
        // fetchJoin() 과 Projections 동시 사용 불가
        // 엔티티인 상태로 탐색하는 것이 fetchJoin이기 때문

        return queryFactory
            .selectFrom(review)
            .join(review.member, member)
            .leftJoin(review.reviewImageList, reviewImage)
            .where(review.place.id.eq(placeId))
            .orderBy(review.createdAt.desc(), reviewImage.createdAt.desc())
            .transform(
                groupBy(review.id).list(
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
                        new CaseBuilder()
                            .when(memberEq(requestMember)).then(true)
                            .otherwise(false).as("isEditable"),
                        Projections.fields(MemberDetailResponseDto.class,
                            review.member.createdAt,
                            review.member.deregisteredAt,
                            review.member.id,
                            review.member.nickname,
                            review.member.email).as("member"),
                        new CaseBuilder()
                            .when(review.createdAt.eq(review.modifiedAt)).then(false)
                            .otherwise(true).as("isEdited"),
                        list(
                            Projections.fields(
                                ReviewImageResponseDto.class,
                                reviewImage.id,
                                reviewImage.s3Key,
                                reviewImage.createdAt
                            ).skipNulls()
                        ).as("imageList")
                    )
                )
            );
    }

    private BooleanBuilder memberEq(Member requestMember) {
        return nullSafeBuilder(() -> member.eq(requestMember));
    }

    public Map<Long, Long> getReviewReplyCount(Long placeId) {

        return queryFactory
            .selectFrom(review)
            .join(review.reviewReplyList, reviewReply)
            .where(review.place.id.eq(placeId))
            .groupBy(review.id)
            .transform(groupBy(review.id).as(reviewReply.count()));
    }

    public List<ReviewReplyResponseDto> getReviewReplyListByReviewId(Member requestMember,
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
                        .when(memberEq(requestMember)).then(true)
                        .otherwise(false).as("isEditable"),
                    Projections.fields(MemberDetailResponseDto.class,
                        reviewReply.member.createdAt,
                        reviewReply.member.id,
                        reviewReply.member.nickname,
                        reviewReply.member.email).as("member"),
                    new CaseBuilder()
                        .when(reviewReply.createdAt.eq(reviewReply.modifiedAt)).then(false)
                        .otherwise(true).as("isEdited")
                )
            ).distinct()
            .from(reviewReply)
            .where(reviewReply.review.id.eq(reviewId))
            .join(reviewReply.member, member)
            .orderBy(reviewReply.createdAt.asc())
            .fetch();
    }
}
