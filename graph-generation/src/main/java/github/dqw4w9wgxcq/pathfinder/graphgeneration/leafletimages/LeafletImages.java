package github.dqw4w9wgxcq.pathfinder.graphgeneration.leafletimages;

import com.google.common.base.Preconditions;
import github.dqw4w9wgxcq.pathfinder.pathfinding.domain.ComponentGrid;
import github.dqw4w9wgxcq.pathfinder.graphgeneration.commons.Util;
import github.dqw4w9wgxcq.pathfinder.graphgeneration.tileworld.TileWorld;
import lombok.extern.slf4j.Slf4j;
import net.runelite.cache.MapImageDumper;
import net.runelite.cache.fs.Store;
import net.runelite.cache.util.XteaKeyManager;

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

    public static void write(File outDir, File cacheDir, File xteasJson, ComponentGrid components) throws IOException {
        log.info("Writing leaflet images to {}", outDir);

        for (int plane = 0; plane < TileWorld.PLANES_SIZE; plane++) {
            var componentsImage = generateFullComponentsImage(plane, false, components);
            var dir = new File(outDir, "component");
            writeLeafletImages(componentsImage, dir, 1);
        }

        for (int plane = 0; plane < TileWorld.PLANES_SIZE; plane++) {
            var blockedImage = generateFullComponentsImage(plane, true, components);
            var dir = new File(outDir, "blocked");
            writeLeafletImages(blockedImage, dir, 1);
        }

        for (int plane = 0; plane < TileWorld.PLANES_SIZE; plane++) {
            var mapImage = generateFullMapImage(plane, cacheDir, xteasJson);
            var dir = new File(outDir, "map");
            writeLeafletImages(mapImage, dir, 4);
        }
    }

    private static BufferedImage generateLeafletImage(BufferedImage fullImage, int scale, int x, int y) {
        return (BufferedImage) fullImage.getSubimage(x, y, SIZE * scale, SIZE * scale).getScaledInstance(SIZE, SIZE, Image.SCALE_SMOOTH);
    }

    private static void writeLeafletImages(BufferedImage fullImage, File outDir, int tilePxSize) throws IOException {
        double maxZoomD = Math.log(tilePxSize) / Math.log(2);//java doesn't have log2
        Preconditions.checkArgument(maxZoomD % 1 == 0, "tilePxSize must be a power of 2");
        int maxZoom = (int) maxZoomD;
        for (int zoom = MIN_ZOOM; zoom <= maxZoom; zoom++) {
            if (zoom != maxZoom) continue;//todo temp

            var zoomDir = new File(outDir, String.valueOf(zoom));
            //noinspection ResultOfMethodCallIgnored
            zoomDir.mkdirs();

            for (int x = 0; x < fullImage.getWidth(); x += SIZE * tilePxSize) {
                for (int y = 0; y < fullImage.getHeight(); y += SIZE * tilePxSize) {
                    var image = generateLeafletImage(fullImage, tilePxSize, x, y);
                    var fileName = String.format("%d_%d.png", x / (SIZE * tilePxSize), y / (SIZE * tilePxSize));
                    var file = new File(zoomDir, fileName);
                    ImageIO.write(image, "png", file);
                }
            }
        }
    }

    public static BufferedImage generateFullComponentsImage(int plane, boolean forBlocked, ComponentGrid components) {
        var grid = components.planes()[plane];
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
}
