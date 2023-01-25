package github.dqw4w9wgxcq.pathfinder.pathfinding;

import github.dqw4w9wgxcq.pathfinder.domain.Position;
import github.dqw4w9wgxcq.pathfinder.domain.pathstep.PathStep;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public record PathfindingResult(@Nullable Position start, @Nullable Position finish, @Nullable List<PathStep> steps) {
}
