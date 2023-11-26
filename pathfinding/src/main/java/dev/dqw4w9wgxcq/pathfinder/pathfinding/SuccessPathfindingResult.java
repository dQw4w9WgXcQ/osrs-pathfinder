package dev.dqw4w9wgxcq.pathfinder.pathfinding;

import dev.dqw4w9wgxcq.pathfinder.commons.domain.Position;
import dev.dqw4w9wgxcq.pathfinder.commons.domain.pathstep.PathStep;

import java.util.List;

public record SuccessPathfindingResult(
        ResultType type,
        Position start,
        Position finish,
        List<PathStep> steps
) implements PathfindingResult {
}