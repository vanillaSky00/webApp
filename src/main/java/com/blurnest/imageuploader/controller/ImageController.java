package com.blurnest.imageuploader.controller;

import com.blurnest.imageuploader.service.ImageProcessingService;
//import com.blurnest.imageuploader.service.StorageService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/images")
public class ImageController {
    // here can add service and utils for real calling handle, controller only assign the endpoint
    // job to each component
    //private final StorageService storageService;
    private final ImageProcessingService imageProcessingService;

    public ImageController(//StorageService storageService,
                           ImageProcessingService imageProcessingService
    ) {
        //this.storageService = storageService;
        this.imageProcessingService = imageProcessingService;
    }

    // matches /process                   → op = "compress"  (default)
    // matches /process?op=compress       → op = "compress"
    // matches /process?op=decompress     → op = "decompress"
    // matches /process?op=mosaic         → op = "mosaic"
    @PostMapping("/process")
    public ResponseEntity<?> processAndDownloadImage(@RequestParam("image") MultipartFile[] files,//for postgrel key value
                                                     @RequestParam(defaultValue = "compress") String op)
            throws IOException {

        //sanitization
        System.out.println("op: " + op);
        if(!op.equals("compress") && !op.equals("decompress") && !op.equals("mosaic")) {
            return  ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Invalid operation");
        }
        System.out.println("finish processAndDownloadImage");
        return imageProcessingService.handleOneShot(files, op);
    }

//    @DeleteMapping("/download/{fileName}")
//    public ResponseEntity<?> deleteImage(@PathVariable String fileName) {
//        if (!fileName.matches("^[a-zA-Z0-9\\-]+\\.jpg$")) {
//            return ResponseEntity.badRequest().body("Invalid file name");
//        }
//
//        File file = new File("download", fileName);
//        if (file.exists() && file.delete()) {
//            return ResponseEntity.ok(Map.of("success", true, "message", "File deleted"));
//        } else {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("File not found");
//        }
//    }
}
