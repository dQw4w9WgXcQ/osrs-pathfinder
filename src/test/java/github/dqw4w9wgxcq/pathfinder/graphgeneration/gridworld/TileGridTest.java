package github.dqw4w9wgxcq.pathfinder.graphgeneration.gridworld;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

@Slf4j
public class TileGridTest {
    int X = 2;
    int Y = 7;

    @Test
    void testEmpty() {
        var grid = newGrid();

        log.debug("\n" + GridWorldTestUtil.stringify(grid));

        Assertions.assertTrue(grid.canTravelInDirection(X, Y, 1, 0));
    }

    @Test
    void testWallFlag() {
        var grid = newGrid();

        grid.addFlag(X, Y, TileFlags.E);

        log.debug("\n" + GridWorldTestUtil.stringify(grid));

        Assertions.assertFalse(grid.canTravelInDirection(X, Y, 1, 0));
    }

    @Test
    void testObjectFlag() {
        var grid = newGrid();

        grid.addFlag(X + 1, Y, TileFlags.OBJECT);

        log.debug("\n" + GridWorldTestUtil.stringify(grid));

        Assertions.assertFalse(grid.canTravelInDirection(X, Y, 1, 0));
    }

    TileGrid newGrid() {
        return new TileGrid(10, 10);
    }
}
