package com.jigumulmi.place.repository;

import com.jigumulmi.place.domain.Restaurant;
import com.jigumulmi.place.dto.response.RestaurantResponseDto;
import com.jigumulmi.place.dto.response.ReviewListResponseDto;
import com.jigumulmi.place.dto.response.ReviewReplyResponseDto;
import java.util.List;
import java.util.Map;

public interface CustomPlaceRepository {

    List<RestaurantResponseDto> getRestaurantList(Long placeId, Long subwayStationId);

    Restaurant getRestaurantDetail(Long placeId);

    Map<Integer, Long> getReviewRatingStatsByPlaceId(Long placeId);

    Double getAverageRatingByPlaceId(Long placeId);

    List<ReviewListResponseDto> getReviewListByPlaceId(Long placeId, Long requestMemberId);

    List<ReviewReplyResponseDto> getReviewReplyListByReviewId(Long requestMemberId, Long reviewId);
}
