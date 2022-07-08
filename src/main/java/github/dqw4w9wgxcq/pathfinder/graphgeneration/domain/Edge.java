package github.dqw4w9wgxcq.pathfinder.graphgeneration.domain;

import java.util.List;

public record Edge(int x, int y, List<Edge> adjacent, Requirement requirement) {
    public Edge {
        if (x < 0 || y < 0) {
            throw new IllegalArgumentException(String.format("x and y must be non-negative, but got x = %d and y = %d", x, y));
        }
    }
}