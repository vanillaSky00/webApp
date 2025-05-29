package com.vanillasky.imageuploader.model.image;

import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Component
public class MosaicProcessor implements ImageProcessor{
    @Override
    public String key() {
        return "mosaic";
    }

    @Override
    public byte[] process(byte[] in) {
        System.out.println("process mosaic photo");

        //testing:
        try {
            Path fakeImagePath = Paths.get("/tmp/mosaic.jpg");
            return Files.readAllBytes(fakeImagePath);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        //return in;
    }
}
