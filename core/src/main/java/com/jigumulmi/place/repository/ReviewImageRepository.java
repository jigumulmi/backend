package com.jigumulmi.place.repository;

import com.jigumulmi.place.domain.ReviewImage;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewImageRepository extends JpaRepository<ReviewImage, Long> {

    List<ReviewImage> findAllByReview_Place_IdOrderByCreatedAtDesc(Long placeId);
    Page<ReviewImage> findAllByReview_Place_IdOrderByCreatedAtDesc(Long placeId, Pageable pageable);
}
