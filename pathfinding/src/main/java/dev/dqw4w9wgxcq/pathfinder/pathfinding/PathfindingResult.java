package dev.dqw4w9wgxcq.pathfinder.pathfinding;

import dev.dqw4w9wgxcq.pathfinder.commons.domain.Position;
import dev.dqw4w9wgxcq.pathfinder.commons.domain.pathstep.PathStep;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public sealed interface PathfindingResult permits PathfindingResult.Blocked, PathfindingResult.Success, PathfindingResult.Unreachable {
    record Success(
            Position start,
            Position finish,
            List<PathStep> steps
    ) implements PathfindingResult {
    }

    record Blocked(
            @Nullable Position start,
            @Nullable Position finish
    ) implements PathfindingResult {
    }

    record Unreachable(
            Position start,
            Position finish
    ) implements PathfindingResult {
    }
}