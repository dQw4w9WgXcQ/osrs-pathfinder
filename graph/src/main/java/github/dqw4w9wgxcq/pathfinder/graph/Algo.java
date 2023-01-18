package github.dqw4w9wgxcq.pathfinder.graph;

import github.dqw4w9wgxcq.pathfinder.domain.Point;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public class Algo {
    public static int chebyshevDistance(Point from, Point to) {
        return Math.max(Math.abs(from.x() - to.x()), Math.abs(from.y() - to.y()));
    }

    /**
     * @return the path or null if not reachable
     */
    public static @Nullable List<Point> bfs(int[][] grid, Point from, Point to) {
        if (grid[from.x()][from.y()] != grid[to.x()][to.y()]) {
            return null;
        }

        if (from.equals(to)) {
            return List.of(to);
        }

        var seenFrom = new HashMap<Point, Point>();
        var frontier = new ArrayDeque<Point>();

        frontier.add(from);

        while (!frontier.isEmpty()) {
            var curr = frontier.poll();
            if (curr.equals(to)) {
                var path = new ArrayList<Point>();
                while (!curr.equals(from)) {
                    path.add(curr);
                    curr = seenFrom.get(curr);
                }

                Collections.reverse(path);
                return path;
            }

            for (var dx = -1; dx <= 1; dx++) {
                for (var dy = -1; dy <= 1; dy++) {
                    if (dx == 0 && dy == 0) {
                        continue;
                    }

                    var adjacentX = curr.x() + dx;
                    var adjacentY = curr.y() + dy;

                    if (adjacentX < 0 || adjacentX >= grid.length || adjacentY < 0 || adjacentY >= grid[0].length) {
                        continue;
                    }

                    if (grid[adjacentX][adjacentY] != grid[from.x()][from.y()]) {
                        continue;
                    }

                    var adjacent = new Point(adjacentX, adjacentY);
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

    public static Map<Point, Integer> distances(int[][] grid, Point from, Set<Point> tos) {
        var distances = new HashMap<Point, Integer>();
        var seen = new HashSet<Point>();
        var frontier = new ArrayDeque<Point>();

        seen.add(from);
        frontier.add(from);
        var distance = 0;
        while (!frontier.isEmpty() && !tos.isEmpty()) {
            var newFrontier = new ArrayDeque<Point>();
            while (!frontier.isEmpty()) {
                var curr = frontier.poll();
                if (tos.contains(curr)) {
                    distances.put(curr, distance);
                    tos.remove(curr);
                }

                for (var dx = -1; dx <= 1; dx++) {
                    for (var dy = -1; dy <= 1; dy++) {
                        if (dx == 0 && dy == 0) {
                            continue;
                        }

                        var adjacentX = curr.x() + dx;
                        var adjacentY = curr.y() + dy;

                        if (adjacentX < 0 || adjacentX >= grid.length || adjacentY < 0 || adjacentY >= grid[0].length) {
                            continue;
                        }

                        if (grid[adjacentX][adjacentY] != grid[from.x()][from.y()]) {
                            continue;
                        }

                        var adjacent = new Point(adjacentX, adjacentY);
                        if (seen.contains(adjacent)) {
                            continue;
                        }

                        seen.add(adjacent);
                        newFrontier.add(adjacent);
                    }
                }
            }

            frontier.addAll(newFrontier);
            distance++;
        }

        if (!tos.isEmpty()) {
            var msg = tos.stream().map(Point::toString).collect(Collectors.joining(","));
            throw new IllegalArgumentException("not in componenet: " + msg);
        }

        return distances;
    }
}
