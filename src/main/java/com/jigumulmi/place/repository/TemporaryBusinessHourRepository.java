package com.jigumulmi.place.repository;

import com.jigumulmi.place.domain.TemporaryBusinessHour;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TemporaryBusinessHourRepository extends JpaRepository<TemporaryBusinessHour, Long> {

    List<TemporaryBusinessHour> findAllByPlaceIdAndMonth(Long placeId, Integer month);
}
