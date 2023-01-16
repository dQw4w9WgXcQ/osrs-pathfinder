package github.dqw4w9wgxcq.pathfinder.graph.domain.link;

import github.dqw4w9wgxcq.pathfinder.graph.domain.Position;
import github.dqw4w9wgxcq.pathfinder.graph.domain.Requirement;

import java.util.List;

public record UndergroundLink(
        int id,
        Position origin,
        Position destination,
        int objectId,
        String action
) implements Link {
    @Override
    public int cost() {
        return 10;
    }

    @Override
    public List<Requirement> requirements() {
        return List.of();
    }
}
