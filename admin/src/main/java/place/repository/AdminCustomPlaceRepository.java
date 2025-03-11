package place.repository;


import static com.jigumulmi.config.querydsl.Utils.getOrderSpecifier;
import static com.jigumulmi.config.querydsl.Utils.nullSafeBuilder;
import static com.jigumulmi.place.domain.QPlace.place;
import static com.jigumulmi.place.domain.QPlaceCategoryMapping.placeCategoryMapping;
import static com.jigumulmi.place.domain.QSubwayStationPlace.subwayStationPlace;

import com.jigumulmi.place.domain.Place;
import com.jigumulmi.place.vo.PlaceCategoryGroup;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;
import place.dto.request.AdminGetPlaceListRequestDto;

@Repository
@RequiredArgsConstructor
public class AdminCustomPlaceRepository {

    private final JPAQueryFactory queryFactory;

    public Page<Place> getPlaceList(Pageable pageable, AdminGetPlaceListRequestDto requestDto) {
        List<Place> content = queryFactory
            .selectFrom(place)
            .where(placeCondition(requestDto))
            .orderBy(getOrderSpecifier(pageable.getSort(), Expressions.path(Place.class, "place")))
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        JPAQuery<Long> totalCountQuery = queryFactory
            .select(place.count())
            .from(place)
            .where(placeCondition(requestDto));

        return PageableExecutionUtils.getPage(content, pageable, totalCountQuery::fetchOne);
    }

    public BooleanExpression placeCondition(AdminGetPlaceListRequestDto requestDto) {
        return (
            place.isFromAdmin.eq(requestDto.getIsFromAdmin())
                .and(subwayStationCondition(requestDto.getSubwayStationId()))
                .and(categoryGroupCondition(requestDto.getCategoryGroup()))
                .and(placeNameContains(requestDto.getPlaceName()))
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
}
