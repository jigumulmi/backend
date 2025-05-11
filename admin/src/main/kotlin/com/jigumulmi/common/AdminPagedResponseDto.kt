package com.jigumulmi.common

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

open class AdminPagedResponseDto<T>(
    open val page: PageDto,
    open val data: List<T>
) {
    data class PageDto(
        val totalCount: Long,
        val currentPage: Int,
        val totalPage: Int
    ) {
        companion object {
            fun of(page: Page<*>, pageable: Pageable): PageDto {
                return PageDto(
                    totalCount = page.totalElements,
                    currentPage = pageable.pageNumber + 1,
                    totalPage = page.totalPages
                )
            }
        }
    }

    companion object {
        fun <T> of(pageData: Page<T>, pageable: Pageable): AdminPagedResponseDto<T> {
            return AdminPagedResponseDto(
                page = PageDto.of(pageData, pageable),
                data = pageData.content
            )
        }
    }
}

