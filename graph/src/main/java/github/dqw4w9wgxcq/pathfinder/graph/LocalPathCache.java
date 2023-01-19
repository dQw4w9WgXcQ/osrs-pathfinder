package github.dqw4w9wgxcq.pathfinder.graph;

import github.dqw4w9wgxcq.pathfinder.domain.Point;
import github.dqw4w9wgxcq.pathfinder.graph.domain.ComponentGrid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
public class LocalPathCache {
    private final ComponentGrid componentGrid;

    public List<Point> get(int plane, Point start, Point end) {
        //todo caching

        var startTime = System.currentTimeMillis();
        var path = Algo.bfs(componentGrid.planes()[plane], start, end);
        var endTime = System.currentTimeMillis();
        log.debug("found path in {}ms", endTime - startTime);
        return minify(path);
    }

    private static List<Point> minify(List<Point> path) {
        //todo
        return path;
    }
}
