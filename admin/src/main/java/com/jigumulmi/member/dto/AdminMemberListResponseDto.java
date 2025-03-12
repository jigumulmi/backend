package com.jigumulmi.member.dto;

import com.jigumulmi.common.PagedResponseDto;
import com.jigumulmi.member.domain.Member;
import com.jigumulmi.member.dto.AdminMemberListResponseDto.MemberDto;
import com.jigumulmi.member.dto.response.MemberDetailResponseDto;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class AdminMemberListResponseDto extends PagedResponseDto<MemberDto> {

    @Getter
    @SuperBuilder
    public static class MemberDto extends MemberDetailResponseDto {

        private LocalDateTime modifiedAt;
        private Long KakaoUserId;

        public static MemberDto from(Member member) {
            return MemberDto.builder()
                .createdAt(member.getCreatedAt())
                .modifiedAt(member.getModifiedAt())
                .id(member.getId())
                .nickname(member.getNickname())
                .email(member.getEmail())
                .KakaoUserId(member.getKakaoUserId())
                .deregisteredAt(member.getDeregisteredAt())
                .isAdmin(member.getIsAdmin())
                .build();
        }

    }
}
