package com.jigumulmi.place.repository;

import com.jigumulmi.place.domain.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {
    @Query("select r from Restaurant r left join fetch r.menuList m where r.isApproved = true")
    List<Restaurant> findAllByIsApprovedTrue();

    @Query("select r from Restaurant r left join fetch r.menuList m where r.subwayStation.id = :subwayStationId and r.isApproved = true")
    List<Restaurant> findAllBySubwayStationIdAndIsApprovedTrue(@Param("subwayStationId") Long subwayStationId);

}
