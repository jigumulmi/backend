package com.jigumulmi.place.repository;

import com.jigumulmi.place.domain.SubwayStation;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubwayStationRepository extends JpaRepository<SubwayStation, Long> {

    List<SubwayStation> findAllByStationNameStartsWith(String stationName);
}
