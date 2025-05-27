package com.vanillasky.imageuploader.model.image;

import org.springframework.stereotype.Service;

public interface ImageProcessor {
    String key(); //"compress", "resize", ... other function
    byte[] process(byte[] in);
}

