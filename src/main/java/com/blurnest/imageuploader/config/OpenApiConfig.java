package com.blurnest.imageuploader.config;

import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(name = "springdoc.api-docs.enabled", matchIfMissing = true)
public class OpenApiConfig {

    @Bean
    public GroupedOpenApi processOnlyApi() {
        return GroupedOpenApi.builder()
                .group("process-api")
                .pathsToMatch("/api/images/process/**") // registry api to doc, Ant style, matches future sub-paths too
                .addOpenApiCustomizer(openApiCustomizer())
                .build();
    }

    //Custom message shown on doc
    private OpenApiCustomizer openApiCustomizer() {
        return openApi -> openApi.info(new Info()
                .title("Image Process API")
                .description("Only /api/images/process is documented")
                .version("1.0.0"));
    }
}
