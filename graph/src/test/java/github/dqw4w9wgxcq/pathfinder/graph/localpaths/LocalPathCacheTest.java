package github.dqw4w9wgxcq.pathfinder.graph.localpaths;

import github.dqw4w9wgxcq.pathfinder.domain.Point;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LocalPathCacheTest {

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
                LocalPathCache.minifyPath(path)
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
                LocalPathCache.minifyPath(path2)
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
                LocalPathCache.minifyPath(path3)
        );
    }
}