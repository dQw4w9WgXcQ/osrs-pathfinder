package github.dqw4w9wgxcq.pathfinder.graphgeneration.gridworld;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class TileGridTest {
    @Test
    void testEmpty() {
        var grid = initGrid();

        log.info("\n" + stringify(pad(toNames(grid.flags))));

        Assertions.assertTrue(grid.canTravelInDirection(5, 5, 1, 0));
    }

    @Test
    void testWall() {
        var grid = initGrid();

        grid.addFlag(5, 5, TileFlags.E);

        log.info("\n" + stringify(pad(toNames(grid.flags))));

        Assertions.assertFalse(grid.canTravelInDirection(5, 5, 1, 0));
    }

    static TileGrid initGrid() {
        return new TileGrid(10, 10);
    }

    static String stringify(List<List<String>> s) {
        var sb = new StringBuilder();
        for (var row : s) {
            for (var col : row) {
                sb.append(col);
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    static List<List<String>> pad(List<List<String>> columns) {
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

    static List<List<String>> toStringLists(int[][] flags) {
        var stringColumns = new ArrayList<List<String>>();
        for (var column : flags) {
            var row = new ArrayList<String>();
            for (var flag : column) {
                var flagString = String.valueOf(flag);
                row.add(flagString);
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
                var flagString = TileFlags.getFlagNames(flag);
                nameRows.add(String.join(",", flagString));
            }

            nameColumns.add(nameRows);
        }

        return nameColumns;
    }
}
