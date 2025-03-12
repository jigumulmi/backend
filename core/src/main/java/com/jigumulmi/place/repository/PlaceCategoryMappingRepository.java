package com.jigumulmi.place.repository;

import com.jigumulmi.place.domain.PlaceCategoryMapping;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlaceCategoryMappingRepository extends JpaRepository<PlaceCategoryMapping, Long> {

    List<PlaceCategoryMapping> findByPlace_Id(Long placeId);
}
