package com.jigumulmi.place.repository;

import com.jigumulmi.place.domain.Menu;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MenuRepository extends JpaRepository<Menu, Long> {

    List<Menu> findAllByPlaceId(Long placeId);
}
