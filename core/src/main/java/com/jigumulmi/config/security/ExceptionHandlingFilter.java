package com.jigumulmi.config.security;

import static com.fasterxml.jackson.core.JsonEncoding.UTF8;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jigumulmi.config.exception.CustomException;
import com.jigumulmi.config.exception.errorCode.ErrorCode;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j(topic = "ERROR_LOGGER")
@Component
public class ExceptionHandlingFilter extends OncePerRequestFilter {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
        FilterChain filterChain) throws IOException {
        try {
            filterChain.doFilter(request, response);
        } catch (CustomException ex) {
            handleCustomException(response, ex);
        } catch (Exception ex) {
            log.error("handleFilterException", ex);
        }
    }

    private void handleCustomException(HttpServletResponse response, CustomException ex)
        throws IOException {
        ErrorCode errorCode = ex.getErrorCode();
        String responseBody = objectMapper.writeValueAsString(errorCode);

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(errorCode.getHttpStatus().value());
        response.setCharacterEncoding(UTF8.getJavaName());
        response.getWriter().write(responseBody);
    }
}
