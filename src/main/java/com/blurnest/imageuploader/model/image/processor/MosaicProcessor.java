package com.blurnest.imageuploader.model.image.processor;

import com.blurnest.imageuploader.model.image.engine.MosaicModelEngine;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
public class MosaicProcessor implements ImageProcessor{

    private final MosaicModelEngine engine =
            MosaicModelEngine.builder()
                    .tileSize(10, 10)
                    .workDir(new File("/tmp/final_project/image"))
                    .userChooseBlur(false)
                    .build();

    @Override
    public String key() {
        return "mosaic";
    }

    @Override
    public byte[] process(byte[] in) {
        throw new UnsupportedOperationException("Use process(byte[][]) for mosaic");
    }

    @Override
    public byte[] process(byte[][] in) {
        System.out.println("process mosaic photo");
        try {
            return engine.preprocess_test(in);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // test
//    @Override
//    public byte[] process_test(byte[] in) {
//        System.out.println("process mosaic photo");
        //testing:
//        try {
//            Path fakeImagePath = Paths.get("/tmp/mosaic.jpg");
//            return Files.readAllBytes(fakeImagePath);
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//    }
}
