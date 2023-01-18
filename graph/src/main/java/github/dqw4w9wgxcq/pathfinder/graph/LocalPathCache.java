package github.dqw4w9wgxcq.pathfinder.graph;

import github.dqw4w9wgxcq.pathfinder.domain.Point;
import github.dqw4w9wgxcq.pathfinder.graph.domain.ComponentGrid;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class LocalPathCache {
    private final ComponentGrid componentGrid;

    public List<Point> get(int plane, Point start, Point end) {
        //todo
        return minify(Algo.bfs(componentGrid.planes()[plane], start, end));
    }

    private static List<Point> minify(List<Point> path) {
        //todo
        return path;
    }
}
