package com.jigumulmi.config.swagger;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class SwaggerBasicAuthFilter extends OncePerRequestFilter {

    @Value("${swagger.username}")
    private String swaggerUsername;
    @Value("${swagger.password}")
    private String swaggerPassword;
    @Value("${springdoc.api-docs.path}")
    private String apiDocsPath;
    @Value("${springdoc.swagger-ui.path}")
    private String swaggerUIPath;
    @Value("${server.servlet.context-path}")
    private String servletContextPath;

    @Override
    public void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
        FilterChain filterChain)
        throws ServletException, IOException {

        if (isFromSwagger(request)) {
            String authHeader = request.getHeader("Authorization");

            if (authHeader != null && authHeader.startsWith("Basic ")) {
                String[] credentials = extractAndDecodeHeader(authHeader);

                if (isAuthorized(credentials[0], credentials[1])) {
                    filterChain.doFilter(request, response);
                    return;
                }
            }

            response.setHeader("WWW-Authenticate", "Basic realm=\"Swagger\"");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        filterChain.doFilter(request, response);

    }

    private boolean isFromSwagger(HttpServletRequest request) {
        String contextPath = request.getContextPath();
        String requestURI = request.getRequestURI();
        return requestURI.startsWith(contextPath + "/swagger-ui");
    }

    private boolean isAuthorized(String username, String password) {
        return username.equals(swaggerUsername) && password.equals(swaggerPassword);
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
