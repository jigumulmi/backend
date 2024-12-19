package com.jigumulmi.config.logging;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class LoggingInterceptor implements HandlerInterceptor {

    Logger logger = LoggerFactory.getLogger(LoggingInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
        Object handler) {
        String requestId = UUID.randomUUID().toString();
        request.setAttribute("X-Request-ID", requestId);

        long startTime = System.currentTimeMillis();
        request.setAttribute("startTime", startTime);

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
        Object handler, Exception ex) {

        String requestId = (String) request.getAttribute("X-Request-ID");
        response.addHeader("X-Request-ID", requestId);

        long startTime = (Long) request.getAttribute("startTime");
        long responseTime = System.currentTimeMillis() - startTime;
        response.setHeader("X-RESPONSE-TIME", responseTime + "ms");

        String queryString = request.getQueryString();
        int statusCode = response.getStatus();
        HttpStatus httpStatus = HttpStatus.valueOf(statusCode);

        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = securityContext.getAuthentication();
        System.out.println("authentication = " + authentication);

        logger.info("{} {} {} {} {}ms {}", request.getMethod(),
            queryString == null ? request.getRequestURI()
                : request.getRequestURI() + "?" + queryString, statusCode,
            httpStatus.getReasonPhrase(), responseTime, requestId);
    }
}

