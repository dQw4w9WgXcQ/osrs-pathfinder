package github.dqw4w9wgxcq.pathfinder.pathfinding;

import github.dqw4w9wgxcq.pathfinder.domain.Point;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Nullable;

import java.util.*;

@Slf4j
public class Algo {
    public static int chebyshev(Point from, Point to) {
        return Math.max(Math.abs(from.x() - to.x()), Math.abs(from.y() - to.y()));
    }

    /**
     * @return the path or null if not reachable
     */
    public static @Nullable List<Point> bfs(int[][] grid, Point from, Point to) {
        var fromX = from.x();
        var fromY = from.y();
        var fromPacked = from.pack();
        var toPacked = to.pack();

        if (grid[fromX][fromY] != grid[to.x()][to.y()]) {
            return null;
        }

        if (from.equals(to)) {
            return List.of(to);
        }

        var gridWidth = grid.length;
        var gridHeight = grid[0].length;

        var seenFrom = new HashMap<Integer, Integer>();
        var frontier = new ArrayDeque<Integer>();

        frontier.add(fromPacked);

        while (!frontier.isEmpty()) {
            var curr = frontier.pop();
            if (curr.equals(toPacked)) {
                var path = new ArrayList<Point>();
                while (!curr.equals(fromPacked)) {
                    path.add(Point.unpack(curr));
                    curr = seenFrom.get(curr);
                }

                path.add(from);

                Collections.reverse(path);
                return path;
            }

            var currX = Point.unpackX(curr);
            var currY = Point.unpackY(curr);

            for (var dx = -1; dx <= 1; dx++) {
                for (var dy = -1; dy <= 1; dy++) {
                    if (dx == 0 && dy == 0) {
                        continue;
                    }

                    var adjacentX = currX + dx;
                    var adjacentY = currY + dy;

                    if (adjacentX < 0 || adjacentX >= gridWidth || adjacentY < 0 || adjacentY >= gridHeight) {
                        continue;
                    }

                    if (grid[adjacentX][adjacentY] != grid[from.x()][from.y()]) {
                        continue;
                    }

                    var adjacent = Point.pack(adjacentX, adjacentY);
                    if (seenFrom.containsKey(adjacent)) {
                        continue;
                    }

                    seenFrom.put(adjacent, curr);
                    frontier.add(adjacent);
                }
            }
        }

        throw new IllegalStateException("from and to are from the same component, but no path was found");
    }

//    public static Map<Point, Integer> distances(int[][] grid, Point from, Set<Point> tos) {
//        var fromPacked = from.pack();
//        var tosPacked = new BitSet();
//        for (var to : tos) {
//            tosPacked.set(to.pack());
//        }
//
//        var distances = new HashMap<Point, Integer>();
//
//        var seen = new BitSet();
//        var frontier = new ArrayDeque<Integer>();
//
//        seen.set(fromPacked);
//        frontier.add(fromPacked);
//        var distance = 0;
//        while (!frontier.isEmpty() && !tos.isEmpty()) {
//            var newFrontier = new ArrayDeque<Integer>();
//            while (!frontier.isEmpty()) {
//                var curr = frontier.poll();
//                if (tosPacked.get(curr)) {
//                    distances.put(Point.unpack(curr), distance);
//                    tosPacked.clear(curr);
//                }
//
//                var currX = Point.unpackX(curr);
//                var currY = Point.unpackY(curr);
//
//                for (var dx = -1; dx <= 1; dx++) {
//                    for (var dy = -1; dy <= 1; dy++) {
//                        if (dx == 0 && dy == 0) {
//                            continue;
//                        }
//
//                        var adjacentX = currX + dx;
//                        var adjacentY = currY + dy;
//
//                        if (adjacentX < 0 || adjacentX >= grid.length || adjacentY < 0 || adjacentY >= grid[0].length) {
//                            continue;
//                        }
//
//                        if (grid[adjacentX][adjacentY] != grid[from.x()][from.y()]) {
//                            continue;
//                        }
//
//                        var adjacent = Point.pack(adjacentX, adjacentY);
//                        if (seen.get(adjacent)) {
//                            continue;
//                        }
//
//                        seen.set(adjacent);
//                        newFrontier.add(adjacent);
//                    }
//                }
//            }
//
//            frontier.addAll(newFrontier);
//            distance++;
//        }
//
//        if (tos.size() != distances.size()) {
//            throw new IllegalArgumentException("not in componenet");
//        }
//
//        return distances;
//    }
}