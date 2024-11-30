package com.jigumulmi.member.dto.response;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class MemberDetailResponseDto extends MemberBasicResponseDto{

    private LocalDateTime createdAt;
    private LocalDateTime deregisteredAt;
    private String email;
    private Boolean isAdmin;
}
