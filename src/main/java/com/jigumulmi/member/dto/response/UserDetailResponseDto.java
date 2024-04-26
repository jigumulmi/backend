package com.jigumulmi.member.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class UserDetailResponseDto {

    private LocalDateTime createdAt;
    private Long id;
    private String nickname;
    private String email;
}
