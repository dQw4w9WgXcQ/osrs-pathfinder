package github.dqw4w9wgxcq.pathfinder.graphgeneration.gridworld;

import github.dqw4w9wgxcq.pathfinder.graphgeneration.commons.RegionUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

@Slf4j
public class ContiguousComponentsTest {
    @Test
    public void testFindIn() {
        var grid = new TileGrid(RegionUtil.SIZE, RegionUtil.SIZE);
        grid.markLoaded(0,0);

        var s = GridWorldTestUtil.stringify(grid);

        log.debug("\n" + s);
        System.out.println("\n" + s);

        var components = ContiguousComponents.findIn(grid);

        var stringify = GridWorldTestUtil.stringify(components.map());
        log.debug("\n" + stringify);
        System.out.println("components: " + "\n" + stringify);

        log.debug("components: {}", components);

        Assertions.assertEquals(components.sizes().size(), 5);
    }
}
