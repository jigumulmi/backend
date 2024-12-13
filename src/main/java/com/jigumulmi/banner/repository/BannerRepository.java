package com.jigumulmi.banner.repository;

import com.jigumulmi.banner.domain.Banner;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BannerRepository extends JpaRepository<Banner, Long> {

}
