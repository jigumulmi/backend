package com.jigumulmi.config.security;

import com.jigumulmi.member.domain.Member;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class HttpTestBasicAuthFilter extends BasicAuthFilter {

    @Value("${swagger.password}")
    private String password;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
        @NonNull HttpServletResponse response,
        @NonNull FilterChain filterChain) throws ServletException, IOException {
        // 컨트롤러에서 HttpSession을 인자로 받으면 request.getSession(true)와 동일하게 세션 없는 경우 생성
        // http 요청에 담긴 세션을 조회하려는 경우 HttpServletRequest의 getSession(false) 사용 (세션 없으면 null 반환)
        try {
            // 테스트가 아닌 클라이언트 서버의 API 호출은 Authorization으로 넘어오는 헤더가 없음
            String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
            boolean isBasicAuth = authHeader != null && authHeader.startsWith("Basic ");
            if (isBasicAuth) {
                String[] credentials = extractAndDecodeHeader(authHeader);
                String passwordFromRequest = credentials[1];
                if (password.equals(passwordFromRequest)) {
                    String username = credentials[0];
                    Long id = Long.valueOf(username);

                    HttpSession session = request.getSession();
                    Member testMember = Member.testMemberBuilder().id(id).build();
                    UserDetailsServiceImpl.setSecurityContextAndSession(testMember, session);
                }
            }
        } catch (Exception e) {
            SecurityContextHolder.clearContext();
        } finally {
            filterChain.doFilter(request, response);
        }
    }
}
