package com.taebong.szs.common.config.swagger;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .components(new Components())
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
                .title("삼쩜삼 개발 과제 테스트 API 문서")
                .description("SZS");
    }
}