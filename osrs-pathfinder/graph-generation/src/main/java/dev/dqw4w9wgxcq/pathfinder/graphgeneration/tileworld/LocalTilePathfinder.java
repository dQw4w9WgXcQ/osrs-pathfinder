package dev.dqw4w9wgxcq.pathfinder.graphgeneration.tileworld;

import dev.dqw4w9wgxcq.pathfinder.commons.domain.Point;
import dev.dqw4w9wgxcq.pathfinder.commons.domain.Position;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RequiredArgsConstructor
public class LocalTilePathfinder {
    private final PathfindingGrid[] planes;

    @SuppressWarnings("unused")
    public static LocalTilePathfinder create(byte[][][] planes) {
        return new LocalTilePathfinder(
                Arrays.stream(planes).map(PathfindingGrid::new).toArray(PathfindingGrid[]::new));
    }

    public List<Point> findPath(int plane, Point start, Point end) {
        return planes[plane].findPath(start, end);
    }

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
