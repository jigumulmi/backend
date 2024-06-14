package com.jigumulmi.place.repository;

import com.jigumulmi.place.domain.Place;
import java.util.Optional;
import lombok.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlaceRepository extends JpaRepository<Place, Long> {

    @Override
    @NonNull
    @EntityGraph(attributePaths = {"menuList", "subwayStationPlaceList"})
    Optional<Place> findById(@NonNull Long placeId);

    @Override
    @NonNull
    @EntityGraph(attributePaths = {"subwayStationPlaceList"})
    Page<Place> findAll(@NonNull Pageable pageable);
}
