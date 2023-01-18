package github.dqw4w9wgxcq.pathfinder.domain.link;

import github.dqw4w9wgxcq.pathfinder.domain.Position;
import github.dqw4w9wgxcq.pathfinder.domain.Requirement;

import java.util.List;

public record StairLink(
        int id,
        Position origin,
        Position destination,
        int objectId,
        boolean up
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
