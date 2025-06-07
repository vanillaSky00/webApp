package com.vanillasky.imageuploader.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Paths;

//now for relative path file:download/*
@Configuration
public class DownloadResourceConfig implements WebMvcConfigurer {

//    public DownloadResourceConfig() {
//        System.out.println("✅ WebConfig loaded");d
//    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

//        System.out.println("✅ Adding /download/** resource handler");
//        System.out.println("✅ JVM working dir: " + System.getProperty("user.dir"));
//        String absPath = Paths.get("download").toAbsolutePath().toUri().toString();
//        System.out.println("✅ Serving from: " + absPath);

        registry.addResourceHandler("/download/**")//an endpoint
                //.addResourceLocations("file:download/")
                .addResourceLocations("file:download/")
                .resourceChain(true);//what is this?
    }
}
