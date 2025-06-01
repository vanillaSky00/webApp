package com.vanillasky.imageuploader.model.image.engine;

import com.vanillasky.imageuploader.model.image.engine.mosaicUtils.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.List;

public class MosaicModelEngine {

    /* ─── defaults ─────────────────────────────────────────────── */
    private final int tileWidth;     // immutable after build()
    private final int tileHeight;
    private final File workDir;      // e.g. new File("image")

    /* ─── builder ──────────────────────────────────────────────── */
    public static class Builder {
        private int tileWidth  = 10;
        private int tileHeight = 10;
        private File workDir   = new File("/tmp");

        public Builder tileSize(int w, int h) {
            this.tileWidth  = w;
            this.tileHeight = h;
            return this;
        }
        public Builder workDir(File dir) {
            this.workDir = dir;
            return this;
        }

        public MosaicModelEngine build() {
            return new MosaicModelEngine(tileWidth, tileHeight, workDir);
        }
    }
    public static Builder builder() { return new Builder(); }

    /* ─── ctor (private) ───────────────────────────────────────── */
    private MosaicModelEngine(int tw, int th, File dir) {
        this.tileWidth  = tw;
        this.tileHeight = th;
        this.workDir    = dir;
    }

    /* ─── public API ───────────────────────────────────────────── */
    /**
     * @param input raw bytes of an uploaded image
     * @return mosaic as JPG bytes
     */
    public byte[] preprocess(byte[] input) throws Exception {
        //hardcoed test
        //String targetPath = "/tmp/target_resized.jpg";
        String targetPath = "/tmp/target.jpg";
        BufferedImage targetImage = ImageLoader.loadImage(targetPath);

        // read input bytes into BufferedImage
        //BufferedImage targetImage = ImageConverter.bytesToBufferedImage(input);

        /* ---- start PREPROCESSING as BufferedImage ---- */
        File tileFolder = new File(workDir, "input/compressed_tile");
        List<BufferedImage> tileImages = ImageLoader.loadImagesFromFolder(tileFolder.getPath());

        // split target into tiles
        List<BufferedImage> targetTiles =
                ImageSplitter.splitImage(targetImage, tileWidth, tileHeight);

        //  match tiles
        List<BufferedImage> matchedTiles = ImageMatcher.matchTiles(targetTiles, tileImages);
        System.out.println("Matched " + matchedTiles.size() + " tiles.");

        //  build mosaic
        int cols = targetImage.getWidth()  / tileWidth;
        int rows = targetImage.getHeight() / tileHeight;
        BufferedImage mosaic = MosaicBuilder.buildMosaic(
                matchedTiles, cols, rows, tileWidth, tileHeight
        );
        /* ---- return as JPG bytes ---- */
        /* ---- finish PREPROCESSING as BufferedImage ---- */
        // Save mosaic image
        try {
            System.out.println("output image");
            ImageIO.write(mosaic, "jpg", new File("/tmp/output.jpg"));
        } catch (IOException e) {
            System.err.println("Failed to save mosaic: " + e.getMessage());
        }

        //return as bytes
        return ImageConverter.bufferedImageToBytes(mosaic, "jpg");
    }

    public byte[] preprocess_(byte[] input) throws Exception {
        //hardcoed test
        String targetPath = "/tmp/target_resized.jpg";
        BufferedImage targetImage = ImageLoader.loadImage(targetPath);

        // read input bytes into BufferedImage
        //BufferedImage targetImage = ImageConverter.bytesToBufferedImage(input);

        /* ---- start PREPROCESSING as BufferedImage ---- */
        File tileFolder = new File(workDir, "input/compressed_tile");
        List<BufferedImage> tileImages = ImageLoader.loadImagesFromFolder(tileFolder.getPath());

        // split target into tiles
        List<BufferedImage> targetTiles =
                ImageSplitter.splitImage(targetImage, tileWidth, tileHeight);

        //  match tiles
        List<BufferedImage> matchedTiles = ImageMatcher.matchTiles(targetTiles, tileImages);
        System.out.println("Matched " + matchedTiles.size() + " tiles.");

        //  build mosaic
        int cols = targetImage.getWidth()  / tileWidth;
        int rows = targetImage.getHeight() / tileHeight;
        BufferedImage mosaic = MosaicBuilder.buildMosaic(
                matchedTiles, cols, rows, tileWidth, tileHeight
        );
        /* ---- return as JPG bytes ---- */
        /* ---- finish PREPROCESSING as BufferedImage ---- */
        // Save mosaic image
//        try {
//            System.out.println("output image");
//            ImageIO.write(mosaic, "jpg", new File("/tmp/output.jpg"));
//        } catch (IOException e) {
//            System.err.println("Failed to save mosaic: " + e.getMessage());
//        }

        //return as bytes
        return ImageConverter.bufferedImageToBytes(mosaic, "jpg");
    }
}
