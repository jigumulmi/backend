package com.jigumulmi.place.repository;

import com.jigumulmi.place.dto.response.ReviewListResponseDto;
import java.util.List;
import java.util.Map;

public interface CustomPlaceRepository {

    Map<Integer, Long> getReviewRatingStatsByPlaceId(Long placeId);

    Double getAverageRatingByPlaceId(Long placeId);

    List<ReviewListResponseDto> getReviewListByPlaceId(Long placeId, Long requestMember);
}
