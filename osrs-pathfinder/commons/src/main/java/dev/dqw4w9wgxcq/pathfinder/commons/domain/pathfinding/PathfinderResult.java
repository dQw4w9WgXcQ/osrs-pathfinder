package dev.dqw4w9wgxcq.pathfinder.commons.domain.pathfinding;

import dev.dqw4w9wgxcq.pathfinder.commons.domain.Position;
import dev.dqw4w9wgxcq.pathfinder.commons.domain.step.Step;
import org.jspecify.annotations.Nullable;

import java.util.List;

public sealed interface PathfinderResult
        permits PathfinderResult.Blocked, PathfinderResult.Success, PathfinderResult.Unreachable {
    record Success(Type type, Position start, Position finish, List<Step> steps) implements PathfinderResult {
        public Success(Position start, Position finish, List<Step> steps) {
            this(Type.SUCCESS, start, finish, steps);
        }
    }

    record Blocked(Type type, @Nullable Position start, @Nullable Position finish) implements PathfinderResult {
        public Blocked(Position start, Position finish) {
            this(Type.BLOCKED, start, finish);
        }
    }

    record Unreachable(Type type, Position start, Position finish) implements PathfinderResult {
        public Unreachable(Position start, Position finish) {
            this(Type.UNREACHABLE, start, finish);
        }
    }

    enum Type {
        SUCCESS,
        BLOCKED,
        UNREACHABLE
    }
}