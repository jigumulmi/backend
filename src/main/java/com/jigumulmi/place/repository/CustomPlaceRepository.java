package com.jigumulmi.place.repository;

import com.jigumulmi.place.dto.response.OverallReviewResponseDto.ReviewRatingStatsDto;
import java.util.List;

public interface CustomPlaceRepository {

    List<ReviewRatingStatsDto> getReviewRatingStatsByPlaceId(Long placeId);

    Double getAverageRatingByPlaceId(Long placeId);
}
