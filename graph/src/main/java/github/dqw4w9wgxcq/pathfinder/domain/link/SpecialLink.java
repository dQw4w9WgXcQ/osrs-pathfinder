package github.dqw4w9wgxcq.pathfinder.domain.link;

import github.dqw4w9wgxcq.pathfinder.domain.Position;
import github.dqw4w9wgxcq.pathfinder.domain.Requirement;

import java.util.List;

public record SpecialLink(
        int id,
        Position origin,
        Position destination,
        int cost,
        List<Requirement> requirements
) implements Link {
}
