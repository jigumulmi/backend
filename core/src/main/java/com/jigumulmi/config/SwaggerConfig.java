package com.jigumulmi.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityScheme.Type;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public GroupedOpenApi getAdminApi() {
        return GroupedOpenApi
            .builder()
            .group("admin")
            .pathsToMatch("/admin/**")
            .build();
    }

    @Bean
    public GroupedOpenApi getServiceApi() {
        return GroupedOpenApi
            .builder()
            .group("service")
            .pathsToExclude("/admin/**")
            .build();
    }

    private Info getInfo() {
        return new Info()
            .title("Jigumulmi API")
            .description("""
                지구멀미 백엔드 API
                
                - 우측 상단에서 어드민 | 서비스 API 선택
                - Authorize 혹은 각 API 자물쇠 클릭하여 인증 가능 (전체 적용)
                """)
            ;
    }

    @Bean
    public OpenAPI openAPI() {

        SecurityScheme sessionAuth = new SecurityScheme()
            .type(SecurityScheme.Type.APIKEY).in(SecurityScheme.In.COOKIE).name("JSESSIONID");

        SecurityScheme swaggerAuth = new SecurityScheme()
            .type(Type.HTTP).scheme("basic");

        SecurityRequirement securityRequirement = new SecurityRequirement().addList("basicAuth")
            .addList("swaggerAuth");

        return new OpenAPI()
            .components(new Components().addSecuritySchemes("basicAuth", sessionAuth)
                .addSecuritySchemes("swaggerAuth", swaggerAuth))
            .addSecurityItem(securityRequirement)
            .info(getInfo());
    }
}