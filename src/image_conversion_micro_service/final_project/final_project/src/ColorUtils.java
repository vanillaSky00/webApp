import java.awt.*;
import java.awt.image.BufferedImage;

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
}
