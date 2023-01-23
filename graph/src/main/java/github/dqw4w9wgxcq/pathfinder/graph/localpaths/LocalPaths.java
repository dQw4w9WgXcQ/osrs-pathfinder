package github.dqw4w9wgxcq.pathfinder.graph.localpaths;

import github.dqw4w9wgxcq.pathfinder.domain.Point;

import java.util.List;

public interface LocalPaths {
    List<Point> get(int plane, Point start, Point end);
}
