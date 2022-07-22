package github.dqw4w9wgxcq.pathfinder.graphgeneration.leafletimages;

import com.google.common.base.Preconditions;
import github.dqw4w9wgxcq.pathfinder.graphgeneration.Graph;
import github.dqw4w9wgxcq.pathfinder.graphgeneration.commons.RegionUtil;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * creates images used by <a href="https://leafletjs.com/">leaflet</a> in visualizer website
 */
public class LeafletImages {
    public static final int MAX_ZOOM = 1;
    public static final int MIN_ZOOM = -3;

    public static void write(Graph graph, File outDir) throws IOException {
        var componentsDir = new File(outDir, "components");
        componentsDir.mkdirs();

        var planes = graph.components();
        for (var plane = 0; plane < planes.size(); plane++) {
            var components = planes.get(plane);
            var map = components.map();
            var mapWidth = map.length;
            var mapHeight = map[0].length;

            Preconditions.checkArgument(mapWidth % RegionUtil.SIZE == 0, "map width must be multiple of {} (region size), found {}", RegionUtil.SIZE, mapWidth);
            Preconditions.checkArgument(mapHeight % RegionUtil.SIZE == 0, "map height must be multiple of {} (region size), found {}", RegionUtil.SIZE, mapHeight);

            var imgSize = RegionUtil.SIZE * 4;

            var width = mapWidth / imgSize;
            var height = mapHeight / imgSize;

            for (var imgX = 0; imgX < width; imgX++) {
                for (var imgY = 0; imgY < height; imgY++) {
                    var img = new BufferedImage(imgSize, imgSize, BufferedImage.TYPE_INT_ARGB);
                    var isBlank = true;
                    var startX = imgX * imgSize;
                    var startY = imgY * imgSize;

                    var endX = Math.min(startX + imgSize, mapWidth);
                    var endY = Math.min(startY + imgSize, mapHeight);

                    for (var x = startX; x < endX; x++) {
                        for (var y = startY; y < endY; y++) {
                            var id = map[x][y];
                            if (id != -1) {
                                isBlank = false;
                                var pixelX = x - startX;
                                var pixelY = y - startY;
                                img.setRGB(pixelX, (imgSize - pixelY) - 1, getColor(id));
                            }
                        }
                    }

                    if (!isBlank) {
                        ImageIO.write(img, "png", new File(componentsDir, String.format("%d-%d-%d.png", plane, imgX, imgY)));
                    }
                }
            }
        }
    }

    private static int getColor(int id) {
        return COLORS[id % COLORS.length];
    }

    private static final int[] COLORS = {
            Color.GRAY.getRGB(),
            Color.BLUE.getRGB(),
            Color.RED.getRGB(),
            Color.YELLOW.getRGB(),
            Color.GREEN.getRGB(),
            Color.CYAN.getRGB(),
            Color.MAGENTA.getRGB(),
            Color.ORANGE.getRGB(),
            Color.PINK.getRGB(),
            Color.WHITE.getRGB(),
    };
}
