package com.blurnest.imageuploader.model.image.engine.mosaicUtils;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class ColorUtils {

    // Compute average color of an image
    public static Color getAverageColor(BufferedImage image) {
        long sumRed = 0, sumGreen = 0, sumBlue = 0;
        int width = image.getWidth();
        int height = image.getHeight();
        int totalPixels = width * height;

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Color pixel = new Color(image.getRGB(x, y));
                sumRed += pixel.getRed();
                sumGreen += pixel.getGreen();
                sumBlue += pixel.getBlue();
            }
        }

        int avgRed = (int) (sumRed / totalPixels);
        int avgGreen = (int) (sumGreen / totalPixels);
        int avgBlue = (int) (sumBlue / totalPixels);

        return new Color(avgRed, avgGreen, avgBlue);
    }

    // Compute Euclidean distance between two colors
    public static double colorDistance(Color c1, Color c2) {
        int redDiff = c1.getRed() - c2.getRed();
        int greenDiff = c1.getGreen() - c2.getGreen();
        int blueDiff = c1.getBlue() - c2.getBlue();
        return Math.sqrt(redDiff * redDiff + greenDiff * greenDiff + blueDiff * blueDiff);
    }

    public static void exportTileColorsToCSV(List<BufferedImage> tiles, String outputPath) {
        try (FileWriter writer = new FileWriter(outputPath)) {
            writer.write("TileIndex,Red,Green,Blue\n");
            for (int i = 0; i < tiles.size(); i++) {
                BufferedImage tile = tiles.get(i);
                Color avgColor = ColorUtils.getAverageColor(tile);
                writer.write(
                        String.format("%d,%d,%d,%d\n", i, avgColor.getRed(), avgColor.getGreen(), avgColor.getBlue()));
            }
            System.out.println("Exported colors to: " + outputPath);
        } catch (IOException e) {
            System.err.println("Failed to write CSV: " + e.getMessage());
        }
    }
}
