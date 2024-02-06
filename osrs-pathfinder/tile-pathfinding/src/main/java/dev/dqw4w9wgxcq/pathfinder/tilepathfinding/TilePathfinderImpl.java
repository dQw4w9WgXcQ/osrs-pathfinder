package dev.dqw4w9wgxcq.pathfinder.tilepathfinding;

import dev.dqw4w9wgxcq.pathfinder.commons.TilePathfinder;
import dev.dqw4w9wgxcq.pathfinder.commons.domain.Point;
import dev.dqw4w9wgxcq.pathfinder.commons.domain.Position;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RequiredArgsConstructor
public class TilePathfinderImpl implements TilePathfinder {
    private final PathfindingGrid[] planes;

    @SuppressWarnings("unused")
    public static TilePathfinderImpl create(byte[][][] planes) {
        return new TilePathfinderImpl(
                Arrays.stream(planes).map(PathfindingGrid::new).toArray(PathfindingGrid[]::new));
    }

    @Override
    public boolean isRemote() {
        return false;
    }

    @Override
    public List<Point> findPath(int plane, Point start, Point end) {
        return planes[plane].findPath(start, end);
    }

    @Override
    public Map<Point, Integer> distances(Position from, Set<Point> tos) {
        return planes[from.plane()].distances(from.toPoint(), tos);
    }

    public byte[][][] grid() {
        var out = new byte[planes.length][][];
        for (var i = 0; i < planes.length; i++) {
            out[i] = planes[i].grid();
        }
        return out;
    }
}