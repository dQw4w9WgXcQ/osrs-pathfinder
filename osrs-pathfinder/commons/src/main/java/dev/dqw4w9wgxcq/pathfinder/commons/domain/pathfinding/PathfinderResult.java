package dev.dqw4w9wgxcq.pathfinder.commons.domain.pathfinding;

import dev.dqw4w9wgxcq.pathfinder.commons.domain.Position;
import dev.dqw4w9wgxcq.pathfinder.commons.domain.step.Step;
import org.jspecify.annotations.Nullable;

import java.util.List;

public sealed interface PathfinderResult
        permits PathfinderResult.Blocked, PathfinderResult.Success, PathfinderResult.Unreachable {
    record Success(Type type, Position start, Position end, List<Step> steps) implements PathfinderResult {
        public Success(Position start, Position end, List<Step> steps) {
            this(Type.SUCCESS, start, end, steps);
        }
    }

    record Blocked(Type type, @Nullable Position start, @Nullable Position end) implements PathfinderResult {
        public Blocked(Position start, Position end) {
            this(Type.BLOCKED, start, end);
        }
    }

    record Unreachable(Type type, Position start, Position end) implements PathfinderResult {
        public Unreachable(Position start, Position end) {
            this(Type.UNREACHABLE, start, end);
        }
    }

    enum Type {
        SUCCESS,
        BLOCKED,
        UNREACHABLE
    }
}
