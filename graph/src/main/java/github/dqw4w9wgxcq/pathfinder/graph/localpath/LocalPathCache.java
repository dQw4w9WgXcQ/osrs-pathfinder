package github.dqw4w9wgxcq.pathfinder.graph.localpath;

import github.dqw4w9wgxcq.pathfinder.domain.Point;
import github.dqw4w9wgxcq.pathfinder.graph.Algo;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class LocalPathCache {
    private final int[][][] components;

    public List<Point> get(int plane, Point start, Point end) {
        //todo
        return minify(Algo.astar(components[plane], start, end));
    }

    private static List<Point> minify(List<Point> path) {
        //todo
        return path;
    }
}
