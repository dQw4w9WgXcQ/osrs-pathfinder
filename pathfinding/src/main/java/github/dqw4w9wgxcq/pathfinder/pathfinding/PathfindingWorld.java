package github.dqw4w9wgxcq.pathfinder.pathfinding;

import github.dqw4w9wgxcq.pathfinder.domain.Point;
import github.dqw4w9wgxcq.pathfinder.domain.Position;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;

public record PathfindingWorld(PathfindingGrid[] planes) {
    public static PathfindingWorld create(int[][][] planes) {
        return new PathfindingWorld(Arrays.stream(planes).map(PathfindingGrid::new).toArray(PathfindingGrid[]::new));
    }

    public Map<Point, Integer> distances(Position from, Set<Point> tos) {
        return planes[from.plane()].distances(from.point(), tos);
    }

    public int[][][] grid() {
        int[][][] out = new int[planes.length][][];
        for (int i = 0; i < planes.length; i++) {
            out[i] = planes[i].grid();
        }
        return out;
    }
}