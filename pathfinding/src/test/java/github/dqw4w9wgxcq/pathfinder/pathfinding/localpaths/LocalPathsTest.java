package github.dqw4w9wgxcq.pathfinder.pathfinding.localpaths;

import github.dqw4w9wgxcq.pathfinder.commons.domain.Point;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LocalPathsTest {

    @Test
    void minifyPath() {
        var path = List.of(
                new Point(0, 0),
                new Point(1, 1),
                new Point(2, 2),
                new Point(3, 3),
                new Point(4, 4)
        );

        assertEquals(
                List.of(
                        new Point(0, 0),
                        new Point(4, 4)
                ),
                LocalPaths.minifyPath(path)
        );

        var path2 = List.of(
                new Point(0, 0),
                new Point(1, 0),
                new Point(2, 0),
                new Point(3, 1),
                new Point(4, 2)
        );

        assertEquals(
                List.of(
                        new Point(0, 0),
                        new Point(2, 0),
                        new Point(4, 2)
                ),
                LocalPaths.minifyPath(path2)
        );

        var path3 = List.of(
                new Point(0, 0),
                new Point(1, 0),
                new Point(2, 0),
                new Point(3, 1),
                new Point(4, 2),
                new Point(5, 2)
        );

        assertEquals(
                List.of(
                        new Point(0, 0),
                        new Point(2, 0),
                        new Point(4, 2),
                        new Point(5, 2)
                ),
                LocalPaths.minifyPath(path3)
        );
    }
}