package com.jigumulmi.place.repository;

import com.jigumulmi.member.domain.Member;
import com.jigumulmi.place.domain.Review;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    Long countByRestaurantId(Long placeId);

    Review findByIdAndMember(Long reviewId, Member member);
}
