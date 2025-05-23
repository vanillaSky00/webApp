package com.vanillasky.imageuploader.service;

import com.vanillasky.imageuploader.entity.FileData;
import com.vanillasky.imageuploader.entity.ImageData;
import com.vanillasky.imageuploader.model.image.ImageUtils;
import com.vanillasky.imageuploader.repository.FileDataRepository;
import com.vanillasky.imageuploader.repository.StorageRepository;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@Service
public class StorageService {

    private final StorageRepository repository;
    private final FileDataRepository fileDataRepository;

    public StorageService(StorageRepository repository, FileDataRepository fileDataRepository) {
        this.repository = repository;
        this.fileDataRepository = fileDataRepository;
    }

    private final String FOLDER_PATH = "/Users/harris/uploads/";

    public String uploadImage(MultipartFile file) throws IOException {

        //save the file to database using save
        //we first build the data then save it
        ImageData imageData = repository.save(ImageData.builder()
                .name(file.getOriginalFilename())
                .type(file.getContentType())
                .imageData(ImageUtils.compressImage(file.getBytes())).build());

        if(imageData != null) {
            return "file uploaded successfully" +  imageData.getName();
        }
        return null;
    }

    public byte[] downloadImage(String fileName) {
        Optional<ImageData> dbImageData = repository.findByName(fileName);
        byte[] images = ImageUtils.decompressImage(dbImageData.get().getImageData());
        return images;
    }

    /**
     * for filesystem
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

}
