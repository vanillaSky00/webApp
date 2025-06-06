package com.vanillasky.imageuploader.controller;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Quick “does the file exist?” endpoint.
 * Example:  GET /test-image/hello.jpg   →  returns the file under ./download/hello.jpg
 *
 * Once this works you know the path is correct; then the static resource handler
 * ( /download/** ) will work too.
 */
//@RestController
public class TestImageController {

    // Change this path if you store images elsewhere
    private final Path downloadDir = Paths.get("download");

    @GetMapping("/test-image/{fileName:.+}")
    public ResponseEntity<Resource> serveImage(@PathVariable String fileName) {

        File file = downloadDir.resolve(fileName).toFile();

        if (!file.exists()) {
            return ResponseEntity
                    .status(404)
                    .body(null);
        }

        // Try to guess MIME type by extension; default to octet-stream
        String contentType = MimeTypeUtils.APPLICATION_OCTET_STREAM_VALUE;
        if (fileName.toLowerCase().endsWith(".png"))  contentType = MediaType.IMAGE_PNG_VALUE;
        if (fileName.toLowerCase().endsWith(".jpg")
                || fileName.toLowerCase().endsWith(".jpeg")) contentType = MediaType.IMAGE_JPEG_VALUE;
        if (fileName.toLowerCase().endsWith(".gif"))  contentType = MediaType.IMAGE_GIF_VALUE;

        Resource body = new FileSystemResource(file);

        return ResponseEntity
                .ok()
                .contentType(MediaType.parseMediaType(contentType))
                .body(body);
    }


    @Bean
    CommandLineRunner dumpMappings(RequestMappingHandlerMapping mappings,
                                   org.springframework.web.servlet.handler.SimpleUrlHandlerMapping resources) {
        return args -> {
            System.out.println("── MVC ENDPOINTS ───────────────");
            mappings.getHandlerMethods().forEach((k,v)-> System.out.println(k));
            System.out.println("── RESOURCE HANDLERS ──────────");
            resources.getHandlerMap().forEach((k,v)-> System.out.println(k + "  →  " + v));
        };
    }

}
