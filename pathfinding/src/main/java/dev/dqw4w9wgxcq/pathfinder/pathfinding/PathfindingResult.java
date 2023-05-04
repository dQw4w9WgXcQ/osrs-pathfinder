package dev.dqw4w9wgxcq.pathfinder.pathfinding;

import dev.dqw4w9wgxcq.pathfinder.commons.domain.Position;
import dev.dqw4w9wgxcq.pathfinder.commons.domain.pathstep.PathStep;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public record PathfindingResult(@Nullable Position start, @Nullable Position finish, @Nullable List<PathStep> steps) {
}
