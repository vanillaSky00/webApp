import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class ImageSplitter {

    public static List<BufferedImage> splitImage(BufferedImage image, int tileWidth, int tileHeight) {
        List<BufferedImage> tiles = new ArrayList<>();

        int imgWidth = image.getWidth();
        int imgHeight = image.getHeight();

        for (int y = 0; y <= imgHeight - tileHeight; y += tileHeight) {
            for (int x = 0; x <= imgWidth - tileWidth; x += tileWidth) {
                BufferedImage subImage = image.getSubimage(x, y, tileWidth, tileHeight);
                tiles.add(subImage);
            }
        }

        return tiles;
    }
}
