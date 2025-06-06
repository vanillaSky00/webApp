package com.vanillasky.imageuploader.controller;

import com.vanillasky.imageuploader.service.ImageProcessingService;
import com.vanillasky.imageuploader.service.StorageService;
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
    private final StorageService storageService;
    private final ImageProcessingService imageProcessingService;

    public ImageController(StorageService storageService,
                           ImageProcessingService imageProcessingService
    ) {
        this.storageService = storageService;
        this.imageProcessingService = imageProcessingService;
    }

    @PostMapping
    public ResponseEntity<?> uploadImage(@RequestParam("image") MultipartFile file) throws IOException {
        String uploadImage = storageService.uploadImage(file);
        return ResponseEntity.status(HttpStatus.OK)
                .body(uploadImage);
    }


    @GetMapping ("/{fileName}")
    public ResponseEntity<?> downloadImage(@PathVariable String fileName){
        byte[] imageData = storageService.downloadImage(fileName);
        return ResponseEntity.status(HttpStatus.OK)
                .contentType(MediaType.valueOf("image/png"))
                .body(imageData);
    }

    @PostMapping("/fileSystem")
    public ResponseEntity<?> uploadImageToFileSystem(@RequestParam("image") MultipartFile file) throws IOException {
        String uploadImage = storageService.uploadImageToFileSystem(file);
        return ResponseEntity.status(HttpStatus.OK)
                .body(uploadImage);
    }


    @GetMapping ("/fileSystem/{fileName}")
    public ResponseEntity<?> downloadImageFromFileSystem(@PathVariable String fileName) throws IOException {
        byte[] imageData = storageService.downloadImageFromFileSystem(fileName);
        return ResponseEntity.status(HttpStatus.OK)
                .contentType(MediaType.valueOf("image/png"))
                .body(imageData);
    }

    @GetMapping("/fileSystem/list")
    public ResponseEntity<?> listAllImages() {
        List<String> fileNames = storageService.loadAllImageFileNames(); // Implement this
        return ResponseEntity.ok(fileNames);
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

//    @PostMapping("/process")
//    public ResponseEntity<?> processAndDownloadImage(@RequestParam("image") MultipartFile[] files,//for postgrel key value
//                                                     @RequestParam(defaultValue = "compress") String op)
//            throws IOException {
//        if (files.length == 0)
//            return ResponseEntity.badRequest().body("No files uploaded");
//
//        if (files.length == 1)//do default 1
//
//            //sanitization
//            System.out.println("op: " + op);
//        if(!op.equals("compress") && !op.equals("decompress") && !op.equals("mosaic")) {
//            return  ResponseEntity.status(HttpStatus.BAD_REQUEST)
//                    .body("Invalid operation");
//        }
//        System.out.println("finish processAndDownloadImage");
//        return storageService.handleOneShot(file, op);
//    }
}
