package com.jigumulmi.config.logging;

import com.jigumulmi.config.security.UserDetailsImpl;
import com.jigumulmi.member.domain.Member;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j(topic = "ACCESS_LOGGER")
@Component
public class LoggingInterceptor implements HandlerInterceptor {

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

        String url = queryString == null ? request.getRequestURI()
            : request.getRequestURI() + "?" + queryString;
        String decodedUrl = URLDecoder.decode(url, StandardCharsets.UTF_8);

        Member requestMember = getRequestMember();
        if (requestMember != null) {
            log.info("[{}] {} {} {} {} {}ms BY member:id:{}:isAdmin:{}", requestId, request.getMethod(),
                decodedUrl, statusCode, httpStatus.getReasonPhrase(), responseTime,
                requestMember.getId(), requestMember.getIsAdmin());
        } else {
            log.info("[{}] {} {} {} {} {}ms", requestId, request.getMethod(),
                decodedUrl, statusCode, httpStatus.getReasonPhrase(), responseTime);
        }

    }

    private static Member getRequestMember() {
        try {
            SecurityContext securityContext = SecurityContextHolder.getContext();
            Authentication authentication = securityContext.getAuthentication();
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            return userDetails.getMember();
        } catch (Exception e) {
            return null;
        }
    }
}

