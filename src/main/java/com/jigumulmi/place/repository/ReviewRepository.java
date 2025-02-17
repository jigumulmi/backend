package com.jigumulmi.place.repository;

import com.jigumulmi.member.domain.Member;
import com.jigumulmi.place.domain.Review;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    @EntityGraph(attributePaths = {"reviewImageList"})
    Optional<Review> findByIdAndMember(Long reviewId, Member member);

    List<Review> findTopByPlaceIdAndMemberIdAndDeletedAtIsNull(Long placeId, Long id);
}
