package dev.dqw4w9wgxcq.pathfinder.commons;

import dev.dqw4w9wgxcq.pathfinder.commons.domain.Point;
import dev.dqw4w9wgxcq.pathfinder.commons.domain.Position;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface TilePathfinder {
    List<Point> findPath(int plane, Point start, Point end);

    Map<Point, Integer> distances(Position from, Set<Point> tos);

    boolean isRemote();
}