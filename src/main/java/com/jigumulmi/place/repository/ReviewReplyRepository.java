package com.jigumulmi.place.repository;

import com.jigumulmi.member.domain.Member;
import com.jigumulmi.place.domain.Review;
import com.jigumulmi.place.domain.ReviewReply;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewReplyRepository extends JpaRepository<ReviewReply, Long> {

    @EntityGraph(attributePaths = {"review"})
    ReviewReply findByIdAndMember(Long reviewReplyId, Member member);

    boolean existsByReview(Review review);

    long countByReview(Review review);
}
