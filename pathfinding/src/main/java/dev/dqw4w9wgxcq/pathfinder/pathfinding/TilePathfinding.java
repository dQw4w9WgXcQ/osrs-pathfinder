package dev.dqw4w9wgxcq.pathfinder.pathfinding;

import dev.dqw4w9wgxcq.pathfinder.commons.domain.Point;
import dev.dqw4w9wgxcq.pathfinder.commons.domain.Position;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;

public record TilePathfinding(PathfindingGrid[] planes) {
    public static TilePathfinding create(int[][][] planes) {
        return new TilePathfinding(Arrays.stream(planes).map(PathfindingGrid::new).toArray(PathfindingGrid[]::new));
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