package com.vanillasky.imageuploader;

import com.vanillasky.imageuploader.controller.ImageController;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan(basePackages = "com.vanillasky.imageuploader.entity")
public class Application {

    private ImageController imageController;

    public static void main(String[] args) {
            SpringApplication.run(Application.class, args);
            System.out.println("Application started successfully!");
    }
}
