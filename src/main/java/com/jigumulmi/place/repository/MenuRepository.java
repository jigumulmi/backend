package com.jigumulmi.place.repository;

import com.jigumulmi.place.domain.Menu;
import com.jigumulmi.place.domain.Place;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MenuRepository extends JpaRepository<Menu, Long> {

    List<Menu> findAllByPlaceId(Long placeId);
    Page<Menu> findAllByPlaceId(Long placeId, Pageable pageable);

    void deleteAllByPlace(Place place);
}
