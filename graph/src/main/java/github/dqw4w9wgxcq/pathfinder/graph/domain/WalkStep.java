package github.dqw4w9wgxcq.pathfinder.graph.domain;

import github.dqw4w9wgxcq.pathfinder.domain.Point;

import java.util.List;

public record WalkStep(int plane, List<Point> path) implements PathStep {
}
