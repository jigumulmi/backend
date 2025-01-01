package com.jigumulmi.config.security;

import static com.fasterxml.jackson.core.JsonEncoding.UTF8;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jigumulmi.config.exception.errorCode.CommonErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.access.AccessDeniedHandler;

@Slf4j
public class AccessDeniedHandlerImpl implements AccessDeniedHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void handle(
        HttpServletRequest request, HttpServletResponse response,
        AccessDeniedException accessDeniedException
    ) throws IOException {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = securityContext.getAuthentication();

        log.error(authentication.getPrincipal().toString());

        String responseBody = objectMapper.writeValueAsString(CommonErrorCode.FORBIDDEN);

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setCharacterEncoding(UTF8.getJavaName());
        response.getWriter().write(responseBody);
    }
}
