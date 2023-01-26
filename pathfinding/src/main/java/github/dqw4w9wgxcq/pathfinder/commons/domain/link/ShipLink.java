package github.dqw4w9wgxcq.pathfinder.commons.domain.link;

import github.dqw4w9wgxcq.pathfinder.commons.domain.Position;
import github.dqw4w9wgxcq.pathfinder.commons.domain.requirement.Requirement;

import java.util.List;

public record ShipLink(int id, Position origin, Position destination) implements Link {
    @Override
    public int cost() {
        return 30;
    }

    @Override
    public List<Requirement> requirements() {
        return List.of();
    }
}
