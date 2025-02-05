package com.jigumulmi.place.dto.response;

import java.util.Map;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ReviewStatisticsResponseDto {

    private Double averageRating;
    private Long totalCount;
    private Map<Integer, Long> statistics; // {별점: 개수}
}
