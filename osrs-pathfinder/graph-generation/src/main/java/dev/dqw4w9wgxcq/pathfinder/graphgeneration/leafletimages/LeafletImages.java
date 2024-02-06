package dev.dqw4w9wgxcq.pathfinder.graphgeneration.leafletimages;

import com.google.common.base.Preconditions;
import dev.dqw4w9wgxcq.pathfinder.commons.Constants;
import dev.dqw4w9wgxcq.pathfinder.commons.domain.pathfinding.ComponentGrid;
import lombok.extern.slf4j.Slf4j;
import net.runelite.cache.MapImageDumper;
import net.runelite.cache.fs.Store;
import net.runelite.cache.util.XteaKeyManager;
import org.jspecify.annotations.Nullable;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * creates images used by Leaflet in visualizer website
 */
@Slf4j
public class LeafletImages {
    public static final int SIZE = 256;
    public static final int MIN_ZOOM = -2;

    private static final int[] COLORS = {
        Color.GRAY.getRGB(),
        Color.WHITE.getRGB(),
        Color.BLUE.getRGB(),
        Color.RED.getRGB(),
        Color.YELLOW.getRGB(),
        Color.GREEN.getRGB(),
        Color.CYAN.getRGB(),
        Color.MAGENTA.getRGB(),
        Color.ORANGE.getRGB(),
        Color.PINK.getRGB(),
        Color.LIGHT_GRAY.getRGB(),
        Color.DARK_GRAY.getRGB(),
    };

    public static void write(File outDir, File cacheDir, File xteasJson, ComponentGrid componentGrid)
            throws IOException {
        log.info("LeafletImages dir:{}", outDir);

        for (var plane = 0; plane < Constants.PLANES_SIZE; plane++) {
            var componentsImage = generateFullComponentsImage(false, plane, componentGrid);
            writeImages(plane, new File(outDir, "component"), componentsImage, 1);
        }

        for (var plane = 0; plane < Constants.PLANES_SIZE; plane++) {
            var blockedImage = generateFullComponentsImage(true, plane, componentGrid);
            writeImages(plane, new File(outDir, "blocked"), blockedImage, 1);
        }

        //        for (var plane = 0; plane < Constants.PLANES_SIZE; plane++) {
        //            var mapImage = generateFullMapImage(plane, cacheDir, xteasJson);
        //            writeImages(plane, new File(outDir, "map"), mapImage, 4);
        //        }
    }

    // returns null if image is would be completely transparent
    private static @Nullable Image generateLeafletImage(BufferedImage fullImage, int x, int y, int tilePxSize) {
        var invertY = fullImage.getHeight() - y - SIZE * tilePxSize;
        log.debug("Generating leaflet image for x={}, y={} (invertY={}), tilePxSize={}", x, y, invertY, tilePxSize);

        var subImg = fullImage.getSubimage(x, invertY, SIZE * tilePxSize, SIZE * tilePxSize);

        if (isImageBlank(subImg)) return null;

        return subImg.getScaledInstance(SIZE, SIZE, Image.SCALE_SMOOTH);
    }

    private static void writeImages(int plane, File dir, BufferedImage fullImg, int tilePxSize) throws IOException {
        log.debug(
                "Writing leaflet images to dir:{}, tilePxSize:{} | fullImage width:{}, height:{}",
                dir,
                tilePxSize,
                fullImg.getWidth(),
                fullImg.getHeight());

        //noinspection ResultOfMethodCallIgnored
        dir.mkdirs();

        // write full image
        ImageIO.write(fullImg, "png", new File(dir, plane + "-full.png"));

        var maxZoomD = Math.log(tilePxSize) / Math.log(2); // java doesn't have log2
        Preconditions.checkArgument(maxZoomD % 1 == 0, "tilePxSize must be a power of 2");
        var maxZoom = (int) maxZoomD;

        for (var zoom = MIN_ZOOM; zoom <= maxZoom; zoom++) {
            if (zoom != maxZoom) continue; // todo temp disabled all but max zoom

            int size = SIZE * tilePxSize;

            var width = fullImg.getWidth() / size;
            var height = fullImg.getHeight() / size;
            for (var x = 0; x < width; x++) {
                for (var y = 0; y < height; y++) {
                    var fileName = String.format("%d-%d-%d.png", plane, x, y);

                    var img = generateLeafletImage(fullImg, x * size, y * size, tilePxSize);

                    if (img == null) continue; // image is blank

                    var file = new File(dir, fileName);
                    ImageIO.write(toBufferedImage(img), "png", file);
                }
            }
        }
    }

    public static BufferedImage generateFullComponentsImage(
            boolean forBlocked, int plane, ComponentGrid componentGrid) {
        var grid = componentGrid.planes()[plane];
        var width = grid.length;
        var height = grid[0].length;

        Preconditions.checkArgument(
                width % Constants.REGION_SIZE == 0,
                "grid width must be multiple of {} (region size), found {}",
                Constants.REGION_SIZE,
                width);
        Preconditions.checkArgument(
                height % Constants.REGION_SIZE == 0,
                "grid height must be multiple of {} (region size), found {}",
                Constants.REGION_SIZE,
                height);

        var img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        for (var x = 0; x < width; x++) {
            for (var y = 0; y < height; y++) {
                var id = grid[x][y];

                var flippedY = height - y - 1;
                if (forBlocked) {
                    if (id == -1) {
                        img.setRGB(x, flippedY, Color.BLACK.getRGB());
                    }
                } else {
                    if (id != -1) {
                        img.setRGB(x, flippedY, getColor(id));
                    }
                }
            }
        }

        return img;
    }

    private static BufferedImage generateFullMapImage(int plane, File cacheDir, File xteasJson) throws IOException {
        var xteaKeyManager = new XteaKeyManager();
        try (var is = new FileInputStream(xteasJson)) {
            xteaKeyManager.loadKeys(is);
        }

        MapImageDumper mapImageDumper;
        try (var store = new Store(cacheDir)) {
            store.load();
            mapImageDumper = new MapImageDumper(store, xteaKeyManager);
            mapImageDumper.setRenderIcons(false);
            mapImageDumper.setTransparency(true);
            mapImageDumper.load();
        }

        return mapImageDumper.drawMap(plane);
    }

    private static int getColor(int id) {
        return COLORS[id % COLORS.length];
    }

    /**
     * Converts a given Image into a BufferedImage
     *
     * @param img The Image to be converted
     * @return The converted BufferedImage
     */
    // https://stackoverflow.com/questions/13605248/java-converting-image-to-bufferedimage
    private static BufferedImage toBufferedImage(Image img) {
        if (img instanceof BufferedImage) {
            return (BufferedImage) img;
        }

        // Create a buffered image with transparency
        BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);

        // Draw the image on to the buffered image
        Graphics2D bGr = bimage.createGraphics();
        bGr.drawImage(img, 0, 0, null);
        bGr.dispose();

        // Return the buffered image
        return bimage;
    }

    private static boolean isImageBlank(BufferedImage img) {
        for (var subX = 0; subX < img.getWidth(); subX++) {
            for (var subY = 0; subY < img.getHeight(); subY++) {
                var rgb = img.getRGB(subX, subY);
                if (rgb != 0) {
                    return false;
                }
            }
        }

        return true;
    }

    public static void main(String[] args) throws IOException {
        var desktop = new File(System.getProperty("user.home"), "Desktop");
        var fullImgFile = new File(desktop, "0-full.png");
        var outDir = new File(desktop, "asdf");

        var fullImg = ImageIO.read(fullImgFile);

        writeImages(0, outDir, fullImg, 1);
    }
}
