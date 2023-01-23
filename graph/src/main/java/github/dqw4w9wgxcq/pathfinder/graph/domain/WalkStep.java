package github.dqw4w9wgxcq.pathfinder.graph.domain;

import github.dqw4w9wgxcq.pathfinder.domain.Point;

import java.util.List;

public record WalkStep(String type, int plane, List<Point> path) implements PathStep {
    public WalkStep(int plane, List<Point> path) {
        this("WALK", plane, path);
    }

    @Override
    public String toString() {
        return "WalkStep[plane=" + plane() + " from " + path.get(0) + " to " + path.get(path.size() - 1) + "]";
    }
}
