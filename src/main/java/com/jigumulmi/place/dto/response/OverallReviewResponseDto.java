package com.jigumulmi.place.dto.response;

import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class OverallReviewResponseDto {

    @Getter
    //Builder 있으면 에러 발생
    public static class ReviewRatingStatsDto {

        private int rating;
        private long count;
    }

    private Double averageRating;
    private Long reviewCount;
    private List<ReviewRatingStatsDto> statistics;

}
