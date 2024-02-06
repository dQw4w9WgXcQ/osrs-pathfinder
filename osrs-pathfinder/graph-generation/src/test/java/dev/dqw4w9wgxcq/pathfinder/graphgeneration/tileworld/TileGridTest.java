package dev.dqw4w9wgxcq.pathfinder.graphgeneration.tileworld;

import dev.dqw4w9wgxcq.pathfinder.commons.TileFlags;
import dev.dqw4w9wgxcq.pathfinder.graphgeneration.GridTestUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
public class TileGridTest {
    private static final int X = 2;
    private static final int Y = 5;

    @Test
    void haveDataFlag() {
        var grid = createGrid();

        log.debug("\n" + GridTestUtil.stringify(grid));

        assertTrue(grid.canTravelInDirection(X, Y, 1, 0));
    }

    @Test
    void wallFlag() {
        var grid = createGrid();

        grid.markTile(X, Y, TileFlags.E_WALL);

        log.debug("\n" + GridTestUtil.stringify(grid));

        assertFalse(grid.canTravelInDirection(X, Y, 1, 0));
    }

    @Test
    void opposingWallFlag() {
        var grid = createGrid();

        grid.markTile(X + 1, Y, TileFlags.W_WALL);

        log.debug("\n" + GridTestUtil.stringify(grid));
        assertFalse(grid.canTravelInDirection(X, Y, 1, 0));
    }

    @Test
    void diagonalWallFlag() {
        var grid = createGrid();
        grid.markTile(X, Y, TileFlags.NE_WALL);

        log.debug("\n" + GridTestUtil.stringify(grid));

        assertFalse(grid.canTravelInDirection(X, Y, 1, 1));
        assertTrue(grid.canTravelInDirection(X, Y, 1, 0));
        assertTrue(grid.canTravelInDirection(X, Y, 0, 1));
    }

    @Test
    void opposingDiagonalWallFlag() {
        var grid = createGrid();
        grid.markTile(X + 1, Y + 1, TileFlags.SW_WALL);

        log.debug("\n" + GridTestUtil.stringify(grid));
        assertFalse(grid.canTravelInDirection(X, Y, 1, 1));
        assertTrue(grid.canTravelInDirection(X, Y, 1, 0));
        assertTrue(grid.canTravelInDirection(X, Y, 0, 1));
    }

    @Test
    void diagonalWithAdjacentWallFlag() {
        var grid = createGrid();
        grid.markTile(X, Y, TileFlags.N_WALL);

        log.debug("\n" + GridTestUtil.stringify(grid));

        assertFalse(grid.canTravelInDirection(X, Y, 1, 1));
    }

    @Test
    void diagonalWithOpposingAdjacentWallFlag() {
        var grid = createGrid();
        grid.markTile(X + 1, Y + 1, TileFlags.S_WALL);

        log.debug("\n" + GridTestUtil.stringify(grid));

        assertFalse(grid.canTravelInDirection(X, Y, 1, 1));
    }

    @Test
    void nonBlockingWallFlag() {
        var grid = createGrid();
        grid.markTile(X, Y, TileFlags.N_WALL);

        log.debug("\n" + GridTestUtil.stringify(grid));

        assertTrue(grid.canTravelInDirection(X, Y, 1, 0));
    }

    @Test
    void nonBlockingOpposingWallFlag() {
        var grid = createGrid();
        grid.markTile(X + 1, Y, TileFlags.S_WALL);

        log.debug("\n" + GridTestUtil.stringify(grid));

        assertTrue(grid.canTravelInDirection(X, Y, 1, 0));
    }

    @Test
    void nonBlockingDiagonalWallFlag() {
        var grid = createGrid();
        grid.markTile(X, Y, TileFlags.SE_WALL);

        log.debug("\n" + GridTestUtil.stringify(grid));

        assertTrue(grid.canTravelInDirection(X, Y, 1, 1));
    }

    @Test
    void nonBlockingOpposingDiagonalWallFlag() {
        var grid = createGrid();
        grid.markTile(X + 1, Y + 1, TileFlags.SE_WALL);

        log.debug("\n" + GridTestUtil.stringify(grid));

        assertTrue(grid.canTravelInDirection(X, Y, 1, 1));
    }

    @Test
    void objectFlag() {
        var grid = createGrid();

        grid.markObject(X, Y, 2, 3, false);

        log.debug("\n" + GridTestUtil.stringify(grid));

        assertFalse(grid.canTravelInDirection(X - 1, Y, 1, 0));
        assertFalse(grid.canTravelInDirection(X, Y, 1, 0));
    }

    TileGrid createGrid() {
        var grid = new TileGrid(10, 10);
        for (var x = 0; x < grid.getWidth(); x++) {
            for (var y = 0; y < grid.getHeight(); y++) {
                grid.markTile(x, y, TileFlags.HAVE_DATA);
            }
        }
        return grid;
    }
}
