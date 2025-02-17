package com.jigumulmi.config.security;

import com.jigumulmi.config.exception.CustomException;
import com.jigumulmi.config.exception.errorCode.CommonErrorCode;
import com.jigumulmi.member.domain.Member;

public class SecurityUtils {

    public static final String ANONYMOUS_USER = "anonymousUser";

    public static Member checkRequiredAuthenticationPrincipal(Object principal) {
        // 인증되지 않은 유저가 요청한 경우 스프링 시큐리티는 principal에 "anonymousUser"를 담아 보낸다
        // @AuthenticationPrincipal의 UserDetails은 문자열이 아니므로 null을 반환한다
        if (ANONYMOUS_USER.equals(principal)) {
            throw new CustomException(CommonErrorCode.UNAUTHORIZED);
        } else {
            UserDetailsImpl userDetails = (UserDetailsImpl) principal;
            return userDetails.getMember();
        }
    }

    public static Member checkOptionalAuthenticationPrincipal(Object principal) {
        if (ANONYMOUS_USER.equals(principal)) {
            return Member.guestMemberBuilder().id(-1).build();
        } else {
            UserDetailsImpl userDetails = (UserDetailsImpl) principal;
            return userDetails.getMember();
        }
    }
}
