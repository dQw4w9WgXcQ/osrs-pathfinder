package github.dqw4w9wgxcq.pathfinder.commons.domain.link;

import github.dqw4w9wgxcq.pathfinder.commons.domain.requirement.Requirement;
import github.dqw4w9wgxcq.pathfinder.commons.domain.Position;

import java.util.List;

public record DungeonLink(
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
