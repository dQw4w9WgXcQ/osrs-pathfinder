package github.dqw4w9wgxcq.pathfinder.commons.domain.link;

import github.dqw4w9wgxcq.pathfinder.commons.domain.requirement.Requirement;
import github.dqw4w9wgxcq.pathfinder.commons.domain.Position;

import java.util.List;

/**
 * A simple door(or gate etc.).  Doors with requirements or that are unidirectional are SpecialLinks.
 */
public record DoorLink(
        int id,
        Position origin,
        Position destination,
        int objectId
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
