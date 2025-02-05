package com.jigumulmi.place.repository;


import static com.jigumulmi.config.querydsl.Utils.nullSafeBuilder;
import static com.jigumulmi.member.domain.QMember.member;
import static com.jigumulmi.place.domain.QFixedBusinessHour.fixedBusinessHour;
import static com.jigumulmi.place.domain.QPlace.place;
import static com.jigumulmi.place.domain.QPlaceCategoryMapping.placeCategoryMapping;
import static com.jigumulmi.place.domain.QPlaceLike.placeLike;
import static com.jigumulmi.place.domain.QReview.review;
import static com.jigumulmi.place.domain.QReviewImage.reviewImage;
import static com.jigumulmi.place.domain.QReviewReply.reviewReply;
import static com.jigumulmi.place.domain.QSubwayStation.subwayStation;
import static com.jigumulmi.place.domain.QSubwayStationLine.subwayStationLine;
import static com.jigumulmi.place.domain.QSubwayStationLineMapping.subwayStationLineMapping;
import static com.jigumulmi.place.domain.QSubwayStationPlace.subwayStationPlace;
import static com.jigumulmi.place.domain.QTemporaryBusinessHour.temporaryBusinessHour;
import static com.querydsl.core.group.GroupBy.groupBy;
import static com.querydsl.core.group.GroupBy.list;
import static com.querydsl.core.types.dsl.Expressions.FALSE;
import static com.querydsl.core.types.dsl.Expressions.TRUE;
import static com.querydsl.core.types.dsl.Expressions.stringTemplate;

import com.jigumulmi.admin.place.dto.WeeklyBusinessHourDto;
import com.jigumulmi.banner.repository.CustomBannerRepository;
import com.jigumulmi.member.domain.Member;
import com.jigumulmi.member.dto.response.MemberDetailResponseDto;
import com.jigumulmi.place.domain.Review;
import com.jigumulmi.place.dto.BusinessHour;
import com.jigumulmi.place.dto.PositionDto;
import com.jigumulmi.place.dto.response.PlaceBasicResponseDto;
import com.jigumulmi.place.dto.response.ReviewReplyResponseDto;
import com.jigumulmi.place.dto.response.SubwayStationResponseDto;
import com.jigumulmi.place.dto.response.SubwayStationResponseDto.SubwayStationLineDto;
import com.jigumulmi.place.vo.PlaceCategoryGroup;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.ConstantImpl;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CustomPlaceRepository {

    private final JPAQueryFactory queryFactory;
    private final CustomBannerRepository customBannerRepository;

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

    public BooleanBuilder placeNameContains(String name) {
        return nullSafeBuilder(() -> place.name.contains(name));
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

    public PlaceBasicResponseDto getPlaceById(Long placeId) {
        // 중첩 리스트 프로젝션 안되는 듯...

        return queryFactory
            .from(place)
            .join(place.subwayStationPlaceList, subwayStationPlace)
            .on(subwayStationPlace.isMain.eq(true))
            .join(subwayStationPlace.subwayStation, subwayStation)
            .join(subwayStation.subwayStationLineMappingList, subwayStationLineMapping)
            .join(subwayStationLineMapping.subwayStationLine)
            .where(place.id.eq(placeId).and(place.isApproved.eq(true)))
            .transform(
                groupBy(place.id).as(
                    Projections.fields(PlaceBasicResponseDto.class,
                        place.id,
                        place.name,
                        Projections.fields(PositionDto.class,
                            place.latitude,
                            place.longitude
                        ).as("position"),
                        place.address,
                        place.contact,
                        place.additionalInfo,
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
                        ).as("subwayStation")
                    )
                )
            ).get(placeId);
    }

    public WeeklyBusinessHourDto getWeeklyBusinessHourByPlaceId(
        Long placeId, LocalDate today) {
        int year = today.getYear();
        int weekOfYear = today.get(WeekFields.SUNDAY_START.weekOfYear());

        List<Tuple> results = queryFactory
            .select(fixedBusinessHour.place.id,
                fixedBusinessHour.openTime,
                fixedBusinessHour.closeTime,
                fixedBusinessHour.breakStart,
                fixedBusinessHour.breakEnd,
                fixedBusinessHour.isDayOff,
                fixedBusinessHour.dayOfWeek,
                temporaryBusinessHour.openTime,
                temporaryBusinessHour.closeTime,
                temporaryBusinessHour.breakStart,
                temporaryBusinessHour.breakEnd,
                temporaryBusinessHour.isDayOff
            )
            .from(fixedBusinessHour)
            .leftJoin(temporaryBusinessHour)
            .on(fixedBusinessHour.place.id.eq(temporaryBusinessHour.place.id)
                .and(temporaryBusinessHour.year.eq(year))
                .and(temporaryBusinessHour.weekOfYear.eq(weekOfYear))
                .and(fixedBusinessHour.dayOfWeek.eq(temporaryBusinessHour.dayOfWeek)))
            .where(fixedBusinessHour.place.id.eq(placeId))
            .fetch();

        WeeklyBusinessHourDto fixedBusinessHourResponseDto = new WeeklyBusinessHourDto();
        for (Tuple row : results) {
            DayOfWeek dayOfWeek = row.get(fixedBusinessHour.dayOfWeek);
            BusinessHour businessHour = customBannerRepository.buildBusinessHour(row);
            fixedBusinessHourResponseDto.updateBusinessHour(Objects.requireNonNull(dayOfWeek), businessHour);
        }

        return fixedBusinessHourResponseDto;
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

    public Page<Review> getReviewListByPlaceId(Long placeId, Pageable pageable) {
        List<Review> reviewList = queryFactory
            .selectFrom(review)
            .join(review.member, member).fetchJoin()
            .where(review.place.id.eq(placeId))
            .orderBy(review.createdAt.desc())
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        JPAQuery<Long> totalCountQuery = queryFactory
            .select(review.count())
            .from(review)
            .where(review.place.id.eq(placeId));

        return PageableExecutionUtils.getPage(reviewList, pageable, totalCountQuery::fetchOne);
    }

    private BooleanExpression memberEq(Member requestMember) {
        if (requestMember == null) {
            return FALSE;
        } else {
            return member.eq(requestMember);
        }
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
                        member.createdAt,
                        member.id,
                        member.nickname,
                        member.email).as("member"),
                    new CaseBuilder()
                        .when(reviewReply.createdAt.eq(reviewReply.modifiedAt)).then(false)
                        .otherwise(true).as("isEdited")
                )
            )
            .from(reviewReply)
            .join(reviewReply.member, member)
            .where(reviewReply.review.id.eq(reviewId))
            .orderBy(reviewReply.createdAt.asc())
            .fetch();
    }
}
