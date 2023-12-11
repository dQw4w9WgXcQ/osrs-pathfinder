package dev.dqw4w9wgxcq.pathfinder.graphgeneration.leafletimages;

import com.google.common.base.Preconditions;
import dev.dqw4w9wgxcq.pathfinder.commons.domain.pathfinding.ComponentGrid;
import dev.dqw4w9wgxcq.pathfinder.graphgeneration.commons.Util;
import dev.dqw4w9wgxcq.pathfinder.graphgeneration.tileworld.TileWorld;
import lombok.extern.slf4j.Slf4j;
import net.runelite.cache.MapImageDumper;
import net.runelite.cache.fs.Store;
import net.runelite.cache.util.XteaKeyManager;
import org.jetbrains.annotations.Nullable;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.RasterFormatException;
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

    public static void write(File outDir, File cacheDir, File xteasJson, ComponentGrid componentGrid) throws IOException {
        log.info("LeafletImages dir:{}", outDir);

        for (var plane = 0; plane < TileWorld.PLANES_SIZE; plane++) {
            var componentsImage = generateFullComponentsImage(false, plane, componentGrid);
            writeLeafletImages(plane, new File(outDir, "component"), componentsImage, 1);
        }

        for (var plane = 0; plane < TileWorld.PLANES_SIZE; plane++) {
            var blockedImage = generateFullComponentsImage(true, plane, componentGrid);
            writeLeafletImages(plane, new File(outDir, "blocked"), blockedImage, 1);
        }

//        for (var plane = 0; plane < TileWorld.PLANES_SIZE; plane++) {
//            var mapImage = generateFullMapImage(plane, cacheDir, xteasJson);
//            writeLeafletImages(new File(outDir, "map"), mapImage, 4);
//        }
    }

    //returns null if image is would be completely transparent
    @Nullable
    private static Image generateLeafletImage(BufferedImage fullImage, int x, int y, int scale) {
        log.debug("Generating leaflet image for x={}, y={}, scale={}", x, y, scale);
        var subimage = fullImage.getSubimage(x, y, SIZE * scale, SIZE * scale);

        //check if subimage is completely transparent
        var isTransparent = true;
        for (var subX = 0; subX < subimage.getWidth(); subX++) {
            for (var subY = 0; subY < subimage.getHeight(); subY++) {
                var rgb = subimage.getRGB(subX, subY);
                if (rgb != 0) {
                    isTransparent = false;
                    break;
                }
            }
        }
        if (isTransparent) {
            return null;
        }

        return subimage.getScaledInstance(SIZE, SIZE, Image.SCALE_SMOOTH);
    }

    private static void writeLeafletImages(int plane, File dir, BufferedImage fullImage, int scale) throws IOException {
        log.debug("Writing leaflet images to dir:{}, scale:{} | fullImage width:{}, height:{}", dir, scale, fullImage.getWidth(), fullImage.getHeight());

        //noinspection ResultOfMethodCallIgnored
        dir.mkdirs();

        ImageIO.write(fullImage, "png", new File(dir, "plane" + plane + ".png"));

        var maxZoomD = Math.log(scale) / Math.log(2);//java doesn't have log2
        Preconditions.checkArgument(maxZoomD % 1 == 0, "scale must be a power of 2");
        var maxZoom = (int) maxZoomD;

        for (var zoom = MIN_ZOOM; zoom <= maxZoom; zoom++) {
            if (zoom != maxZoom) continue;//todo temp disabled all but max zoom

            for (var x = 0; x < fullImage.getWidth(); x += SIZE * scale) {
                for (var y = 0; y < fullImage.getHeight(); y += SIZE * scale) {
                    Image image;
                    try {
                        image = generateLeafletImage(fullImage, x, y, scale);
                    } catch (RasterFormatException e) {                        //todo fix this
                        log.warn("cba rn, x:{} y:{} scale:{}", x, y, scale);
//                        log.debug("RasterFormatException", e);
                        continue;
                    }

                    if (image == null) continue;

                    var fileName = String.format("%d-%d-%d.png", plane, x / (SIZE * scale), y / (SIZE * scale));
                    var file = new File(dir, fileName);
                    ImageIO.write(toBufferedImage(image), "png", file);
                }
            }
        }
    }

    public static BufferedImage generateFullComponentsImage(boolean forBlocked, int plane, ComponentGrid componentGrid) {
        var grid = componentGrid.planes()[plane];
        var width = grid.length;
        var height = grid[0].length;

        Preconditions.checkArgument(width % Util.REGION_SIZE == 0, "grid width must be multiple of {} (region size), found {}", Util.REGION_SIZE, width);
        Preconditions.checkArgument(height % Util.REGION_SIZE == 0, "grid height must be multiple of {} (region size), found {}", Util.REGION_SIZE, height);

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

    public static BufferedImage generateFullMapImage(int plane, File cacheDir, File xteasJson) throws IOException {
        var xteaKeyManager = new XteaKeyManager();
        try (var is = new FileInputStream(xteasJson)) {
            xteaKeyManager.loadKeys(is);
        }

        try (var store = new Store(cacheDir)) {
            store.load();
            var mapImageDumper = new MapImageDumper(store, xteaKeyManager);
            mapImageDumper.setRenderIcons(false);
            mapImageDumper.setTransparency(true);
            mapImageDumper.load();
            return mapImageDumper.drawMap(plane);
        }
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
    //https://stackoverflow.com/questions/13605248/java-converting-image-to-bufferedimage
    public static BufferedImage toBufferedImage(Image img) {
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
}