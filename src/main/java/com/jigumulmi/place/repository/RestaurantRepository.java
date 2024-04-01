package com.jigumulmi.place.repository;

import com.jigumulmi.place.domain.Restaurant;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {
    @EntityGraph(attributePaths = {"menuList"})
    List<Restaurant> findAllByIsApprovedTrue();

    @EntityGraph(attributePaths = {"menuList"})
    List<Restaurant> findAllBySubwayStationIdAndIsApprovedTrue(@Param("subwayStationId") Long subwayStationId);

}
