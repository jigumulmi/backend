package com.jigumulmi.config.swagger;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityScheme.Type;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {

        Info info = new Info()
            .title("Jigumulmi API")
            .description("지구멀미")
            .version("0.1.0");

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
            .info(info);
    }
}