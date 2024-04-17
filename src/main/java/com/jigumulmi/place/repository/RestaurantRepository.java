package com.jigumulmi.place.repository;

import com.jigumulmi.place.domain.Restaurant;
import com.jigumulmi.place.domain.SubwayStation;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {
    @EntityGraph(attributePaths = {"subwayStation"})
    List<Restaurant> findAllByIsApprovedTrue();

    @EntityGraph(attributePaths = {"subwayStation"})
    List<Restaurant> findAllBySubwayStationIdAndIsApprovedTrue(@Param("subwayStationId") Long subwayStationId);

    @EntityGraph(attributePaths = {"menuList", "subwayStation"})
    Restaurant findByIdAndIsApprovedTrue(@Param("id") Long restaurantId);


    @Query("select r.subwayStation from Restaurant r where r.id = :id")
    SubwayStation findSubwayStationById(@Param("id") Long placeId);
}
