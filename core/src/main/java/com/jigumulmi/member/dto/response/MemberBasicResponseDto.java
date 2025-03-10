package com.jigumulmi.member.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@NoArgsConstructor
@SuperBuilder
public class MemberBasicResponseDto {

    private Long id;
    private String nickname;
}
