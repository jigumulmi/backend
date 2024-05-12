package com.jigumulmi.admin.dto.response;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
public class MemberResponseDto {

    private LocalDateTime createdAt;
    private Long id;
    private String nickname;
    private Long KakaoUserId;
    private LocalDateTime deregisteredAt;
}
