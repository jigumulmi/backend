package com.jigumulmi.config.logging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jigumulmi.config.logging.LoggingVO.LoggingVOBuilder;
import com.jigumulmi.config.security.UserDetailsImpl;
import com.jigumulmi.member.domain.Member;
import com.jigumulmi.member.vo.MemberRole;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j(topic = "ACCESS_LOGGER")
@Component
public class LoggingInterceptor implements HandlerInterceptor {

    private final ObjectMapper objectMapper = new ObjectMapper();

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
        Object handler, Exception ex) throws JsonProcessingException {

        String requestId = (String) request.getAttribute("X-Request-ID");
        response.addHeader("X-Request-ID", requestId);

        long startTime = (Long) request.getAttribute("startTime");
        long responseTime = System.currentTimeMillis() - startTime;
        response.setHeader("X-RESPONSE-TIME", responseTime + "ms");

        String queryString = request.getQueryString();
        String decodedQueryString =
            queryString == null ? null : URLDecoder.decode(queryString, StandardCharsets.UTF_8);

        LoggingVOBuilder loggingVOBuilder = LoggingVO.builder()
            .requestId(requestId)
            .requestMethod(request.getMethod())
            .requestUri(request.getRequestURI())
            .requestQueryParam(decodedQueryString)
            .httpStatusCode(response.getStatus())
            .responseTime(responseTime);

        LoggingVO loggingVO;
        Member requestMember = getRequestMember();
        if (requestMember == null) {
            loggingVO = loggingVOBuilder.build();
        } else {
            loggingVO = loggingVOBuilder
                .memberId(requestMember.getId())
                .memberRole(MemberRole.getRole(requestMember.getIsAdmin()))
                .build();
        }

        log.info(objectMapper.writeValueAsString(loggingVO));

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

