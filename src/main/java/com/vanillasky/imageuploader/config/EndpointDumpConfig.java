package com.vanillasky.imageuploader.config;

import jakarta.annotation.Nullable;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.handler.SimpleUrlHandlerMapping;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

@Configuration
public class EndpointDumpConfig {

    @Bean
    CommandLineRunner dumpMappings(
            ApplicationContext ctx,
            RequestMappingHandlerMapping mvc) {

        return args -> {
            System.out.println("\n── MVC ENDPOINTS ───────────");
            mvc.getHandlerMethods().forEach((info, mh) -> System.out.println(info));

            System.out.println("\n── RESOURCE HANDLERS ───────");
            ctx.getBeansOfType(SimpleUrlHandlerMapping.class).values()
                    .forEach(mapping -> mapping.getHandlerMap()
                            .forEach((pat, handler) ->
                                    System.out.println(pat + "  →  " + handler)));
        };
    }
}
