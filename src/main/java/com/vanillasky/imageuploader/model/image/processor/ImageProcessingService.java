package com.vanillasky.imageuploader.model.image.processor;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Like controller for image model, assign each image processing job to corresponding img processor
 */
@Service
public class ImageProcessingService {

    private final Map<String, ImageProcessor> map;

    //registering available img processors
    public ImageProcessingService(List<ImageProcessor> list) {
        this.map = list.stream().collect(Collectors.toMap(ImageProcessor::key, p -> p));
    }

    //use the corresponding img processors (op) when calling
    public byte[] apply(String op, byte[] data) {
        return map.get(op).process(data);
    }

    //use the corresponding img processors (op) when calling
    public byte[] apply(String op, byte[][] data) {
        return map.get(op).process(data);
    }
}
