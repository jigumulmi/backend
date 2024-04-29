package com.jigumulmi.place.repository;

import java.util.Map;

public interface CustomPlaceRepository {

    Map<Integer, Long> getReviewRatingStatsByPlaceId(Long placeId);

    Double getAverageRatingByPlaceId(Long placeId);
}
