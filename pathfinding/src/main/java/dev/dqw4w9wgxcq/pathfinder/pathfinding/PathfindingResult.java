package dev.dqw4w9wgxcq.pathfinder.pathfinding;

import dev.dqw4w9wgxcq.pathfinder.commons.domain.Position;
import dev.dqw4w9wgxcq.pathfinder.commons.domain.step.Step;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public sealed interface PathfindingResult permits PathfindingResult.Blocked, PathfindingResult.Success, PathfindingResult.Unreachable {
    record Success(
            Type type,
            Position start,
            Position finish,
            List<Step> steps
    ) implements PathfindingResult {
        Success(Position start, Position finish, List<Step> steps) {
            this(Type.SUCCESS, start, finish, steps);
        }
    }

    record Blocked(
            Type type,
            @Nullable Position start,
            @Nullable Position finish
    ) implements PathfindingResult {
        Blocked(Position start, Position finish) {
            this(Type.BLOCKED, start, finish);
        }
    }

    record Unreachable(
            Type type,
            Position start,
            Position finish
    ) implements PathfindingResult {
        Unreachable(Position start, Position finish) {
            this(Type.UNREACHABLE, start, finish);
        }
    }

    enum Type {
        SUCCESS,
        BLOCKED,
        UNREACHABLE
    }
}