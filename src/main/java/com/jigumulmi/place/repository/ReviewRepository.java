package com.jigumulmi.place.repository;

import com.jigumulmi.place.domain.Review;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, Long>, CustomPlaceRepository {

}
