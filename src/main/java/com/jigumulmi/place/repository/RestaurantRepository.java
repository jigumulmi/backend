package com.jigumulmi.place.repository;

import com.jigumulmi.place.domain.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {

    List<Restaurant> findAllBySubwayStationIdAndIsApprovedTrue(Long subwayStationId);
}
