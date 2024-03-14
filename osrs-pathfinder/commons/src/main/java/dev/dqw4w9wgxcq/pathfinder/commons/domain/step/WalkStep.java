package dev.dqw4w9wgxcq.pathfinder.commons.domain.step;

import dev.dqw4w9wgxcq.pathfinder.commons.domain.Point;

import java.util.List;

public record WalkStep(Type type, boolean cached, int cost, int plane, List<Point> path) implements Step {
    public WalkStep(boolean cached, int cost, int plane, List<Point> path) {
        this(Type.WALK, cached, cost, plane, path);
    }
}
