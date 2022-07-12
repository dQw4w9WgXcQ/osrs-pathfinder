package github.dqw4w9wgxcq.pathfinder.graphgeneration.algo;

import github.dqw4w9wgxcq.pathfinder.graphgeneration.algo.Edge;
import github.dqw4w9wgxcq.pathfinder.graphgeneration.gridworld.GridEdge;

import javax.annotation.Nullable;
import java.util.*;

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
                var path = new LinkedList<Edge>();
                var currentEdge = current;
                while (currentEdge != null) {
                    path.add(0, currentEdge);
                    currentEdge = seenFrom.get(currentEdge);
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

    public static Set<Edge> floodFill(Edge start) {
        var seen = new HashSet<Edge>();
        var frontier = new LinkedList<Edge>();

        seen.add(start);
        frontier.add(start);

        while (!frontier.isEmpty()) {
            var current = frontier.pop();
            for (var adjacent : current.adjacent()) {
                if (!seen.contains(adjacent)) {
                    seen.add(adjacent);
                    frontier.add(adjacent);
                }
            }
        }

        return seen;
    }
}
