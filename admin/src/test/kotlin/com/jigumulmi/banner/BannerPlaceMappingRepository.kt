package com.jigumulmi.banner

import com.jigumulmi.banner.domain.BannerPlaceMapping
import org.springframework.data.jpa.repository.JpaRepository

interface BannerPlaceMappingRepository : JpaRepository<BannerPlaceMapping?, Long?> {
    fun findAllByBannerId(bannerId: Long?): List<BannerPlaceMapping?>?
}
