package com.blurnest.imageuploader.service;

import com.blurnest.imageuploader.entity.FileData;
import com.blurnest.imageuploader.entity.ImageData;
import com.blurnest.imageuploader.repository.FileDataRepository;
import com.blurnest.imageuploader.repository.StorageRepository;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@Service
public class StorageService {

    private final StorageRepository repository;
    private final FileDataRepository fileDataRepository;
    private final ImageProcessingService imageProcessingService;

    public StorageService(StorageRepository repository,
                          FileDataRepository fileDataRepository,
                          ImageProcessingService imageProcessingService) {
        this.repository = repository;
        this.fileDataRepository = fileDataRepository;
        this.imageProcessingService = imageProcessingService;
    }

    private final String FOLDER_PATH = "/Users/harris/uploads/";

    /**
     * upload an image to the database
     * @param file
     * @return
     * @throws IOException
     */
    public String uploadImage(MultipartFile file) throws IOException {

        //save the file to database using save
        //we first build the data then save it
        ImageData imageData = repository.save(ImageData.builder()
                .name(file.getOriginalFilename())
                .type(file.getContentType())
                .imageData(imageProcessingService.apply("compress", file.getBytes())).build());
                //.imageData(CompressionProcessor.compressImage(file.getBytes())).build());

        if(imageData != null) {
            return "file uploaded successfully" +  imageData.getName();
        }
        return null;
    }

    /**
     * download an image from database
     * @param fileName
     * @return
     */
    public byte[] downloadImage(String fileName) {
        Optional<ImageData> dbImageData = repository.findByName(fileName);
        //byte[] images = CompressionProcessor.decompressImage(dbImageData.get().getImageData());
        byte[] images = imageProcessingService.apply("decompress", dbImageData.get().getImageData());
        return images;
    }

    /**
     * For filesystem
     */
    public String uploadImageToFileSystem(MultipartFile file) throws IOException {
        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        String filePath = FOLDER_PATH + fileName;

        //ensure directory exists
        Path uploadPath = Paths.get(FOLDER_PATH);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        //save metadata to database
        FileData fileData = fileDataRepository.save(FileData.builder()
                .name(file.getOriginalFilename())
                .type(file.getContentType())
                .filePath(filePath).build()
        );
        //save to filesystem
        file.transferTo((new File(filePath)));

        if(fileData != null) {
            return "file uploaded successfully" + filePath;
        }
        return null;
    }

    public byte[] downloadImageFromFileSystem(String fileName) throws IOException {
        Optional<FileData> fileData = fileDataRepository.findByName(fileName);
        String filePath = fileData.get().getFilePath();
        byte[] images = Files.readAllBytes(new File(filePath).toPath());
        return images;
    }


    public List<String> loadAllImageFileNames() {
        try (Stream<Path> stream = Files.walk(Paths.get("path/to/image/storage"))) {
            return stream
                    .filter(Files::isRegularFile)
                    .map(path -> path.getFileName().toString())
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException("Could not list files", e);
        }
    }



//    public ResponseEntity<?> handleOneShot(MultipartFile[] files, String op) throws IOException {
//        System.out.println("In handleOneShot, file length: " + files.length);
//
//        if (files == null || files.length == 0) {
//            return ResponseEntity.badRequest().body("No files uploaded");
//        }
//
//        //what if files are large?
//        byte[][] images = new byte[files.length][];
//        for (int i = 0; i < files.length; i++) {
//            images[i] = files[i].getBytes();
//        }
//
//        //run the requested algorithm to process the img
//        byte[] result = imageProcessingService.apply(op, images);
//
//        //save result image to static folder (e.g., /static/output/)
//        String fileName = UUID.randomUUID() + ".jpg";
//        File outputDir = new File("src/main/resources/static/output/");
//        outputDir.mkdirs();  // create if not exist
//        File outputFile = new File(outputDir, fileName);
//        Files.write(outputFile.toPath(), result);
//
//        //public URL
//        String url = "/output/" + fileName;
//
//        //return URL as JSON
//        Map<String, String> body = Map.of("url", url);
//        return ResponseEntity.ok().body(body);
//    }

    //test
//    public ResponseEntity<?> handleOneShot_test1(MultipartFile source, String op) throws IOException {
//        System.out.println("In handleOneShot");
//        //save upload to a JVM temp file
//        File tempInput = File.createTempFile("upload-", ".bin");
//        source.transferTo(tempInput);
//
//        //run the requested algorithm to process the img
//        byte[] result = imageProcessingService.apply(op, Files.readAllBytes(tempInput.toPath()));
//
//        //wrap bytes in a stream for zero-copy transfer
//        InputStreamResource body =
//                new InputStreamResource(new ByteArrayInputStream(result));
//
//        //build HTTP response
//        ResponseEntity<InputStreamResource> resp = ResponseEntity.ok()
//                .header(HttpHeaders.CONTENT_DISPOSITION,
//                        "attachment; filename=\"" + UUID.randomUUID() + ".png\"")
//                .contentLength(result.length)
//                .contentType(MediaType.IMAGE_PNG)
//                .body(body);
//        System.out.println("End handleOneShot");
//        //async cleanup
//        //new Thread(tempInput::delete).start();
//        new Thread(() -> {
//            tempInput.delete();
//            System.out.println("async cleanup");
//        }).start();
//
//        return resp;
//    }
//
//
//    public ResponseEntity<?> handleOneShot_test0(MultipartFile file, String op) {
//        // simulate processing
//        System.out.println("Simulating " + op + " image processing...");
//
//        // fake result: return URL to test image
//        String fakeImageUrl = "/public/mosaic.jpg";
//
//        Map<String, Object> result = new HashMap<>();
//        result.put("success", true);
//        result.put("imageUrl", fakeImageUrl);
//        result.put("message", "Image processed successfully with operation: " + op);
//
//        return ResponseEntity.ok(result);
//    }

}
