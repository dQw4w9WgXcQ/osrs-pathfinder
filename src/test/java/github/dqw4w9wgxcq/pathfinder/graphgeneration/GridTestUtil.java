package github.dqw4w9wgxcq.pathfinder.graphgeneration;

import github.dqw4w9wgxcq.pathfinder.graphgeneration.tileworld.TileFlags;
import github.dqw4w9wgxcq.pathfinder.graphgeneration.tileworld.TileGrid;

public class GridTestUtil {
    public static String stringify(TileGrid grid) {
        return stringify(toDescriptions(grid.getTileArray()));
    }

    public static String stringify(int[][] map) {
        return stringify(toStrings(map));
    }

    private static String stringify(String[][] map) {
        //the world is described as from bottom left not top left
        var sb = new StringBuilder();
        for (var y = map[0].length - 1; y >= 0; y--) {
            for (var strings : map) {
                sb.append(strings[y]);
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    //if an int is -1, will be replaced with "-"
    private static String[][] toStrings(int[][] map) {
        var strings = new String[map.length][map[0].length];
        for (var x = 0; x < map.length; x++) {
            for (var y = 0; y < map[0].length; y++) {
                strings[x][y] = map[x][y] == -1 ? "-" : String.valueOf(map[x][y]);
            }
        }
        return pad(strings);
    }

    private static String[][] toDescriptions(int[][] map) {
        var descs = new String[map.length][map[0].length];
        for (var x = 0; x < map.length; x++) {
            for (var y = 0; y < map[0].length; y++) {
                var i = map[x][y];
                var desc = TileFlags.describe(i);
                if (desc.length() > 20) {
                    desc = i + "";
                }

                descs[x][y] = desc;
            }
        }

        return pad(descs);
    }

    private static String[][] pad(String[][] map) {
        var paddingSize = 0;
        for (var row : map) {
            for (var s : row) {
                paddingSize = Math.max(paddingSize, s.length());
            }
        }

        var padded = new String[map.length][map[0].length];
        for (var x = 0; x < map.length; x++) {
            for (var y = 0; y < map[0].length; y++) {
                var s = map[x][y];
                var padding = " ".repeat(paddingSize - s.length() + 1);
                padded[x][y] = s + padding;
            }
        }

        return padded;
    }
}
