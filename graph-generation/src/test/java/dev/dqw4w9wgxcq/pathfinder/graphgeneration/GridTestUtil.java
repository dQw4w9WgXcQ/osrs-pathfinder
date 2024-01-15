package dev.dqw4w9wgxcq.pathfinder.graphgeneration;

import dev.dqw4w9wgxcq.pathfinder.commons.TileFlags;
import dev.dqw4w9wgxcq.pathfinder.graphgeneration.tileworld.TileGrid;

public class GridTestUtil {
    public static String stringify(TileGrid grid) {
        return stringify(toDescriptions(grid.getTileArray()));
    }

    public static String stringify(int[][] planes) {
        return stringify(toStrings(planes));
    }

    private static String stringify(String[][] planes) {
        // the world is described as from bottom left not top left
        var sb = new StringBuilder();
        for (var y = planes[0].length - 1; y >= 0; y--) {
            for (var strings : planes) {
                sb.append(strings[y]);
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    // if an int is -1, will be replaced with "-"
    private static String[][] toStrings(int[][] planes) {
        var strings = new String[planes.length][planes[0].length];
        for (var x = 0; x < planes.length; x++) {
            for (var y = 0; y < planes[0].length; y++) {
                strings[x][y] = planes[x][y] == -1 ? "-" : String.valueOf(planes[x][y]);
            }
        }
        return pad(strings);
    }

    private static String[][] toDescriptions(int[][] planes) {
        var descs = new String[planes.length][planes[0].length];
        for (var x = 0; x < planes.length; x++) {
            for (var y = 0; y < planes[0].length; y++) {
                var i = planes[x][y];
                var desc = TileFlags.describe(i);
                if (desc.length() > 20) {
                    desc = i + "";
                }

                descs[x][y] = desc;
            }
        }

        return pad(descs);
    }

    private static String[][] pad(String[][] planes) {
        var paddingSize = 0;
        for (var row : planes) {
            for (var s : row) {
                paddingSize = Math.max(paddingSize, s.length());
            }
        }

        var padded = new String[planes.length][planes[0].length];
        for (var x = 0; x < planes.length; x++) {
            for (var y = 0; y < planes[0].length; y++) {
                var s = planes[x][y];
                var padding = " ".repeat(paddingSize - s.length() + 1);
                padded[x][y] = s + padding;
            }
        }

        return padded;
    }
}
