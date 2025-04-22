package com.guardians.config;

import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public GroupedOpenApi ctfGroupedOpenApi() {
        return GroupedOpenApi
                .builder()
                .group("ctf")
                .pathsToMatch("/api/**")
                .addOpenApiCustomizer(
                        openApi -> openApi.setInfo(
                                new Info()
                                        .title("Guardians API")
                                        .description("워게임 트레이닝 서비스 플랫폼 'Guardians' REST API 문서입니다.")
                                        .version("1.0.0")
                        )
                )
                .build();
    }
}
