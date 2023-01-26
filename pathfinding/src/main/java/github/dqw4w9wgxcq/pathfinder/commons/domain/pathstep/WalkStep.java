package github.dqw4w9wgxcq.pathfinder.commons.domain.pathstep;

import github.dqw4w9wgxcq.pathfinder.commons.domain.Point;

import java.util.List;

public record WalkStep(String type, int plane, List<Point> path) implements PathStep {
    public WalkStep(int plane, List<Point> path) {
        this("WALK", plane, path);
    }
}
