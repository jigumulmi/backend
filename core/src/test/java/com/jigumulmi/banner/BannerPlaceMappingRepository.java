package com.jigumulmi.banner;

import com.jigumulmi.banner.domain.BannerPlaceMapping;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BannerPlaceMappingRepository extends JpaRepository<BannerPlaceMapping, Long> {
}
