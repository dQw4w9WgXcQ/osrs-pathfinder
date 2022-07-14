package github.dqw4w9wgxcq.pathfinder.graphgeneration.gridworld;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

@Slf4j
public class ContiguousComponentsTest {
    @Test
    public void testFindIn() {
        var grid = new TileGrid(10, 10);

        grid.addFlag(0, 1, TileFlags.VALID);
        grid.addFlag(1, 1, TileFlags.VALID);
        grid.addFlag(2, 2, TileFlags.VALID);
        grid.addFlag(2, 3, TileFlags.VALID);
        grid.addFlag(5, 5, TileFlags.VALID);
        grid.addFlag(4, 4, TileFlags.VALID);
        grid.addFlag(7, 6, TileFlags.VALID);
        grid.addFlag(7, 7, TileFlags.VALID | TileFlags.OBJECT);
        grid.addFlag(7, 8, TileFlags.VALID);

        var s = GridWorldTestUtil.stringify(grid);

        log.debug("\n" + s);

        var components = ContiguousComponents.findIn(grid);

        var stringify = GridWorldTestUtil.stringify(components.map());
        log.debug("\n" + stringify);

        log.debug("components: {}", components);

        Assertions.assertEquals(components.sizes().size(), 4);

        //not sure if this is a good idea
        //maybe ok if move TileFlags.descriptions to test package
//        Assertions.assertEquals(stringify, """
//                 - - - - - - - - - -
//                 - - - - - - - - - -
//                 - - 0 0 - - - - - -
//                 - - - - - - - - - -
//                 - - - - 1 - - - - -
//                 - - - - - 1 - - - -
//                 - - - - - - - - - -
//                 - - - - - - 2 - 3 -
//                 - - - - - - - - - -
//                 - - - - - - - - - -
//                """);
    }
}
