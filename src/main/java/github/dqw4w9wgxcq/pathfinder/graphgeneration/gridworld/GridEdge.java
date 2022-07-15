package github.dqw4w9wgxcq.pathfinder.graphgeneration.gridworld;

import github.dqw4w9wgxcq.pathfinder.graphgeneration.algo.Edge;
import github.dqw4w9wgxcq.pathfinder.graphgeneration.commons.Point;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@AllArgsConstructor
public class GridEdge implements Edge {
    @Getter
    private final int x, y;
    private final TileGrid grid;

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

    public static Point toPoint(int hash) {
        return new Point(hash >> 16, hash & 0xFFFF);
    }

    @Override
    public String toString() {
        return "GridEdge[" +
                "x=" + x + ", " +
                "y=" + y +
                ']';
    }
}
