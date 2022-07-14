package github.dqw4w9wgxcq.pathfinder.graphgeneration.gridworld;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

@Slf4j
public class ConnectedComponentsTest {
    @Test
    public void testFindIn() {

        var grid = new PlaneGrid(10, 10);

        grid.addFlag(0,1,CollisionFlags.VALID);
        grid.addFlag(1,1,CollisionFlags.VALID);
        grid.addFlag(2, 2, CollisionFlags.VALID);
        grid.addFlag(2, 3, CollisionFlags.VALID);
        grid.addFlag(5, 5, CollisionFlags.VALID);
        grid.addFlag(4, 4, CollisionFlags.VALID);
        grid.addFlag(7, 6, CollisionFlags.VALID);
        grid.addFlag(7, 7, CollisionFlags.VALID | CollisionFlags.OBJECT);
        grid.addFlag(7, 8, CollisionFlags.VALID);

        var s = GridWorldTestUtil.stringify(grid);

        log.debug("\n" + s);

        var components = ConnectedComponents.findIn(grid);

        var stringify = GridWorldTestUtil.stringify(components.map());
        log.debug("\n" + stringify);

        log.debug("components: {}", components);

        Assertions.assertEquals(components.count(), 4);

        //not sure if this is a good idea
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
