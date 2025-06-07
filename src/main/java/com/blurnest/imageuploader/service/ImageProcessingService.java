package com.vanillasky.imageuploader.service;

import com.vanillasky.imageuploader.model.image.processor.ImageProcessor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Like controller for image model, assign each image processing job to corresponding img processor
 */
@Service
public class ImageProcessingService {

    private final Map<String, ImageProcessor> map;

    //registering available img processors
    public ImageProcessingService(List<ImageProcessor> list) {
        this.map = list.stream().collect(Collectors.toMap(ImageProcessor::key, p -> p));
    }

    //use the corresponding img processors (op) when calling
    //handle one picture
    public byte[] apply(String op, byte[] data) {
        return map.get(op).process(data);
    }

    //handle multiple pictures
    public byte[] apply(String op, byte[][] data) {
        return map.get(op).process(data);
    }

    //generate mosaic photo and return result url(in specific JSON format defined by frontend)
    public ResponseEntity<?> handleOneShot(MultipartFile[] files, String op) throws IOException {
        System.out.println("In handleOneShot, file length: " + files.length);

        if (files == null || files.length == 0) {
            return ResponseEntity.badRequest().body("No files uploaded");
        }

        //what if files are large?
        byte[][] images = new byte[files.length][];
        for (int i = 0; i < files.length; i++) {
            images[i] = files[i].getBytes();
        }

        //run the requested algorithm to process the img
        byte[] result = apply(op, images);

        //save result image to static folder (e.g., /static/output/)
        String fileName = UUID.randomUUID() + ".jpg";
        File outputDir = new File("download");
        outputDir.mkdirs();  // create if not exist
        File outputFile = new File(outputDir, fileName);

        //file location: download/<uuid>.jpg
        Files.write(outputFile.toPath(), result);
        System.out.println("Writing file to: " + outputFile.getAbsolutePath());

        /** Construct the public URL that the frontend will use to preview or download the file.
         * This assumes:
         * 1. The file is saved locally under the "download/" directory. (relative path)
         * 2. Spring Boot's WebConfig maps the URL path "/download/**" to serve static files from that directory:
         *    registry.addResourceHandler("/download/**").addResourceLocations("file:download/");
         * 3. In development mode, the Vue frontend is served on a different port (e.g., 5173), and has a Vite proxy configured:
         *    '/download': { target: 'http://localhost:8080', changeOrigin: true }
         * Therefore, returning a relative path like "/download/<uuid>.jpg" will work correctly in both development and production.
         */
        String url = "/download/" + fileName;

        //return URL as JSON in required format
        Map<String, Object> body = Map.of(
                "success", true,
                "imageUrl", url,
                "message", "Image generated successfully"
        );
        return ResponseEntity.ok().body(body);
    }
}

/**
 * Deployment note:
 * When you package the Spring Boot application as a JAR, files inside src/main/resources/static/
 * are packaged inside the JAR and become read-only. You cannot write to that location at runtime.
 */
