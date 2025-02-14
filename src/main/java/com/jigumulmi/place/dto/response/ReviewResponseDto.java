package com.jigumulmi.place.dto.response;

import com.jigumulmi.member.domain.Member;
import com.jigumulmi.member.dto.response.MemberDetailResponseDto;
import com.jigumulmi.place.domain.Review;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Builder
public class ReviewResponseDto {

    static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");

    private String reviewedAt;
    private LocalDateTime deletedAt;
    private Long id;
    private Integer rating;
    private String content;
    private Boolean isEditable;
    private Boolean isEdited;
    private MemberDetailResponseDto member;
    private List<ReviewImageResponseDto> imageList;
    @Setter
    private Long replyCount;

    public static ReviewResponseDto from(Review review, Member requestMember) {
        Member reviewAuthor = review.getMember();

        return ReviewResponseDto.builder()
            .reviewedAt(review.getModifiedAt().format(formatter))
            .deletedAt(review.getDeletedAt())
            .id(review.getId())
            .rating(review.getRating())
            .content(review.getContent())
            .isEditable(reviewAuthor.equals(requestMember))
            .member(MemberDetailResponseDto.from(reviewAuthor))
            .isEdited(review.getModifiedAt().isAfter(review.getCreatedAt()))
            .imageList(
                review.getReviewImageList().stream().map(ReviewImageResponseDto::from).toList())
            .build();
    }
}
