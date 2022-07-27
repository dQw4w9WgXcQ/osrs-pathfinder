package github.dqw4w9wgxcq.pathfinder.graphgeneration.algo;

import github.dqw4w9wgxcq.pathfinder.graphgeneration.commons.Point;
import github.dqw4w9wgxcq.pathfinder.graphgeneration.gridworld.TileGrid;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Nullable;
import java.util.*;

@Slf4j
public class Algo {
    public static @Nullable List<Point> aStar(TileGrid map, Point start, Point end) {
        var seenFrom = new HashMap<Point, Point>();
        var frontier = new PriorityQueue<Point>(Comparator.comparingDouble(p -> chebyshev(p, end)));

        frontier.add(start);
        while (!frontier.isEmpty()) {
            var curr = frontier.poll();
            if (curr.equals(end)) {
                var path = new ArrayList<Point>();
                while (!curr.equals(start)) {
                    path.add(curr);
                    curr = seenFrom.get(curr);
                }
                return path;
            }

            for (var dx = -1; dx < 1; dx++) {
                for (var dy = -1; dy < 1; dy++) {
                    if (dx == 0 && dy == 0) {
                        continue;
                    }

                    var adjacent = new Point(curr.x() + dx, curr.y() + dy);

                    if (seenFrom.containsKey(adjacent)) {
                        continue;
                    }

                    if (map.canTravelInDirection(curr.x(), curr.y(), dx, dy)) {
                        frontier.add(adjacent);
                        seenFrom.put(adjacent, curr);
                    }
                }
            }
        }

        return null;
    }

    private static int chebyshev(Point from, Point to) {
        return Math.max(Math.abs(from.x() - to.x()), Math.abs(from.y() - to.y()));
    }

    public static @Nullable List<Edge> dijkstra(Edge start, Edge end) {
        var seenFrom = new HashMap<Edge, Edge>();
        var frontier = new PriorityQueue<Edge>();

        frontier.add(start);
        while (!frontier.isEmpty()) {
            var curr = frontier.remove();
            if (curr.equals(end)) {
                var path = new ArrayList<Edge>();
                while (!curr.equals(start)) {
                    path.add(0, curr);
                    curr = seenFrom.get(curr);
                }
                return path;
            }

            for (var adj : curr.adjacent()) {
                if (!seenFrom.containsKey(adj)) {
                    seenFrom.put(adj, curr);
                    frontier.add(adj);
                }
            }
        }

        return null;
    }
}
