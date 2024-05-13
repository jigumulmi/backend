package com.jigumulmi.admin.dto.response;

import com.jigumulmi.member.domain.Member;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MemberListResponseDto {

    @Getter
    @Builder
    public static class MemberDto {

        private LocalDateTime createdAt;
        private Long id;
        private String nickname;
        private Long KakaoUserId;
        private LocalDateTime deregisteredAt;

        public static MemberDto from(Member member) {
            return MemberDto.builder()
                .createdAt(member.getCreatedAt())
                .id(member.getId())
                .nickname(member.getNickname())
                .KakaoUserId(member.getKakaoUserId())
                .deregisteredAt(member.getDeregisteredAt())
                .build();
        }

    }

    private List<MemberDto> memberList;
    private int totalCount;
}
