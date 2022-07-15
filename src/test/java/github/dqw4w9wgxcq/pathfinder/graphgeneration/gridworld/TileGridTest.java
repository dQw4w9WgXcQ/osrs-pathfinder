package github.dqw4w9wgxcq.pathfinder.graphgeneration.gridworld;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

@Slf4j
public class TileGridTest {
    int X = 3;
    int Y = 7;

    @Test
    void testValid() {
        var grid = newGrid();

        log.debug("\n" + GridWorldTestUtil.stringify(grid));

        Assertions.assertTrue(grid.canTravelInDirection(X, Y, 1, 0));
    }

    @Test
    void testWallFlag() {
        var grid = newGrid();

        grid.markTile(X, Y, TileFlags.E);

        log.debug("\n" + GridWorldTestUtil.stringify(grid));
        System.out.println(GridWorldTestUtil.stringify(grid));

        Assertions.assertFalse(grid.canTravelInDirection(X, Y, 1, 0));
    }

    @Test
    void testDiagonalWallFlag() {
        var grid = newGrid();
        grid.markTile(X, Y, TileFlags.NW);
        grid.markTile(X + 1, Y + 1, TileFlags.SE);

        log.debug("\n" + GridWorldTestUtil.stringify(grid));
        System.out.println("\n" + GridWorldTestUtil.stringify(grid));

        Assertions.assertFalse(grid.canTravelInDirection(X, Y, 1, 1));
        Assertions.assertFalse(grid.canTravelInDirection(X, Y, 1, 0));
        Assertions.assertFalse(grid.canTravelInDirection(X, Y, 0, 1));
    }

    @Test
    void testObjectFlag() {
        var grid = newGrid();

        grid.markAreaObject(X, Y, 2, 3, false);

        log.debug("\n" + GridWorldTestUtil.stringify(grid));
        System.out.println("\n" + GridWorldTestUtil.stringify(grid));

        Assertions.assertFalse(grid.canTravelInDirection(X, Y, 1, 0));
    }

    TileGrid newGrid() {
        var grid = new TileGrid(10, 10);
        for (var x = 0; x < grid.getSizeX(); x++) {
            for (var y = 0; y < grid.getSizeY(); y++) {
                grid.markTile(x, y, TileFlags.HAVE_DATA);
            }
        }
        return grid;
    }
}
