package com.jigumulmi.place.repository;

import com.jigumulmi.member.domain.Member;
import com.jigumulmi.place.domain.Place;
import com.jigumulmi.place.domain.PlaceLike;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlaceLikeRepository extends JpaRepository<PlaceLike, Long> {

    void deleteByPlaceAndMember(Place place, Member member);
}
