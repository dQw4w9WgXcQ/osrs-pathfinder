package github.dqw4w9wgxcq.pathfinder.graphgeneration.tileworld;

import github.dqw4w9wgxcq.pathfinder.pathfinding.PathfindingGrid;
import github.dqw4w9wgxcq.pathfinder.pathfinding.domain.TileFlags;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
class TileGridToPathfindingGridTest {
    @Test
    void empty() {
        var tg = createTileGrid();
        var pathfindingGrid = tg.toPathfindingGrid();
        assertTrue((pathfindingGrid.grid()[0][0] & PathfindingGrid.EAST) != 0);
    }

    @Test
    void bounds() {
        var tg = createTileGrid();

        var pathfindingGrid = tg.toPathfindingGrid();
        assertEquals(0, pathfindingGrid.grid()[0][0] & PathfindingGrid.WEST);
    }

    @Test
    void wall() {
        var tg = createTileGrid();

        tg.markTile(0, 0, TileFlags.N_WALL);

        var pathfindingGrid = tg.toPathfindingGrid();

        assertEquals(0, pathfindingGrid.grid()[0][0] & PathfindingGrid.NORTH);
    }

    @Test
    void opposingWall() {
        var tg = createTileGrid();

        tg.markTile(0, 1, TileFlags.S_WALL);

        var pathfindingGrid = tg.toPathfindingGrid();

        assertEquals(0, pathfindingGrid.grid()[0][0] & PathfindingGrid.NORTH);
    }

    @Test
    void diagonalWall() {
        var tg = createTileGrid();

        tg.markTile(0, 0, TileFlags.NE_WALL);

        var pathfindingGrid = tg.toPathfindingGrid();

        assertEquals(0, pathfindingGrid.grid()[0][0] & PathfindingGrid.NORTH_EAST);
    }

    @Test
    void diagonalOpposingWall() {
        var tg = createTileGrid();

        tg.markTile(1, 1, TileFlags.SW_WALL);

        var pathfindingGrid = tg.toPathfindingGrid();

        assertEquals(0, pathfindingGrid.grid()[0][0] & PathfindingGrid.NORTH_EAST);
    }

    @Test
    void diagonalAdjacentWall() {
        var tg = createTileGrid();

        tg.markTile(0, 0, TileFlags.E_WALL);
        var pathfindingGrid = tg.toPathfindingGrid();
        assertEquals(0, pathfindingGrid.grid()[0][0] & PathfindingGrid.NORTH_EAST);
    }

    @Test
    void diagonalAdjacentOpposingWall() {
        var tg = createTileGrid();

        tg.markTile(1, 0, TileFlags.W_WALL);

        var pathfindingGrid = tg.toPathfindingGrid();

        assertEquals(0, pathfindingGrid.grid()[0][0] & PathfindingGrid.NORTH_EAST);
    }

    @Test
    void diagonalAdjacentObject() {
        var tg = createTileGrid();

        tg.markObject(1, 0, 1, 1, false);

        var pathfindingGrid = tg.toPathfindingGrid();

        assertEquals(0, pathfindingGrid.grid()[0][0] & PathfindingGrid.NORTH_EAST);
    }

    @Test
    void noData() {
        var tg = createTileGrid();

        tg.unmarkTile(0, 1, TileFlags.HAVE_DATA);

        var pathfindingGrid = tg.toPathfindingGrid();

        assertEquals(0, pathfindingGrid.grid()[0][0] & PathfindingGrid.NORTH);
        assertEquals(0, pathfindingGrid.grid()[0][0] & PathfindingGrid.NORTH_EAST);
    }

    TileGrid createTileGrid() {
        var tileGrid = new TileGrid(5, 5);
        for (int x = 0; x < tileGrid.getWidth(); x++) {
            for (int y = 0; y < tileGrid.getHeight(); y++) {
                tileGrid.markTile(x, y, TileFlags.HAVE_DATA);
            }
        }

        return tileGrid;
    }
}