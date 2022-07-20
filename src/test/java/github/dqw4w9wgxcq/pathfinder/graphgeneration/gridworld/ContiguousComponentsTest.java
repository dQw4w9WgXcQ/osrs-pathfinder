package github.dqw4w9wgxcq.pathfinder.graphgeneration.gridworld;

import github.dqw4w9wgxcq.pathfinder.graphgeneration.commons.RegionUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

@Slf4j
public class ContiguousComponentsTest {
    @Test
    public void wall() {
        var grid = newGrid();

        grid.markTile(0, 0, TileFlags.E);
        grid.markTile(0, 0, TileFlags.N);
        grid.markTile(1, 0, TileFlags.W);
        grid.markTile(0, 1, TileFlags.S);

        var s = GridWorldTestUtil.stringify(grid);
        log.debug("\n" + s);

        var components = ContiguousComponents.findIn(grid);

        var s2 = GridWorldTestUtil.stringify(components.map());
        log.debug("\n" + s2);

        log.debug("components: {}", components);

        var sum = components.sizes().stream().mapToInt(Integer::intValue).sum();
        Assertions.assertEquals(grid.getSizeX() * grid.getSizeY(), sum);

        Assertions.assertEquals(2, components.sizes().size());
    }

    @Test
    public void diagonalWall() {
        var grid = newGrid();

        grid.markTile(0, 2, TileFlags.OBJECT);
        grid.markTile(1, 1, TileFlags.OBJECT);
        grid.markTile(2, 0, TileFlags.OBJECT);
        grid.markTile(0, 1, TileFlags.NE);
        grid.markTile(1, 0, TileFlags.NE);
        grid.markTile(1, 2, TileFlags.SW);
        grid.markTile(2, 1, TileFlags.SW);

        var s = GridWorldTestUtil.stringify(grid);
        log.debug("\n" + s);

        var components = ContiguousComponents.findIn(grid);

        var s2 = GridWorldTestUtil.stringify(components.map());
        log.debug("\n" + s2);

        log.debug("components: {}", components);

        var sum = components.sizes().stream().mapToInt(Integer::intValue).sum();
        Assertions.assertEquals(grid.getSizeX() * grid.getSizeY() - 3, sum);

        Assertions.assertEquals(2, components.sizes().size());
    }

    @Test
    public void object() {
        var grid = newGrid();
        grid.markAreaObject(0, 1, 1, 1, true);
        grid.markAreaObject(1, 0, 1, 1, false);
        grid.markAreaObject(1, 1, 1, 1, true);
        var components = ContiguousComponents.findIn(grid);
        var s = GridWorldTestUtil.stringify(components.map());
        log.debug("\n{}", s);

        Assertions.assertEquals(2, components.sizes().size());
    }

    @Test
    void noData() {
        var grid = new TileGrid(RegionUtil.SIZE, RegionUtil.SIZE);
        grid.markAreaObject(0, 1, 1, 1, true);
        grid.markAreaObject(1, 0, 1, 1, false);
        grid.markAreaObject(1, 1, 1, 1, true);
        var components = ContiguousComponents.findIn(grid);
        var s = GridWorldTestUtil.stringify(components.map());
        log.debug("\n{}", s);

        Assertions.assertEquals(0, components.sizes().stream().mapToInt(Integer::intValue).sum());

        Assertions.assertEquals(0, components.sizes().size());
    }

    private TileGrid newGrid() {
        var grid = new TileGrid(RegionUtil.SIZE, RegionUtil.SIZE);
        grid.markHaveData(0, 0);
        return grid;
    }
}
