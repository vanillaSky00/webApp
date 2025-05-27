package com.vanillasky.imageuploader.controller;

import com.vanillasky.imageuploader.model.image.CompressionProcessor;
import com.vanillasky.imageuploader.service.StorageService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("api/home/images")
public class ImageController {
    // here can add service and utils for real calling handle, controller only assign the endpoint
    // job to each component
    private final StorageService service;
    private final StorageService storageService;

    public ImageController(StorageService service, StorageService storageService) {
        this.service = service;
        this.storageService = storageService;
    }

    @PostMapping
    public ResponseEntity<?> uploadImage(@RequestParam("image") MultipartFile file) throws IOException {
        String uploadImage = service.uploadImage(file);
        return ResponseEntity.status(HttpStatus.OK)
                .body(uploadImage);
    }


    @GetMapping ("/{fileName}")
    public ResponseEntity<?> downloadImage(@PathVariable String fileName){
        byte[] imageData = service.downloadImage(fileName);
        return ResponseEntity.status(HttpStatus.OK)
                .contentType(MediaType.valueOf("image/png"))
                .body(imageData);
    }

    @PostMapping("/fileSystem")
    public ResponseEntity<?> uploadImageToFileSystem(@RequestParam("image") MultipartFile file) throws IOException {
        String uploadImage = service.uploadImageToFileSystem(file);
        return ResponseEntity.status(HttpStatus.OK)
                .body(uploadImage);
    }


    @GetMapping ("/fileSystem/{fileName}")
    public ResponseEntity<?> downloadImageFromFileSystem(@PathVariable String fileName) throws IOException {
        byte[] imageData = service.downloadImageFromFileSystem(fileName);
        return ResponseEntity.status(HttpStatus.OK)
                .contentType(MediaType.valueOf("image/png"))
                .body(imageData);
    }

    @GetMapping("/fileSystem/list")
    public ResponseEntity<?> listAllImages() {
        List<String> fileNames = service.loadAllImageFileNames(); // Implement this
        return ResponseEntity.ok(fileNames);
    }

    // matches /process                → op = "compress"  (default)
    // matches /process?op=compress       → op = "compress"
    // matches /process?op=decompress     → op = "decompress"
    @PostMapping("/process")
    public ResponseEntity<?> processAndDownloadImage(@RequestParam("image") MultipartFile file,//for postgrel key value
                                                     @RequestParam(defaultValue = "compress") String op)
            throws IOException {

        //sanitization
        System.out.println("op: " + op);
        if(!op.equals("compress") && !op.equals("decompress")) {
            return  ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Invalid operation");
        }
        return storageService.handleOneShot(file, op);
    }
}
