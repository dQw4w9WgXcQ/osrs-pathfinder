package dev.dqw4w9wgxcq.pathfinder.tilepathfinding;

import dev.dqw4w9wgxcq.pathfinder.commons.TilePathfinder;
import dev.dqw4w9wgxcq.pathfinder.commons.domain.Point;
import dev.dqw4w9wgxcq.pathfinder.commons.domain.Position;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.Semaphore;

@Slf4j
public class TilePathfinderWrapper implements TilePathfinder {
    private record PathCacheKey(int plane, Point start, Point end) {
    }

    private final TilePathfinder delegate;
    private final Semaphore pathSemaphore;
    private final Map<PathCacheKey, FutureTask<List<Point>>> pathCache = new HashMap<>();

    public TilePathfinderWrapper(TilePathfinder delegate, int maxConcurrency) {
        this.delegate = delegate;
        pathSemaphore = new Semaphore(maxConcurrency, true);
    }

    @SneakyThrows({InterruptedException.class, ExecutionException.class})
    @Override
    public List<Point> findPath(int plane, Point start, Point end) {
        var key = new PathCacheKey(plane, start, end);

        FutureTask<List<Point>> future;
        synchronized (pathCache) {
            if (pathCache.containsKey(key)) {
                future = pathCache.get(key);
            } else {
                future = new FutureTask<>(() -> newPath(plane, start, end));
                pathCache.put(key, future);
                future.run();
            }
        }

        return future.get();
    }

    @Override
    public Map<Point, Integer> distances(Position from, Set<Point> tos) {
        return delegate.distances(from, tos);
    }

    @Override
    public boolean isRemote() {
        return delegate.isRemote();
    }

    private List<Point> newPath(int plane, Point start, Point end) throws InterruptedException {
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