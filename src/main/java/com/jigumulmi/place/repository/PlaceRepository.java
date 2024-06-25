package com.jigumulmi.place.repository;

import com.jigumulmi.place.domain.Place;
import java.util.Optional;
import lombok.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlaceRepository extends JpaRepository<Place, Long> {

    // TODO 두 컬렉션에 동시 left join 안되므로 querydsl로 재작성
    @Override
    @NonNull
    @EntityGraph(attributePaths = {"menuList", "subwayStationPlaceList"})
    Optional<Place> findById(@NonNull Long placeId);

    @Override
    @NonNull
    @EntityGraph(attributePaths = {"subwayStationPlaceList"})
    Page<Place> findAll(@NonNull Pageable pageable);
}
