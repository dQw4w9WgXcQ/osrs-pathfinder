package github.dqw4w9wgxcq.pathfinder.graphgeneration.gridworld;

import java.util.ArrayList;
import java.util.List;

public class GridWorldTestUtil {
    static String stringify(TileGrid grid) {
        return stringify(pad(toNames(grid.flags)));
    }

    static String stringify(int[][] map) {
        return stringify(pad(toStrings(map)));
    }

    static String stringify(List<List<String>> columns) {
        var sb = new StringBuilder();
        for (var column : columns) {
            for (var row : column) {
                sb.append(row);
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    private static List<List<String>> pad(List<List<String>> columns) {
        var width = 0;
        for (var column : columns) {
            for (var s : column) {
                width = Math.max(width, s.length());
            }
        }

        var paddedColumns = new ArrayList<List<String>>();
        for (var row : columns) {
            var paddedRow = new ArrayList<String>();
            for (var flagString : row) {
                var padding = " ".repeat(width - flagString.length() + 1);
                paddedRow.add(padding + flagString);
            }
            paddedColumns.add(paddedRow);
        }
        return paddedColumns;
    }

    //if an int is -1, will be replaced with "-"
    static List<List<String>> toStrings(int[][] ints) {
        var stringColumns = new ArrayList<List<String>>();
        for (var column : ints) {
            var row = new ArrayList<String>();
            for (var i : column) {
                String s;
                if (i == -1) {
                    s = "-";
                } else {
                    s = String.valueOf(i);
                }

                row.add(s);
            }

            stringColumns.add(row);
        }

        return stringColumns;
    }

    static List<List<String>> toNames(int[][] flags) {
        var nameColumns = new ArrayList<List<String>>();
        for (var column : flags) {
            var nameRows = new ArrayList<String>();
            for (var flag : column) {
                var desc = TileFlags.describe(flag);
                if (desc.length() > 20) {
                    desc = flag + "";
                }
                nameRows.add(desc);
            }

            nameColumns.add(nameRows);
        }

        return nameColumns;
    }
}
