package com.jigumulmi.config.swagger;

import com.jigumulmi.config.security.UserDetailsServiceImpl;
import com.jigumulmi.member.domain.Member;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class SwaggerBasicAuthFilter extends OncePerRequestFilter {

    @Value("${swagger.password}")
    private String swaggerPassword;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
        @NonNull HttpServletResponse response,
        @NonNull FilterChain filterChain) throws ServletException, IOException {
        // 컨트롤러에서 HttpSession을 인자로 받으면 request.getSession(true)와 동일하게 세션 없는 경우 생성
        // http 요청에 담긴 세션을 조회하려는 경우 HttpServletRequest의 getSession(false) 사용 (세션 없으면 null 반환)
        try {
            String referer = request.getHeader("Referer");
            boolean isFromSwagger = referer.endsWith("/swagger-ui/index.html");

            String authHeader = request.getHeader("Authorization");
            if (authHeader != null && authHeader.startsWith("Basic ") && isFromSwagger) {
                String[] credentials = extractAndDecodeHeader(authHeader);
                String password = credentials[1];
                if (password.equals(swaggerPassword)) {
                    String username = credentials[0];
                    Long id = Long.valueOf(username);

                    HttpSession session = request.getSession();
                    Member swaggerMember = new Member(id, "swagger");
                    UserDetailsServiceImpl.setSecurityContextAndSession(swaggerMember, session);
                }
            }
        } catch (Exception e) {
            SecurityContextHolder.clearContext();
        } finally {
            filterChain.doFilter(request, response);
        }

    }

    private String[] extractAndDecodeHeader(String header) throws IOException {
        byte[] base64Token = header.substring(6).getBytes(StandardCharsets.UTF_8);
        byte[] decoded;
        try {
            decoded = Base64.getDecoder().decode(base64Token);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Failed to decode basic authentication token");
        }

        String token = new String(decoded, StandardCharsets.UTF_8);

        int delim = token.indexOf(":");

        if (delim == -1) {
            throw new IOException("Invalid basic authentication token");
        }

        return new String[]{token.substring(0, delim), token.substring(delim + 1)};
    }
}
