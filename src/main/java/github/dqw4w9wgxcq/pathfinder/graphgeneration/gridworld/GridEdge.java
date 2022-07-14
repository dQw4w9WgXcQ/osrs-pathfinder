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
        return getId();
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof GridEdge && hashCode() == o.hashCode();
    }

    public int getId() {
        return toId(x, y);
    }

    public static int toId(int x, int y) {
        return x << 16 | y;
    }

    public static Point toPoint(int id) {
        return new Point(id >> 16, id & 0xFFFF);
    }

    @Override
    public String toString() {
        return "GridEdge[" +
                "x=" + x + ", " +
                "y=" + y + ", " +
                ']';
    }
}
