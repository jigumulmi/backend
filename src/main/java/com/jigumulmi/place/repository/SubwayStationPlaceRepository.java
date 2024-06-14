package com.jigumulmi.place.repository;

import com.jigumulmi.place.domain.SubwayStationPlace;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubwayStationPlaceRepository extends JpaRepository<SubwayStationPlace, Long> {

    void deleteAllByPlaceId(Long placeId);
}
