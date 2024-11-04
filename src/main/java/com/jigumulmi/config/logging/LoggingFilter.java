//package com.jigumulmi.config.logging;
//
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import lombok.NonNull;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.MediaType;
//import org.springframework.stereotype.Component;
//import org.springframework.util.StreamUtils;
//import org.springframework.web.filter.OncePerRequestFilter;
//import org.springframework.web.util.ContentCachingResponseWrapper;
//
//import java.io.IOException;
//import java.io.InputStream;
//import java.time.Duration;
//import java.time.Instant;
//import java.util.Arrays;
//import java.util.List;
//
//@Slf4j
//@Component
//public class LoggingFilter extends OncePerRequestFilter {
//
//    @Override
//    protected void doFilterInternal(@NonNull HttpServletRequest request,
//        @NonNull HttpServletResponse response,
//        @NonNull FilterChain filterChain)
//        throws ServletException, IOException {
//        if (isAsyncDispatch(request)) {
//            filterChain.doFilter(request, response);
//        } else {
//            doFilterWrapped(new RequestWrapper(request),
//                new ContentCachingResponseWrapper(response), filterChain);
//        }
//    }
//
//    protected void doFilterWrapped(RequestWrapper request, ContentCachingResponseWrapper response,
//        FilterChain filterChain) throws IOException, ServletException {
//        Instant start = Instant.now();
//        try {
//            filterChain.doFilter(request, response);
//            //logRequest(request);
//        } finally {
//            Instant end = Instant.now();
//            long responseTime = Duration.between(start, end).toMillis();
//            response.setHeader("X-RESPONSE-TIME", String.valueOf(responseTime));
//
//            log(request, response, responseTime);
//            //logResponse(response);
//            response.copyBodyToResponse();
//        }
//    }
//
//    private static void log(RequestWrapper request, ContentCachingResponseWrapper response,
//        long responseTime) {
//        String queryString = request.getQueryString();
//
//        int statusCode = response.getStatus();
//        HttpStatus httpStatus = HttpStatus.valueOf(statusCode);
//
//        log.info("{} {} {} {} {}ms ", request.getMethod(),
//            queryString == null ? request.getRequestURI()
//                : request.getRequestURI() + "?" + queryString, statusCode,
//            httpStatus.getReasonPhrase(), responseTime);
//    }
//
//    private static void logRequest(RequestWrapper request) throws IOException {
//        String queryString = request.getQueryString();
//        log.info("Request: {} {}", request.getMethod(),
//            queryString == null ? request.getRequestURI()
//                : request.getRequestURI() + "?" + queryString);
//
//        logPayload("Request", request.getContentType(), request.getInputStream());
//    }
//
//    private static void logResponse(ContentCachingResponseWrapper response) throws IOException {
//        logPayload("Response", response.getContentType(), response.getContentInputStream());
//    }
//
//    private static void logPayload(String prefix, String contentType, InputStream inputStream)
//        throws IOException {
//        boolean visible = isVisible(
//            MediaType.valueOf(contentType == null ? "application/json" : contentType));
//
//        if (visible) {
//            byte[] content = StreamUtils.copyToByteArray(inputStream);
//            if (content.length > 0) {
//                String contentString = new String(content);
//                log.info("{} Payload: {}", prefix, contentString);
//            }
//        } else {
//            log.info("{} Payload: Binary Content", prefix);
//        }
//    }
//
//    private static boolean isVisible(MediaType mediaType) {
//        final List<MediaType> VISIBLE_TYPES = Arrays.asList(
//            MediaType.valueOf("text/*"),
//            MediaType.APPLICATION_FORM_URLENCODED,
//            MediaType.APPLICATION_JSON,
//            MediaType.APPLICATION_XML,
//            MediaType.valueOf("application/*+json"),
//            MediaType.valueOf("application/*+xml"),
//            MediaType.MULTIPART_FORM_DATA
//        );
//
//        return VISIBLE_TYPES.stream()
//            .anyMatch(visibleType -> visibleType.includes(mediaType));
//    }
//}