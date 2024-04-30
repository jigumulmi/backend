package com.jigumulmi.place.repository;

import com.jigumulmi.member.domain.Member;
import com.jigumulmi.place.domain.ReviewReply;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewReplyRepository extends JpaRepository<ReviewReply, Long> {

    ReviewReply findByIdAndMember(Long reviewReplyId, Member member);
}
