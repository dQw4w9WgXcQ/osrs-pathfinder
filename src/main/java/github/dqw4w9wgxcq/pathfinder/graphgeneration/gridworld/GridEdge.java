package github.dqw4w9wgxcq.pathfinder.graphgeneration.gridworld;

import github.dqw4w9wgxcq.pathfinder.graphgeneration.algo.Edge;

import java.util.ArrayList;
import java.util.List;

public record GridEdge(int x, int y, TileGrid grid) implements Edge {
    @Override
    public List<Edge> adjacent() {
        var out = new ArrayList<Edge>();
        for (var dx = -1; dx <= 1; dx++) {
            for (var dy = -1; dy <= 1; dy++) {
                if (dx == 0 && dy == 0) {
                    continue;
                }

                if (grid.canTravelInDirection(x, y, dx, dy)) {
                    out.add(new GridEdge(x + dx, y + dy, grid));
                }
            }
        }

        return out;
    }

    @Override
    public int hashCode() {
        return x << 16 | y;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof GridEdge && hashCode() == o.hashCode();
    }
}
