package com.jigumulmi.place.repository

import com.jigumulmi.config.querydsl.Utils
import com.jigumulmi.place.domain.Place
import com.jigumulmi.place.domain.QPlace.place
import com.jigumulmi.place.domain.QPlaceCategoryMapping
import com.jigumulmi.place.domain.QSubwayStationPlace
import com.jigumulmi.place.dto.request.AdminGetPlaceListRequestDto
import com.jigumulmi.place.vo.PlaceCategoryGroup
import com.querydsl.core.BooleanBuilder
import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.core.types.dsl.Expressions
import com.querydsl.jpa.JPAExpressions
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.support.PageableExecutionUtils
import org.springframework.stereotype.Repository


@Repository
class AdminCustomPlaceRepository(
    private val queryFactory: JPAQueryFactory
) {

    fun getPlaceList(pageable: Pageable, requestDto: AdminGetPlaceListRequestDto): Page<Place> {
        val content = queryFactory
            .selectFrom(place)
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
            .where(placeCondition(requestDto))

        return PageableExecutionUtils.getPage(content, pageable) { totalCountQuery.fetchOne()!! }
    }

    private fun placeCondition(requestDto: AdminGetPlaceListRequestDto): BooleanExpression {
        return (place.isFromAdmin.eq(requestDto.isFromAdmin)
            .and(subwayStationCondition(requestDto.subwayStationId))
            .and(categoryGroupCondition(requestDto.categoryGroup))
            .and(placeNameContains(requestDto.placeName))
                )
    }

    fun subwayStationCondition(subwayStationId: Long?): BooleanExpression? {
        return if (subwayStationId == null) {
            null
        } else {
            place.id.`in`(
                JPAExpressions
                    .select(QSubwayStationPlace.subwayStationPlace.place.id)
                    .from(QSubwayStationPlace.subwayStationPlace)
                    .where(
                        QSubwayStationPlace.subwayStationPlace.subwayStation.id.eq(
                            subwayStationId
                        )
                    )
            )
        }
    }

    fun placeNameContains(name: String?): BooleanBuilder {
        return Utils.nullSafeBuilder { place.name.contains(name) }
    }

    fun categoryGroupCondition(categoryGroup: PlaceCategoryGroup?): BooleanExpression? {
        return if (categoryGroup == null) {
            null
        } else {
            place.id.`in`(
                JPAExpressions
                    .select(QPlaceCategoryMapping.placeCategoryMapping.place.id)
                    .from(QPlaceCategoryMapping.placeCategoryMapping)
                    .where(QPlaceCategoryMapping.placeCategoryMapping.categoryGroup.eq(categoryGroup))
            )
        }
    }
}
