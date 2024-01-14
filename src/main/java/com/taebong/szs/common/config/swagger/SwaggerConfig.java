package com.taebong.szs.common.config.swagger;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI openAPI() {
        SecurityScheme bearerAuth = new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("Authorization")
                .in(SecurityScheme.In.HEADER)
                .name(HttpHeaders.AUTHORIZATION);

        SecurityRequirement addSecurityItem = new SecurityRequirement();
        addSecurityItem.addList("Authorization");

        return new OpenAPI()
                .components(new Components().addSecuritySchemes("Authorization", bearerAuth))
                .addSecurityItem(addSecurityItem)
                .info(getInfo());
    }

    @Bean
    public GroupedOpenApi getUserApi() {
        return GroupedOpenApi
                .builder()
                .group("user")
                .pathsToMatch("/szs/**")
                .build();
    }

    private Info getInfo() {
        return new Info()
                .version("v1.0.0")
                .title("SZS 개발 과제 테스트 API 문서")
                .description("SZS");
    }
}