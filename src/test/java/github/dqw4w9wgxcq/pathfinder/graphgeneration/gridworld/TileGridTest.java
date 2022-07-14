package github.dqw4w9wgxcq.pathfinder.graphgeneration.gridworld;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

@Slf4j
public class TileGridTest {
    int X = 5;
    int Y = 5;

    @Test
    void testValid() {
        var grid = newGrid();

        log.debug("\n" + GridWorldTestUtil.stringify(grid));

        Assertions.assertTrue(grid.canTravelInDirection(X, Y, 1, 0));
    }

    @Test
    void testWallFlag() {
        var grid = newGrid();

        grid.markFlag(X, Y, TileFlags.E);

        log.debug("\n" + GridWorldTestUtil.stringify(grid));

        Assertions.assertFalse(grid.canTravelInDirection(X, Y, 1, 0));
    }

    @Test
    void testDiagonalWallFlag() {
        var grid = newGrid();
        grid.markFlag(X, Y, TileFlags.NE);
        grid.markFlag(X + 1, Y + 1, TileFlags.SW);

        log.debug("\n" + GridWorldTestUtil.stringify(grid));

        Assertions.assertFalse(grid.canTravelInDirection(X, Y, 1, 1));
        Assertions.assertTrue(grid.canTravelInDirection(X, Y, 1, 0));
        Assertions.assertTrue(grid.canTravelInDirection(X, Y, 0, 1));
    }

    @Test
    void testObjectFlag() {
        var grid = newGrid();

        grid.markFlag(X + 1, Y, TileFlags.OBJECT);

        log.debug("\n" + GridWorldTestUtil.stringify(grid));

        Assertions.assertFalse(grid.canTravelInDirection(X, Y, 1, 0));
    }

    TileGrid newGrid() {
        var grid = new TileGrid(10, 10);
        for (var x = 0; x < grid.getSizeX(); x++) {
            for (var y = 0; y < grid.getSizeY(); y++) {
                grid.markFlag(x, y, TileFlags.VALID);
            }
        }
        return grid;
    }
}
