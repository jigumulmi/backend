package com.jigumulmi.place.dto.response;

import com.jigumulmi.member.dto.response.MemberDetailResponseDto;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReviewResponseDto {

    private String reviewedAt;
    private LocalDateTime deletedAt;
    private Long id;
    private Integer rating;
    private String content;
    private Integer replyCount;
    private Boolean isEditable;
    private MemberDetailResponseDto member;
    private ReactionDto reaction;
    private Boolean isEdited;
    private List<ReviewImageResponseDto> imageList;
}
