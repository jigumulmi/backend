package com.jigumulmi.place.dto.response;

import com.jigumulmi.member.dto.response.MemberDetailResponseDto;
import lombok.Getter;

@Getter
public class ReviewReplyResponseDto {

    private String repliedAt;
    private Long id;
    private String content;
    private Boolean isEditable;
    private MemberDetailResponseDto member;
    private Boolean isEdited;
}
