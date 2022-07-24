package github.dqw4w9wgxcq.pathfinder.graphgeneration.algo;

import lombok.extern.slf4j.Slf4j;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;

@Slf4j
public class Algo {
    public static @Nullable List<Edge> aStar(Edge start, Edge end) {
        throw new UnsupportedOperationException("Not implemented yet");//todo
    }

    public static @Nullable List<Edge> dijkstra(Edge start, Edge end) {
        var seenFrom = new HashMap<Edge, Edge>();
        var frontier = new PriorityQueue<Edge>();
        frontier.add(start);
        while (!frontier.isEmpty()) {
            var current = frontier.remove();
            if (current.equals(end)) {
                //backtrack and return path
                var path = new LinkedList<Edge>();
                while (!current.equals(start)) {
                    path.addFirst(current);
                    current = seenFrom.get(current);
                }
                return path;
            }

            for (var edge : current.adjacent()) {
                if (!seenFrom.containsKey(edge)) {
                    seenFrom.put(edge, current);
                    frontier.add(edge);
                }
            }
        }

        return null;
    }
}
