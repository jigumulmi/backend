package com.jigumulmi.banner.dto.response

import com.jigumulmi.banner.domain.Banner
import java.time.LocalDateTime

data class AdminBannerResponseDto(
    val modifiedAt: LocalDateTime,
    val id: Long,
    val title: String? = null,
    val isActive: Boolean? = false,
    ) {

    companion object {
        fun from(banner: Banner): AdminBannerResponseDto {
            return AdminBannerResponseDto(
                id = banner.id,
                title = banner.title,
                isActive = banner.isActive,
                modifiedAt = banner.modifiedAt,
            )
        }
    }
}
