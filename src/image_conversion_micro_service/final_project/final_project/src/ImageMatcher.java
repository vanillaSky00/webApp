import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class ImageMatcher {

    // Return a list of best-match tiles corresponding to each target tile
    public static List<BufferedImage> matchTiles(
            List<BufferedImage> targetTiles,
            List<BufferedImage> tileLibrary) {

        List<BufferedImage> result = new ArrayList<>();

        // Precompute average colors of tile library
        List<Color> tileColors = new ArrayList<>();
        for (BufferedImage tile : tileLibrary) {
            tileColors.add(ColorUtils.getAverageColor(tile));
        }

        // For each target tile, find the closest tile
        for (BufferedImage target : targetTiles) {
            Color targetColor = ColorUtils.getAverageColor(target);

            double minDistance = Double.MAX_VALUE;
            int bestMatchIndex = -1;

            for (int i = 0; i < tileColors.size(); i++) {
                double dist = ColorUtils.colorDistance(targetColor, tileColors.get(i));
                if (dist < minDistance) {
                    minDistance = dist;
                    bestMatchIndex = i;
                }
            }

            result.add(tileLibrary.get(bestMatchIndex));
        }

        return result;
    }
}