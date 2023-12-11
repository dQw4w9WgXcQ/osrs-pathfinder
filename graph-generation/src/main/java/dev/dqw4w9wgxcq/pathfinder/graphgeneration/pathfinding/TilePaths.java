package dev.dqw4w9wgxcq.pathfinder.graphgeneration.pathfinding;

import dev.dqw4w9wgxcq.pathfinder.commons.domain.Point;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.VisibleForTesting;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
public class TilePaths {
    private final TilePathfindingForGraphGen tilePathfinding;

    public List<Point> findPath(int plane, Point start, Point end) {
        return newPath(plane, start, end);
    }

    private List<Point> newPath(int plane, Point start, Point end) {
        var startTime = System.currentTimeMillis();
        var path = tilePathfinding.findPath(plane, start, end);
        var endTime = System.currentTimeMillis();
        log.debug("local path from {} to {} in {} ms", start, end, endTime - startTime);
        if (path == null) {
            log.debug("no path found");
            return null;
        }

        return toMinifiedPath(path);
    }

    @VisibleForTesting
    static List<Point> toMinifiedPath(List<Point> path) {
        var minified = new ArrayList<Point>();
        Point prevPrev = null;
        Point prev = null;
        for (var curr : path) {
            if (prev == null) {
                prev = curr;
                minified.add(curr);
                continue;
            }

            if (prevPrev == null) {
                prevPrev = prev;
                prev = curr;
                continue;
            }

            var dx = prev.x() - prevPrev.x();
            var dy = prev.y() - prevPrev.y();
            var dx2 = curr.x() - prev.x();
            var dy2 = curr.y() - prev.y();

            if (dx != dx2 || dy != dy2) {
                minified.add(prev);
            }

            prevPrev = prev;
            prev = curr;
        }

        minified.add(prev);
        return minified;
    }
}