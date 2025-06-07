package com.vanillasky.imageuploader.model.image.processor;

public interface ImageProcessor {
    String key(); //"compress", "resize", ... other function
    byte[] process(byte[] in);

    //java 8
    default byte[] process(byte[][] in) {
        throw new UnsupportedOperationException("This processor does not support multiple images.");
    }
}

