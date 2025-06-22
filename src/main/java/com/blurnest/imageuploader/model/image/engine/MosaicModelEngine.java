package com.blurnest.imageuploader.model.image.engine;

import com.blurnest.imageuploader.model.image.engine.mosaicUtils.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class MosaicModelEngine {

    /* ─── defaults ─────────────────────────────────────────────── */
    private final int tileWidth;     // immutable after build()
    private final int tileHeight;
    private final File workDir;      // e.g. new File("image")
    private final boolean userChooseBlur;

    /* ─── builder ──────────────────────────────────────────────── */
    public static class Builder {
        private int tileWidth = 10;
        private int tileHeight = 10;
        private File workDir = new File("/tmp");
        private boolean userChooseBlur = false;

        public Builder tileSize(int w, int h) {
            this.tileWidth = w;
            this.tileHeight = h;
            return this;
        }

        public Builder userChooseBlur(boolean blur) {
            this.userChooseBlur = blur;
            return this;
        }

        public Builder workDir(File dir) {
            this.workDir = dir;
            return this;
        }

        public MosaicModelEngine build() {
            return new MosaicModelEngine(tileWidth, tileHeight, workDir, userChooseBlur);
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    /* ─── ctor (private) ───────────────────────────────────────── */
    private MosaicModelEngine(int tw, int th, File dir, boolean userChooseBlur) {
        this.tileWidth = tw;
        this.tileHeight = th;
        this.workDir = dir;
        this.userChooseBlur = userChooseBlur;
    }

    /* ─── public API ───────────────────────────────────────────── */

    /**
     * @param inputs raw bytes of an uploaded image
     * @return mosaic as JPG bytes
     */
    public byte[] preprocess(byte[][] inputs) throws Exception {

        //base image
        BufferedImage targetImage = ImageIO.read(new ByteArrayInputStream(inputs[0]));
        if (targetImage == null) throw new IllegalArgumentException("Base image is invalid or unsupported format.");

        List<BufferedImage> tileImages = ImageLoader.loadImagesFromFolder("compressed_tile");
        /* ---- start PREPROCESSING as BufferedImage ---- */
        // split target into tiles
        List<BufferedImage> targetTiles =
                ImageSplitter.splitImage(targetImage, tileWidth, tileHeight);

        //  match tiles
        List<BufferedImage> matchedTiles = ImageMatcher.matchTiles(targetTiles, tileImages);
        System.out.println("Matched " + matchedTiles.size() + " tiles.");

        //  build mosaic
        int cols = targetImage.getWidth() / tileWidth;
        int rows = targetImage.getHeight() / tileHeight;
        BufferedImage mosaic = MosaicBuilder.buildMosaic(
                matchedTiles, cols, rows, tileWidth, tileHeight, userChooseBlur
        );
        /* ---- finish PREPROCESSING as BufferedImage ---- */

        //return as bytes
        return ImageConverter.bufferedImageToBytes(mosaic, "jpg");
    }

    public byte[] preprocess_test(byte[][] inputs) throws Exception {

        //base image
        BufferedImage targetImage = ImageIO.read(new ByteArrayInputStream(inputs[0]));
        if (targetImage == null) throw new IllegalArgumentException("Base image is invalid or unsupported format.");

        //images library
        List<BufferedImage> tileImages = new ArrayList<>();
        for (int i = 1; i < inputs.length; i++) {
            BufferedImage tile = ImageIO.read(new ByteArrayInputStream(inputs[i]));
            if (tile == null) continue; // skip corrupt tiles
            tileImages.add(tile);
        }

        /* ---- start PREPROCESSING as BufferedImage ---- */
        // split target into tiles
        List<BufferedImage> targetTiles =
                ImageSplitter.splitImage(targetImage, tileWidth, tileHeight);

        //  match tiles
        List<BufferedImage> matchedTiles = ImageMatcher.matchTiles(targetTiles, tileImages);
        System.out.println("Matched " + matchedTiles.size() + " tiles.");

        //  build mosaic
        int cols = targetImage.getWidth() / tileWidth;
        int rows = targetImage.getHeight() / tileHeight;
        BufferedImage mosaic = MosaicBuilder.buildMosaic(
                matchedTiles, cols, rows, tileWidth, tileHeight, userChooseBlur
        );

        /* ---- finish PREPROCESSING as BufferedImage ---- */

        //return as bytes
        return ImageConverter.bufferedImageToBytes(mosaic, "jpg");
    }
}
