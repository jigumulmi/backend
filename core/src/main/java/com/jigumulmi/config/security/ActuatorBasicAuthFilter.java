package com.jigumulmi.config.security;

import com.jigumulmi.config.exception.CustomException;
import com.jigumulmi.config.exception.errorCode.CommonErrorCode;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

@Component
public class ActuatorBasicAuthFilter extends BasicAuthFilter {

    @Value("${management.endpoints.web.base-path}")
    private String webBasePath;
    @Value("${management.username}")
    private String username;
    @Value("${management.password}")
    private String password;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
        @NonNull HttpServletResponse response,
        @NonNull FilterChain filterChain) throws ServletException, IOException {
        String requestURI = request.getRequestURI();
        if (requestURI.contains(webBasePath) && !requestURI.endsWith("/health")) {
            String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
            boolean isBasicAuth = authHeader != null && authHeader.startsWith("Basic ");
            if (!isBasicAuth) {
                throw new CustomException(CommonErrorCode.UNAUTHORIZED);
            }

            String[] credentials = extractAndDecodeHeader(authHeader);
            String usernameFromRequest = credentials[0];
            String passwordFromRequest = credentials[1];
            if (!password.equals(passwordFromRequest) || !username.equals(
                usernameFromRequest)) {
                throw new CustomException(CommonErrorCode.UNAUTHORIZED);
            }
        }

        filterChain.doFilter(request, response);
    }
}
