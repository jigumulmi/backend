package com.jigumulmi.place.repository;

import com.jigumulmi.place.domain.Place;
import java.util.Optional;
import lombok.NonNull;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlaceRepository extends JpaRepository<Place, Long> {

    //두 컬렉션 이상에 동시 left join 불가 (첫 번째 것만 조인됨)
    @Override
    @NonNull
    @EntityGraph(attributePaths = {"categoryMappingList"})
    Optional<Place> findById(@NonNull Long placeId);
}
