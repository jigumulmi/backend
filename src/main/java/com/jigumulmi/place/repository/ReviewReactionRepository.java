package com.jigumulmi.place.repository;

import com.jigumulmi.place.domain.ReviewReaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewReactionRepository extends JpaRepository<ReviewReaction, Long> {

    void deleteByIdAndMemberId(Long reviewReactionId, Long memberId);
}
