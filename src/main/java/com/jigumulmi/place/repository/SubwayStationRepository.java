package com.jigumulmi.place.repository;

import com.jigumulmi.place.domain.SubwayStation;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SubwayStationRepository extends JpaRepository<SubwayStation, Long> {
    List<SubwayStation> findAllByNameStartsWith(String name, Sort sort);
}
