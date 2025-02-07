package com.jigumulmi.member.dto.response;

import com.jigumulmi.member.domain.Member;
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

    public static MemberDetailResponseDto from(Member member) {
        return MemberDetailResponseDto.builder()
            .createdAt(member.getCreatedAt())
            .deregisteredAt(member.getDeregisteredAt())
            .email(member.getEmail())
            .isAdmin(member.getIsAdmin())
            .build();
    }
}
