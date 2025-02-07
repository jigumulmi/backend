package com.jigumulmi.place.dto.response;

import static java.lang.Math.round;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Map;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ReviewStatisticsResponseDto {

    @Getter
    @Builder
    public static class RatingStatistics {

        @JsonProperty("1")
        private int one;
        @JsonProperty("2")
        private int two;
        @JsonProperty("3")
        private int three;
        @JsonProperty("4")
        private int four;
        @JsonProperty("5")
        private int five;

        private static RatingStatistics fromReviewRatingStatMap(
            Map<Integer, Long> reviewRatingStatMap) {
            return RatingStatistics.builder()
                .one(reviewRatingStatMap.getOrDefault(1, 0L).intValue())
                .two(reviewRatingStatMap.getOrDefault(2, 0L).intValue())
                .three(reviewRatingStatMap.getOrDefault(3, 0L).intValue())
                .four(reviewRatingStatMap.getOrDefault(4, 0L).intValue())
                .five(reviewRatingStatMap.getOrDefault(5, 0L).intValue())
                .build();
        }
    }

    private Double averageRating;
    private int totalCount;
    private RatingStatistics statistics;

    public static ReviewStatisticsResponseDto fromReviewRatingStatMap(
        Map<Integer, Long> reviewRatingStatMap) {
        int totalCount = 0;
        int totalRating = 0;
        for (int rate = 1; rate <= 5; rate++) {
            int count = reviewRatingStatMap.getOrDefault(rate, 0L).intValue();
            totalCount += count;
            totalRating += (count * rate);
        }
        Double averageRating = round((float) totalRating / totalCount * 100) / 100.0; // 소수점 둘째자리까지

        return ReviewStatisticsResponseDto.builder()
            .averageRating(averageRating)
            .totalCount(totalCount)
            .statistics(RatingStatistics.fromReviewRatingStatMap(reviewRatingStatMap))
            .build();
    }
}
