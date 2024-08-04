package com.jigumulmi.admin.dto.response;

import com.jigumulmi.member.domain.Member;
import com.jigumulmi.member.dto.response.MemberDetailResponseDto;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@Builder
public class AdminMemberListResponseDto {

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

    private PageDto page;
    private List<MemberDto> data;
}
