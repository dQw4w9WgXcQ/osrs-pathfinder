package github.dqw4w9wgxcq.pathfinder.graphgeneration.gridworld;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class PlaneGridTest {
    int X = 2;
    int Y = 7;

    @Test
    void testEmpty() {
        var grid = newGrid();

        log.info("\n" + stringify(pad(toNames(grid.flags))));

        Assertions.assertTrue(grid.canTravelInDirection(X, Y, 1, 0));
    }

    @Test
    void testWallFlag() {
        var grid = newGrid();

        grid.addFlag(X, Y, CollisionFlags.E);

        log.info("\n" + stringify(pad(toNames(grid.flags))));

        Assertions.assertFalse(grid.canTravelInDirection(X, Y, 1, 0));
    }

    @Test
    void testObjectFlag() {
        var grid = newGrid();

        grid.addFlag(X + 1, Y, CollisionFlags.OBJECT);

        log.info("\n" + stringify(pad(toNames(grid.flags))));

        Assertions.assertFalse(grid.canTravelInDirection(X, Y, 1, 0));
    }

    static PlaneGrid newGrid() {
        return new PlaneGrid(10, 10);
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
                var desc = CollisionFlags.toDescription(flag);
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
