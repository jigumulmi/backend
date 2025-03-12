package com.jigumulmi.member.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class KakaoAuthResponseDto {

    private boolean hasRegistered;
    private String nickname;
}
