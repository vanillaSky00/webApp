import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.List;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
public class MosaicBuilder {

    // Build a mosaic image from matched tiles
    public static BufferedImage buildMosaic(List<BufferedImage> tiles, int cols, int rows, int tileWidth,
            int tileHeight) {
        int mosaicWidth = cols * tileWidth;
        int mosaicHeight = rows * tileHeight;

        BufferedImage mosaic = new BufferedImage(mosaicWidth, mosaicHeight, BufferedImage.TYPE_INT_RGB);
        Graphics g = mosaic.getGraphics();

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                int index = row * cols + col;
                BufferedImage tile = blur(tiles.get(index));
                
                int x = col * tileWidth;
                int y = row * tileHeight;
                g.drawImage(tile, x, y, null);
            }
        }

        g.dispose();
        return mosaic;
    }

    private static BufferedImage blur(BufferedImage img) {
        float[] kernel = {
                1f / 9, 1f / 9, 1f / 9,
                1f / 9, 1f / 9, 1f / 9,
                1f / 9, 1f / 9, 1f / 9
        };
        ConvolveOp op = new ConvolveOp(new Kernel(3, 3, kernel), ConvolveOp.EDGE_NO_OP, null);
        return op.filter(img, null);
    }
}