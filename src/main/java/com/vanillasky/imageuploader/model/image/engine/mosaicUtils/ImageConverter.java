package com.vanillasky.imageuploader.model.image.engine.mosaicUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ImageConverter {
    public static byte[] bufferedImageToBytes(BufferedImage image, String format) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            ImageIO.write(image, format, baos);
            return baos.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Failed to convert image to byte array", e);
        }
    }

    public static BufferedImage bytesToBufferedImage(byte[] data) {
        try (ByteArrayInputStream bais = new ByteArrayInputStream(data)) {
            /*
             * ImageIO.read(...) is thread-safe for *reading*,
             * but it uses an internal cache on disk by default.
             * Disable it once at JVM start-up (cheap) or in a static block:
             */
            ImageIO.setUseCache(false);
            BufferedImage img = ImageIO.read(bais);
            if (img == null) {
                throw new IllegalArgumentException("Unsupported image format or corrupt data");
            }
            return img;
        } catch (IOException e) {
            throw new RuntimeException("Failed to convert byte[] to BufferedImage", e);
        }
    }
}
