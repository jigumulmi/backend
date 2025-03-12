package com.jigumulmi.place.repository;

import com.jigumulmi.member.domain.Member;
import com.jigumulmi.place.domain.PlaceLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

public interface PlaceLikeRepository extends JpaRepository<PlaceLike, Long> {

    @Transactional
    void deleteByPlace_IdAndMember(Long placeId, Member member);
}
