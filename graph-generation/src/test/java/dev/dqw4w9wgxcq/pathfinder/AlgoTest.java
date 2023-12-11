package dev.dqw4w9wgxcq.pathfinder;

//import dev.dqw4w9wgxcq.pathfinder.domain.Point;
//import org.junit.jupiter.api.Test;
//
//import java.util.Arrays;
//import java.util.HashSet;
//import java.util.Set;
//
//import static org.junit.jupiter.api.Assertions.assertThrows;

class AlgoTest {
//    @Test
//    void distances() {
//        var grid = new int[10][10];
//        for (var i = 0; i < 10; i++) {
//            Arrays.fill(grid[i], -1);
//        }
//
//        grid[0][0] = 0;
//        grid[0][1] = 0;
//        grid[0][2] = 0;
//        grid[0][3] = 0;
//        grid[1][0] = 0;
//        grid[1][1] = 0;
//        grid[1][2] = 1;
//        grid[1][3] = 0;
//
//        var tos = new HashSet<Point>();
//        tos.add(new Point(0, 0));
//        tos.add(new Point(0, 1));
//        tos.add(new Point(0, 2));
//        tos.add(new Point(0, 3));
//        tos.add(new Point(1, 3));
//        var distances = Algo.distances(grid, new Point(0, 0), tos);
//
//        assertEquals(0, distances.get(new Point(0, 0)));//test origin in tos
//        assertEquals(1, distances.get(new Point(0, 1)));
//        assertEquals(2, distances.get(new Point(0, 2)));
//        assertEquals(3, distances.get(new Point(0, 3)));
//        assertEquals(3, distances.get(new Point(1, 3)));
//        assertThrows(
//                IllegalArgumentException.class,
//                () -> Algo.distances(grid, new Point(0, 0), new HashSet<>(Set.of(new Point(1, 2))))
//        );
//    }
}