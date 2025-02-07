package com.jigumulmi.place.repository;

import com.jigumulmi.place.domain.Place;
import com.jigumulmi.place.domain.PlaceImage;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

public interface PlaceImageRepository extends JpaRepository<PlaceImage, Long> {

    List<PlaceImage> findByPlace_Id(Long placeId);

    @Transactional
    void deleteAllByPlace(Place place);
}
