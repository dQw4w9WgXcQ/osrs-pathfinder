package github.dqw4w9wgxcq.pathfinder.graphgeneration.algo;

import github.dqw4w9wgxcq.pathfinder.graphgeneration.commons.Point;
import github.dqw4w9wgxcq.pathfinder.graphgeneration.tileworld.TileGrid;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Nullable;
import java.util.*;

@Slf4j
public class Algo {
    /**
     * @return the path or null if not reachable
     */
    public static @Nullable List<Point> aStar(TileGrid grid, Point start, Point end) {
        var seenFrom = new HashMap<Point, Point>();
        var frontier = new PriorityQueue<Point>(Comparator.comparingInt(p -> chebyshevDistance(p, end)));

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

                    if (grid.canTravelInDirection(curr.x(), curr.y(), dx, dy)) {
                        frontier.add(adjacent);
                        seenFrom.put(adjacent, curr);
                    }
                }
            }
        }

        return null;
    }

    /**
     * @return the path or null if not reachable
     */
    public static <E extends Edge<E>> @Nullable List<E> dijkstra(E start, E end) {
        var seenFrom = new HashMap<E, E>();
        var frontier = new PriorityQueue<E>();

        frontier.add(start);
        while (!frontier.isEmpty()) {
            var curr = frontier.remove();
            if (curr.equals(end)) {
                var path = new ArrayList<E>();
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

    public static int chebyshevDistance(Point from, Point to) {
        return Math.max(Math.abs(from.x() - to.x()), Math.abs(from.y() - to.y()));
    }
}
