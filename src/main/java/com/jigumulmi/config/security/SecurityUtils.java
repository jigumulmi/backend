package com.jigumulmi.config.security;

import com.jigumulmi.config.exception.CustomException;
import com.jigumulmi.config.exception.errorCode.CommonErrorCode;

public class SecurityUtils {

    public static Object checkAuthenticationPrincipal(Object principal) {
        // 인증되지 않은 유저가 요청한 경우 스프링 시큐리티는 principal에 "anonymousUser"를 담아 보낸다
        // @AuthenticationPrincipal의 UserDetails은 문자열이 아니므로 null을 반환한다
        if ("anonymousUser".equals(principal)) {
            throw new CustomException(CommonErrorCode.FORBIDDEN);
        }
        return principal;
    }
}
