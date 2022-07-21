package github.dqw4w9wgxcq.pathfinder.graphgeneration.algo;

import lombok.extern.slf4j.Slf4j;

import javax.annotation.Nullable;
import java.util.*;

@Slf4j
public class Algo {
    public static @Nullable List<Edge> aStar(Edge start, Edge end) {
        throw new UnsupportedOperationException("Not implemented yet");//todo
    }

    public static @Nullable List<Edge> bfs(Edge start, Edge end) {
        var seenFrom = new HashMap<Edge, Edge>();
        var frontier = new LinkedList<Edge>();
        frontier.add(start);
        while (!frontier.isEmpty()) {
            var current = frontier.remove();
            if (current.equals(end)) {
                throw new UnsupportedOperationException("not implemented");//todo
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
