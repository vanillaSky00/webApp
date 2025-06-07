package com.blurnest.imageuploader.config;// CORSConfig.java
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CORSConfig implements WebMvcConfigurer {

    //inject from YAML file
    @Value("${app.cors.origins}")
    private String[] allowOrigins;

    @PostConstruct
    public void init() {
        System.out.println("My test value: " + allowOrigins[0]);
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins(allowOrigins)
                .allowedMethods("GET", "POST", "OPTIONS")
                .allowedHeaders("Content-Type", "Authorization")
                .maxAge(3600);
        //.allowCredentials(true) //only if you really using cookies

        //add more
        registry.addMapping("/v3/api-docs")
                .allowedOrigins(allowOrigins)
                .allowedMethods("GET")
                .allowedHeaders("Content-Type", "Authorization", "Accept");
    }
}

//http://localhost:5173 is hardcoded for local development
