package com.jigumulmi.place.repository;

import com.jigumulmi.place.domain.FixedBusinessHour;
import com.jigumulmi.place.domain.Place;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

public interface FixedBusinessHourRepository extends JpaRepository<FixedBusinessHour, Long> {

    @Transactional
    void deleteAllByPlace(Place place);
}
