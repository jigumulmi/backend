package com.jigumulmi.banner

import com.jigumulmi.banner.domain.QBannerPlaceMapping.bannerPlaceMapping
import com.jigumulmi.banner.dto.request.GetCandidatePlaceListRequestDto
import com.jigumulmi.config.querydsl.Utils
import com.jigumulmi.place.domain.Place
import com.jigumulmi.place.domain.QMenu.menu
import com.jigumulmi.place.domain.QPlace.place
import com.jigumulmi.place.repository.AdminCustomPlaceRepository
import com.jigumulmi.place.vo.District
import com.jigumulmi.place.vo.Region
import com.querydsl.core.BooleanBuilder
import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.core.types.dsl.Expressions
import com.querydsl.jpa.JPAExpressions
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.support.PageableExecutionUtils
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional


@Repository
class AdminCustomBannerRepository(
    private val jdbcTemplate: NamedParameterJdbcTemplate,
    private val queryFactory: JPAQueryFactory,

    private val adminCustomPlaceRepository: AdminCustomPlaceRepository,
) {

    fun batchInsertBannerPlace(bannerId: Long, placeIdList: List<Long>) {
        val sql = "INSERT INTO banner_place_mapping (banner_id, place_id) " +
                "VALUES (:bannerId, :placeId)"

        val batch = placeIdList.map { placeId: Long ->
            MapSqlParameterSource()
                .addValue("bannerId", bannerId)
                .addValue("placeId", placeId)
        }.toTypedArray()

        jdbcTemplate.batchUpdate(sql, batch)
    }

    @Transactional
    fun deleteBannerPlaceByBannerIdAndPlaceIdList(bannerId: Long, placeIdList: List<Long>) {
        queryFactory
            .delete(bannerPlaceMapping)
            .where(
                bannerPlaceMapping.banner.id.eq(bannerId)
                    .and(bannerPlaceMapping.place.id.`in`(placeIdList))
            )
            .execute()
    }

    fun getAllMappedPlaceByBannerId(pageable: Pageable, bannerId: Long?): Page<Place> {
        val content = queryFactory
            .selectFrom(place)
            .join(place.bannerPlaceMappingList, bannerPlaceMapping)
            .where(bannerPlaceMapping.banner.id.eq(bannerId))
            .orderBy(
                *Utils.getOrderSpecifier(
                    pageable.sort, Expressions.path(
                        Place::class.java,
                        "com/jigumulmi/place"
                    )
                )
            )
            .offset(pageable.offset)
            .limit(pageable.pageSize.toLong())
            .fetch()

        val totalCountQuery = queryFactory
            .select(place.count())
            .from(place)
            .join(place.bannerPlaceMappingList, bannerPlaceMapping)
            .where(bannerPlaceMapping.banner.id.eq(bannerId))

        return PageableExecutionUtils.getPage(content, pageable) { totalCountQuery.fetchOne()!! }
    }

    @Transactional
    fun deleteBannerPlaceByBannerId(bannerId: Long?) {
        queryFactory
            .delete(bannerPlaceMapping)
            .where(bannerPlaceMapping.banner.id.eq(bannerId))
            .execute()
    }

    fun getAllUnmappedPlaceByBannerIdAndFilters(
        pageable: Pageable,
        requestDto: GetCandidatePlaceListRequestDto
    ): Page<Place> {
        val content = queryFactory
            .selectFrom(place)
            .leftJoin(place.bannerPlaceMappingList, bannerPlaceMapping)
            .on(bannerPlaceMapping.banner.id.eq(requestDto.bannerId))
            .where(placeCondition(requestDto))
            .orderBy(
                *Utils.getOrderSpecifier(
                    pageable.sort, Expressions.path(
                        Place::class.java,
                        "com/jigumulmi/place"
                    )
                )
            )
            .offset(pageable.offset)
            .limit(pageable.pageSize.toLong())
            .fetch()

        val totalCountQuery = queryFactory
            .select(place.count())
            .from(place)
            .leftJoin(place.bannerPlaceMappingList, bannerPlaceMapping)
            .on(bannerPlaceMapping.banner.id.eq(requestDto.bannerId))
            .where(placeCondition(requestDto))

        return PageableExecutionUtils.getPage(content, pageable) { totalCountQuery.fetchOne()!! }
    }

    private fun placeCondition(requestDto: GetCandidatePlaceListRequestDto): BooleanExpression {
        return (bannerPlaceMapping.place.id.isNull()
            .and(place.isApproved.isTrue())
            .and(adminCustomPlaceRepository.subwayStationCondition(requestDto.subwayStationId))
            .and(
                adminCustomPlaceRepository.categoryGroupCondition(
                    requestDto.placeCategoryGroup
                )
            )
            .and(adminCustomPlaceRepository.placeNameContains(requestDto.placeName))
            .and(regionEq(requestDto.region))
            .and(districtEq(requestDto.district))
            .and(menuNameContains(requestDto.menuName))
                )
    }

    private fun regionEq(region: Region?): BooleanBuilder {
        return Utils.nullSafeBuilder { place.region.eq(region) }
    }

    private fun districtEq(district: District?): BooleanBuilder {
        return Utils.nullSafeBuilder { place.district.eq(district) }
    }

    private fun menuNameContains(name: String?): BooleanExpression? {
        return if (name == null) {
            null
        } else {
            place.id.`in`(
                JPAExpressions
                    .select(menu.place.id)
                    .from(menu)
                    .where(menu.name.contains(name))
            )
        }
    }
}
