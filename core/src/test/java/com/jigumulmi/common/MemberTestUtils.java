package com.jigumulmi.common;

import com.jigumulmi.config.security.UserDetailsImpl;
import com.jigumulmi.member.domain.Member;
import java.time.LocalDateTime;
import org.springframework.security.core.Authentication;
import org.springframework.security.test.context.TestSecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;

public class MemberTestUtils {

    public static final LocalDateTime DEFAULT_CREATED_AT = LocalDateTime.of(2024, 3, 29, 0, 0, 0);

    public static Member getMember(Long memberId, Boolean isAdmin) {
        Member member = Member.builder()
            .nickname("testNickname")
            .email("test@email.com")
            .kakaoUserId(123L)
            .isAdmin(isAdmin)
            .deregisteredAt(null)
            .build();

        ReflectionTestUtils.setField(member, "id", memberId);
        ReflectionTestUtils.setField(member, "createdAt", DEFAULT_CREATED_AT);
        return member;
    }

    public static Member getMemberFromSecurityContext() {
        Authentication authentication = TestSecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl principal = (UserDetailsImpl) authentication.getPrincipal();
        return principal.getMember();
    }

}
