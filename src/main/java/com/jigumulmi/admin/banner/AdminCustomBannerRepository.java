package com.jigumulmi.admin.banner;


import static com.jigumulmi.banner.domain.QBannerPlaceMapping.bannerPlaceMapping;
import static com.jigumulmi.config.querydsl.Utils.getOrderSpecifier;
import static com.jigumulmi.config.querydsl.Utils.nullSafeBuilder;
import static com.jigumulmi.place.domain.QMenu.menu;
import static com.jigumulmi.place.domain.QPlace.place;

import com.jigumulmi.admin.banner.dto.request.BannerPlaceMappingRequestDto;
import com.jigumulmi.admin.banner.dto.request.DeleteBannerRequestDto;
import com.jigumulmi.admin.banner.dto.request.GetCandidatePlaceListRequestDto;
import com.jigumulmi.place.domain.Place;
import com.jigumulmi.place.repository.CustomPlaceRepository;
import com.jigumulmi.place.vo.District;
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
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
public class AdminCustomBannerRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final JPAQueryFactory queryFactory;

    private final CustomPlaceRepository customPlaceRepository;

    public void batchInsertBannerPlace(Long bannerId, BannerPlaceMappingRequestDto requestDto) {
        String sql = "INSERT INTO banner_place_mapping (banner_id, place_id) " +
            "VALUES (:bannerId, :placeId)";

        SqlParameterSource[] batch = requestDto.getPlaceIdList().stream()
            .map(placeId -> new MapSqlParameterSource()
                .addValue("bannerId", bannerId)
                .addValue("placeId", placeId))
            .toArray(SqlParameterSource[]::new);

        jdbcTemplate.batchUpdate(sql, batch);
    }

    @Transactional
    public void deleteBannerPlace(Long bannerId, BannerPlaceMappingRequestDto requestDto) {
        queryFactory
            .delete(bannerPlaceMapping)
            .where(
                bannerPlaceMapping.banner.id.eq(bannerId)
                    .and(bannerPlaceMapping.place.id.in(requestDto.getPlaceIdList()))
            )
            .execute();
    }

    public Page<Place> getAllMappedPlaceByBannerId(Pageable pageable, Long bannerId) {
        List<Place> content = queryFactory
            .selectFrom(place)
            .join(place.bannerPlaceMappingList, bannerPlaceMapping)
            .where(bannerPlaceMapping.banner.id.eq(bannerId))
            .orderBy(getOrderSpecifier(pageable.getSort(), Expressions.path(Place.class, "place")))
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        JPAQuery<Long> totalCountQuery = queryFactory
            .select(place.count())
            .from(place)
            .join(place.bannerPlaceMappingList, bannerPlaceMapping)
            .where(bannerPlaceMapping.banner.id.eq(bannerId));

        return PageableExecutionUtils.getPage(content, pageable, totalCountQuery::fetchOne);
    }

    @Transactional
    public void deleteBannerPlace(DeleteBannerRequestDto requestDto) {
        queryFactory
            .delete(bannerPlaceMapping)
            .where(
                bannerPlaceMapping.banner.id.in(requestDto.getBannerIdList()))
            .execute();
    }

    public Page<Place> getAllUnmappedPlaceByBannerIdAndFilters(Pageable pageable,
        GetCandidatePlaceListRequestDto requestDto) {
        List<Place> content = queryFactory
            .selectFrom(place)
            .leftJoin(place.bannerPlaceMappingList, bannerPlaceMapping)
            .on(bannerPlaceMapping.banner.id.eq(requestDto.getBannerId()))
            .where(placeCondition(requestDto))
            .orderBy(getOrderSpecifier(pageable.getSort(), Expressions.path(Place.class, "place")))
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        JPAQuery<Long> totalCountQuery = queryFactory
            .select(place.count())
            .from(place)
            .leftJoin(place.bannerPlaceMappingList, bannerPlaceMapping)
            .on(bannerPlaceMapping.banner.id.eq(requestDto.getBannerId()))
            .where(placeCondition(requestDto));

        return PageableExecutionUtils.getPage(content, pageable, totalCountQuery::fetchOne);
    }

    public BooleanBuilder districtEq(District district) {
        return nullSafeBuilder(() -> place.district.eq(district));
    }

    public BooleanExpression menuNameContains(String name) {
        if (name == null) {
            return null;
        } else {
            return place.id.in(
                JPAExpressions
                    .select(menu.place.id)
                    .from(menu)
                    .where(menu.name.contains(name))
            );
        }
    }

    public BooleanExpression placeCondition(GetCandidatePlaceListRequestDto requestDto) {
        return (
            bannerPlaceMapping.place.id.isNull().and(place.isApproved.isTrue())
                .and(customPlaceRepository.subwayStationCondition(requestDto.getSubwayStationId()))
                .and(customPlaceRepository.categoryGroupCondition(
                    requestDto.getPlaceCategoryGroup()))
                .and(customPlaceRepository.placeNameContains(requestDto.getPlaceName()))
                .and(districtEq(requestDto.getDistrict()))
                .and(menuNameContains(requestDto.getMenuName()))
        );
    }
}
