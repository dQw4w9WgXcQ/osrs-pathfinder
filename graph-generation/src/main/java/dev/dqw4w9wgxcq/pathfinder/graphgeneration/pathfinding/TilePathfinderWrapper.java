package dev.dqw4w9wgxcq.pathfinder.graphgeneration.pathfinding;

import dev.dqw4w9wgxcq.pathfinder.commons.TilePathfinder;
import dev.dqw4w9wgxcq.pathfinder.commons.domain.Point;
import dev.dqw4w9wgxcq.pathfinder.commons.domain.Position;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;

@RequiredArgsConstructor
@Slf4j
public class TilePathfinderWrapper implements TilePathfinder {
    private final TilePathfinder delegate;
    private final int maxConcurrency = 10;


    private record PathCacheKey(int plane, Point start, Point end) {
    }

    private final Map<PathCacheKey, List<Point>> pathCache = new ConcurrentHashMap<>();
    private final Semaphore pathSemaphore = new Semaphore(maxConcurrency, true);

    @Override
    public List<Point> findPath(int plane, Point start, Point end) {
        return pathCache.computeIfAbsent(new PathCacheKey(plane, start, end), k -> newPath(k.plane(), k.start(), k.end()));
    }

    @Override
    public Map<Point, Integer> distances(Position from, Set<Point> tos) {
        return delegate.distances(from, tos);
    }

    @Override
    public boolean isRemote() {
        return delegate.isRemote();
    }

    @SneakyThrows(InterruptedException.class)
    private List<Point> newPath(int plane, Point start, Point end) {
        List<Point> path;
        long time;
        try {
            pathSemaphore.acquire();
            var startTime = System.currentTimeMillis();
            path = delegate.findPath(plane, start, end);
            time = System.currentTimeMillis() - startTime;
        } finally {
            pathSemaphore.release();
        }

        log.debug("from {} to {} in {} ms", start, end, time);

        if (path == null) {
            log.debug("no path found");
            return null;
        }

        return toMinifiedPath(path);
    }

    public static List<Point> toMinifiedPath(List<Point> path) {
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