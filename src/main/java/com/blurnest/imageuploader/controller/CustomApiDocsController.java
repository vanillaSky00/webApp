package com.blurnest.imageuploader.controller;

import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Controller
public class CustomApiDocsController {
    @GetMapping(value = "/v3/api-docs", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> customApiDocsRoot() {
        ClassPathResource jsonFile = new ClassPathResource("static/openapi.json");
//        System.out.println("Resource exists: " + jsonFile.exists());
//        System.out.println("File path: " + jsonFile.getPath());
        try {
            String json = StreamUtils.copyToString(jsonFile.getInputStream(), StandardCharsets.UTF_8);
            return ResponseEntity.ok(json);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
