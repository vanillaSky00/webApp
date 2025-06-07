package com.blurnest.imageuploader.model.image.engine.mosaicUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ImageLoader {

    // access the target image
    public static BufferedImage loadImage(String path) {
        try {
            File file = new File(path);
            return ImageIO.read(file);
        } catch (IOException e) {
            System.err.println("access fail：" + e.getMessage());
            return null;
        }
    }

    // access all compressed tile images
    public static List<BufferedImage> loadImagesFromFolder(String folderPath) {
        List<BufferedImage> images = new ArrayList<>();
        File folder = new File(folderPath);
        File[] files = folder.listFiles();

        if (files == null || files.length == 0) {
            System.err.println("document doesn't exist：" + folderPath);
            return images;
        }

        for (File file : files) {
            if (file.isFile() && isImageFile(file)) {
                BufferedImage img = loadImage(file.getAbsolutePath());
                if (img != null) {
                    images.add(img);
                }
            }
        }

        return images;
    }

    private static boolean isImageFile(File file) {
        String[] extensions = { ".jpg", ".jpeg", ".png", ".bmp" };
        String name = file.getName().toLowerCase();
        for (String ext : extensions) {
            if (name.endsWith(ext)) return true;
        }
        return false;
    }

}
