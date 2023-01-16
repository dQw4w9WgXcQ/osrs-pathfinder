package github.dqw4w9wgxcq.pathfinder.graph.domain.link;

import github.dqw4w9wgxcq.pathfinder.graph.domain.Position;
import github.dqw4w9wgxcq.pathfinder.graph.domain.Requirement;

import java.util.List;

public record SpecialLink(
        int id,
        Position origin,
        Position destination,
        int cost,
        List<Requirement> requirements
) implements Link {
}
