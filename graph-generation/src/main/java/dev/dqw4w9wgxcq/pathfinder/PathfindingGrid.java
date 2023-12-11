package dev.dqw4w9wgxcq.pathfinder;

import dev.dqw4w9wgxcq.pathfinder.commons.domain.Point;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Accessors(fluent = true)
public class PathfindingGrid {
    public static final int NORTH = 1;
    public static final int SOUTH = 1 << 1;
    public static final int EAST = 1 << 2;
    public static final int WEST = 1 << 3;
    public static final int NORTH_EAST = 1 << 4;
    public static final int NORTH_WEST = 1 << 5;
    public static final int SOUTH_EAST = 1 << 6;
    public static final int SOUTH_WEST = 1 << 7;

    @Getter
    private final int[][] grid;

    //bfs
    public List<Point> findPath(Point from, Point to) {
        if (from.equals(to)) {
            return List.of(to);
        }

        var seenFrom = new HashMap<Point, Point>();
        var frontier = new ArrayDeque<Point>();

        frontier.push(from);
        while (!frontier.isEmpty()) {
            var curr = frontier.pop();
            if (curr.equals(to)) {
                var path = new ArrayDeque<Point>();
                while (!curr.equals(from)) {
                    path.push(curr);
                    curr = seenFrom.get(curr);
                }

                path.push(from);

                return List.copyOf(path);
            }

            int x = curr.x();
            int y = curr.y();
            var directions = grid[x][y];
            if ((directions & NORTH) != 0) {
                addAdjacent(seenFrom, frontier, curr, x, y + 1);
            }

            if ((directions & SOUTH) != 0) {
                addAdjacent(seenFrom, frontier, curr, x, y - 1);
            }

            if ((directions & EAST) != 0) {
                addAdjacent(seenFrom, frontier, curr, x + 1, y);
            }

            if ((directions & WEST) != 0) {
                addAdjacent(seenFrom, frontier, curr, x - 1, y);
            }

            if ((directions & NORTH_EAST) != 0) {
                addAdjacent(seenFrom, frontier, curr, x + 1, y + 1);
            }

            if ((directions & NORTH_WEST) != 0) {
                addAdjacent(seenFrom, frontier, curr, x - 1, y + 1);
            }

            if ((directions & SOUTH_EAST) != 0) {
                addAdjacent(seenFrom, frontier, curr, x + 1, y - 1);
            }

            if ((directions & SOUTH_WEST) != 0) {
                addAdjacent(seenFrom, frontier, curr, x - 1, y - 1);
            }
        }

        return null;
    }

    private static void addAdjacent(Map<Point, Point> seenFrom, Deque<Point> frontier, Point curr, int x, int y) {
        var adjacent = new Point(x, y);
        if (!seenFrom.containsKey(adjacent)) {
            frontier.add(adjacent);
            seenFrom.put(adjacent, curr);
        }
    }

    public Map<Point, Integer> distances(Point from, Set<Point> tos) {
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

                int x = curr.x();
                int y = curr.y();
                var directions = grid[x][y];
                if ((directions & NORTH) != 0) {
                    addAdjacent(seen, newFrontier, x, y + 1);
                }

                if ((directions & SOUTH) != 0) {
                    addAdjacent(seen, newFrontier, x, y - 1);
                }

                if ((directions & EAST) != 0) {
                    addAdjacent(seen, newFrontier, x + 1, y);
                }

                if ((directions & WEST) != 0) {
                    addAdjacent(seen, newFrontier, x - 1, y);
                }

                if ((directions & NORTH_EAST) != 0) {
                    addAdjacent(seen, newFrontier, x + 1, y + 1);
                }

                if ((directions & NORTH_WEST) != 0) {
                    addAdjacent(seen, newFrontier, x - 1, y + 1);
                }

                if ((directions & SOUTH_EAST) != 0) {
                    addAdjacent(seen, newFrontier, x + 1, y - 1);
                }

                if ((directions & SOUTH_WEST) != 0) {
                    addAdjacent(seen, newFrontier, x - 1, y - 1);
                }
            }

            frontier.addAll(newFrontier);
            distance++;
        }

        if (!tos.isEmpty()) {
            var msg = tos.stream().map(Point::toString).collect(Collectors.joining(","));
            throw new IllegalArgumentException("not in component: " + msg);
        }

        return distances;
    }

    private static void addAdjacent(Set<Point> seen, Deque<Point> frontier, int x, int y) {
        var adjacent = new Point(x, y);
        if (!seen.contains(adjacent)) {
            frontier.add(adjacent);
            seen.add(adjacent);
        }
    }
}