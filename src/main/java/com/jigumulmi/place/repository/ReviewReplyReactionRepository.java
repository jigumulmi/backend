package com.jigumulmi.place.repository;

import com.jigumulmi.place.domain.ReviewReplyReaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewReplyReactionRepository extends JpaRepository<ReviewReplyReaction, Long> {

    void deleteByIdAndMemberId(Long reviewReplyReactionId, Long memberId);

}
