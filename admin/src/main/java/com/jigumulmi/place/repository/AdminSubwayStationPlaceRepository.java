package com.jigumulmi.place.repository;

import com.jigumulmi.place.domain.Place;
import com.jigumulmi.place.domain.SubwayStationPlace;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminSubwayStationPlaceRepository extends JpaRepository<SubwayStationPlace, Long> {

    void deleteAllByPlace(Place place);
}
