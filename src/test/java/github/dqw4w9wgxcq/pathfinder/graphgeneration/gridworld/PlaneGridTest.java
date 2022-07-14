package github.dqw4w9wgxcq.pathfinder.graphgeneration.gridworld;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

@Slf4j
public class PlaneGridTest {
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

        grid.addFlag(X, Y, CollisionFlags.E);

        log.debug("\n" + GridWorldTestUtil.stringify(grid));

        Assertions.assertFalse(grid.canTravelInDirection(X, Y, 1, 0));
    }

    @Test
    void testObjectFlag() {
        var grid = newGrid();

        grid.addFlag(X + 1, Y, CollisionFlags.OBJECT);

        log.debug("\n" + GridWorldTestUtil.stringify(grid));

        Assertions.assertFalse(grid.canTravelInDirection(X, Y, 1, 0));
    }

    PlaneGrid newGrid() {
        return new PlaneGrid(10, 10);
    }
}
