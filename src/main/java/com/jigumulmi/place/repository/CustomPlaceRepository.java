package com.jigumulmi.place.repository;

import com.jigumulmi.place.domain.Place;
import com.jigumulmi.place.dto.response.PlaceResponseDto;
import com.jigumulmi.place.dto.response.ReviewResponseDto;
import com.jigumulmi.place.dto.response.ReviewReplyResponseDto;
import java.util.List;
import java.util.Map;

public interface CustomPlaceRepository {

    List<PlaceResponseDto> getPlaceList(Long subwayStationId);

    Place getPlaceDetail(Long placeId);

    Map<Integer, Long> getReviewRatingStatsByPlaceId(Long placeId);

    List<ReviewResponseDto> getReviewListByPlaceId(Long placeId, Long requestMemberId);

    List<ReviewReplyResponseDto> getReviewReplyListByReviewId(Long requestMemberId, Long reviewId);
}
