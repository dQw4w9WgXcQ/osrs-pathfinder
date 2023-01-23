package github.dqw4w9wgxcq.pathfinder.graph.localpaths;

import github.dqw4w9wgxcq.pathfinder.domain.Point;
import github.dqw4w9wgxcq.pathfinder.graph.Algo;
import github.dqw4w9wgxcq.pathfinder.graph.domain.ComponentGrid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
public class LocalPathCache implements LocalPaths {
    private final ComponentGrid componentGrid;

    public List<Point> get(int plane, Point start, Point end) {
        //todo caching
        return newPath(plane, start, end);
    }

    private List<Point> newPath(int plane, Point start, Point end) {
        var startTime = System.currentTimeMillis();
        var path = Algo.bfs(componentGrid.planes()[plane], start, end);
        var endTime = System.currentTimeMillis();
        log.debug("local path from {} to {} in {} ms", start, end, endTime - startTime);
        if (path == null) {
            log.debug("no path found");
            return null;
        }

        return minifyPath(path);
    }

    static List<Point> minifyPath(List<Point> path) {
        var minified = new ArrayList<Point>();
        Point prevPrev = null;
        Point prev = null;
        for (Point curr : path) {
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

            int dx = prev.x() - prevPrev.x();
            int dy = prev.y() - prevPrev.y();
            int dx2 = curr.x() - prev.x();
            int dy2 = curr.y() - prev.y();

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