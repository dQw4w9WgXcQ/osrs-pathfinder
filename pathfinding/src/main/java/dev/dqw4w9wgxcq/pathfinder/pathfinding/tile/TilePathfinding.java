package dev.dqw4w9wgxcq.pathfinder.pathfinding.tile;

import dev.dqw4w9wgxcq.pathfinder.commons.domain.Point;
import dev.dqw4w9wgxcq.pathfinder.commons.domain.Position;
import dev.dqw4w9wgxcq.pathfinder.pathfinding.PathfindingGrid;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;

public record TilePathfinding(PathfindingGrid[] planes) {
    public static TilePathfinding create(int[][][] planes) {
        return new TilePathfinding(Arrays.stream(planes).map(PathfindingGrid::new).toArray(PathfindingGrid[]::new));
    }

    public Map<Point, Integer> distances(Position from, Set<Point> tos) {
        return planes[from.plane()].distances(from.toPoint(), tos);
    }

    public int[][][] grid() {
        int[][][] out = new int[planes.length][][];
        for (int i = 0; i < planes.length; i++) {
            out[i] = planes[i].grid();
        }
        return out;
    }
}