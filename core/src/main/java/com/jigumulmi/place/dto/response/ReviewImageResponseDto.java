package com.jigumulmi.place.dto.response;

import com.jigumulmi.place.domain.ReviewImage;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewImageResponseDto {

    private Long id;
    private String s3Key;
    private LocalDateTime createdAt;

    public static ReviewImageResponseDto from(ReviewImage reviewImage) {
        return ReviewImageResponseDto.builder()
            .id(reviewImage.getId())
            .s3Key(reviewImage.getS3Key())
            .createdAt(reviewImage.getCreatedAt())
            .build();
    }
}
