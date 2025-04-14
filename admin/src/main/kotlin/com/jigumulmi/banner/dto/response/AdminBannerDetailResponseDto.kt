package com.jigumulmi.banner.dto.response

import com.jigumulmi.banner.domain.Banner
import java.time.LocalDateTime

class AdminBannerDetailResponseDto(
    val createdAt: LocalDateTime,
    val modifiedAt: LocalDateTime,
    val id: Long,
    val title: String? = null,
    val isActive: Boolean? = false,
    val outerImageS3Key: String? = null,
    val innerImageS3Key: String? = null
    ) {

    companion object {
        fun from(banner: Banner): AdminBannerDetailResponseDto {
            return AdminBannerDetailResponseDto(
                createdAt = banner.createdAt,
                modifiedAt = banner.modifiedAt,
                id = banner.id,
                title = banner.title,
                isActive = banner.isActive,
                outerImageS3Key = banner.outerImageS3Key,
                innerImageS3Key = banner.innerImageS3Key,
            )
        }
    }
}
