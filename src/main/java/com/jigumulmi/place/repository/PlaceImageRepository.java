package com.jigumulmi.place.repository;

import com.jigumulmi.place.domain.PlaceImage;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlaceImageRepository extends JpaRepository<PlaceImage, Long> {

    List<PlaceImage> findByPlace_Id(Long placeId);
}
