package com.vanillasky.imageuploader.model.image.processor;

public interface ImageProcessor {
    String key(); //"compress", "resize", ... other function
    byte[] process(byte[] in);
}

