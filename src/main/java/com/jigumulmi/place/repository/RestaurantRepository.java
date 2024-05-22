package com.jigumulmi.place.repository;

import com.jigumulmi.place.domain.Restaurant;
import java.util.Optional;
import lombok.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {

    @Override
    @NonNull
    @EntityGraph(attributePaths = {"menuList", "subwayStationPlaceList"})
    Optional<Restaurant> findById(@NonNull Long placeId);

    @Override
    @NonNull
    @EntityGraph(attributePaths = {"subwayStationPlaceList"})
    Page<Restaurant> findAll(@NonNull Pageable pageable);
}
