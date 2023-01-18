package github.dqw4w9wgxcq.pathfinder.graphgeneration.component;

import github.dqw4w9wgxcq.pathfinder.graphgeneration.GridTestUtil;
import github.dqw4w9wgxcq.pathfinder.graphgeneration.commons.Util;
import github.dqw4w9wgxcq.pathfinder.graphgeneration.tileworld.TileFlags;
import github.dqw4w9wgxcq.pathfinder.graphgeneration.tileworld.TileGrid;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
public class ContiguousComponentsTest {
    @Test
    void wall() {
        var grid = createGrid();
        grid.markTile(0, 0, TileFlags.E_WALL);
        grid.markTile(0, 0, TileFlags.N_WALL);
        grid.markTile(1, 0, TileFlags.W_WALL);
        grid.markTile(0, 1, TileFlags.S_WALL);

        var planes = new TileGrid[]{grid};

        var s = GridTestUtil.stringify(planes[0]);
        log.debug("\n" + s);

        var components = ContiguousComponents.create(planes);

        var s2 = GridTestUtil.stringify(components.planes()[0]);
        log.debug("\n" + s2);

        log.debug("components: {}", components);

        var sum = components.sizes().stream().mapToInt(i -> i).sum();
        assertEquals(planes[0].getSizeX() * planes[0].getSizeY(), sum);

        assertEquals(2, components.count());
    }

    @Test
    void diagonalWall() {
        var grid = createGrid();

        grid.markTile(0, 2, TileFlags.OBJECT);
        grid.markTile(1, 1, TileFlags.OBJECT);
        grid.markTile(2, 0, TileFlags.OBJECT);
        grid.markTile(0, 1, TileFlags.NE_WALL);
        grid.markTile(1, 0, TileFlags.NE_WALL);
        grid.markTile(1, 2, TileFlags.SW_WALL);
        grid.markTile(2, 1, TileFlags.SW_WALL);

        var s = GridTestUtil.stringify(grid);
        log.debug("\n" + s);

        var components = ContiguousComponents.create(new TileGrid[]{grid});

        var s2 = GridTestUtil.stringify(components.planes()[0]);
        log.debug("\n" + s2);

        log.debug("components: {}", components);

        var sum = components.sizes().stream().mapToInt(i -> i).sum();
        assertEquals(grid.getSizeX() * grid.getSizeY() - 3, sum);

        assertEquals(2, components.count());
    }

    @Test
    void object() {
        var grid = createGrid();
        grid.markAreaObject(0, 1, 1, 1, true);
        grid.markAreaObject(1, 0, 1, 1, false);
        grid.markAreaObject(1, 1, 1, 1, true);
        var components = ContiguousComponents.create(new TileGrid[]{grid});
        var s = GridTestUtil.stringify(components.planes()[0]);
        log.debug("\n{}", s);

        assertEquals(2, components.count());
    }

    @Test
    void noData() {
        var grid = new TileGrid(Util.REGION_SIZE, Util.REGION_SIZE);
        grid.markAreaObject(0, 1, 1, 1, true);
        grid.markAreaObject(1, 0, 1, 1, false);
        grid.markAreaObject(1, 1, 1, 1, true);
        var components = ContiguousComponents.create(new TileGrid[]{grid});
        var s = GridTestUtil.stringify(components.planes()[0]);
        log.debug("\n{}", s);

        assertEquals(0, components.sizes().stream().mapToInt(i -> i).sum());

        assertEquals(0, components.count());
    }

    private TileGrid createGrid() {
        var grid = new TileGrid(Util.REGION_SIZE, Util.REGION_SIZE);
        grid.markRegionHaveData(0, 0);
        return grid;
    }
}
