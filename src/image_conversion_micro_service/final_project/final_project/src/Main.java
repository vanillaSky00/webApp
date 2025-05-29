import java.awt.image.BufferedImage;
import java.util.List;

import javax.imageio.ImageIO;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.awt.Color;

public class Main {

    public static void main(String[] args) {
        String targetPath = "image/input/target_resized.jpg";
        String tileFolder = "image/input/compressed_tile";

        // Load target image and tiles
        BufferedImage targetImage = ImageLoader.loadImage(targetPath);
        List<BufferedImage> tileImages = ImageLoader.loadImagesFromFolder(tileFolder);

        /*
         * if (targetImage != null) {
         * System.out.println("Target image size: " + targetImage.getWidth() + "x" +
         * targetImage.getHeight());
         * ImageDisplay.showImage(targetImage, "Target Image");
         * }
         */

        System.out.println("Loaded " + tileImages.size() + " tile images.");

        // Split target image into 30x30 blocks
        int tileWidth = 10;
        int tileHeight = 10;
        List<BufferedImage> targetTiles = ImageSplitter.splitImage(targetImage, tileWidth, tileHeight);
        System.out.println("Split target image into " + targetTiles.size() + " tiles.");

        // Export average colors
        exportTileColorsToCSV(tileImages, "tile_colors.csv");
        exportTileColorsToCSV(targetTiles, "target_tile_colors.csv"); // reuse same method

        // Match target tiles to the best matching tile from the library
        List<BufferedImage> matchedTiles = ImageMatcher.matchTiles(targetTiles, tileImages);
        System.out.println("Matching completed. " + matchedTiles.size() + " tiles matched.");

        // Calculate columns and rows of target image
        int cols = targetImage.getWidth() / tileWidth;
        int rows = targetImage.getHeight() / tileHeight;

        // Build mosaic
        BufferedImage mosaic = MosaicBuilder.buildMosaic(matchedTiles, cols, rows, tileWidth, tileHeight);
        // ensure output directory exists
        new File("image").mkdirs();
        // Save mosaic image
        try {
            ImageIO.write(mosaic, "jpg", new File("image/output/output.jpg"));
            System.out.println("Mosaic image saved to image/output.jpg");
            ImageDisplay.showImage(mosaic, "Mosaic Result");
        } catch (IOException e) {
            System.err.println("Failed to save mosaic: " + e.getMessage());
        }
    }

    // Export image average colors to CSV
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
