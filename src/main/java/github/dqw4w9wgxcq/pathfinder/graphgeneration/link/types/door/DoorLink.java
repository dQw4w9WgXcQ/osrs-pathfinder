package github.dqw4w9wgxcq.pathfinder.graphgeneration.link.types.door;

import github.dqw4w9wgxcq.pathfinder.graphgeneration.link.Link;
import github.dqw4w9wgxcq.pathfinder.graphgeneration.link.requirement.LinkRequirement;
import net.runelite.cache.region.Position;

import java.util.List;

/**
 * A simple door(or gate etc.).  Doors with requirements or that are unidirectional are SpecialLinks.
 *
 * @param destination
 * @param objectId
 */
public record DoorLink(Position origin, Position destination, int objectId) implements Link {
    @Override
    public int cost() {
        return 10;
    }

    @Override
    public List<LinkRequirement> requirements() {
        return List.of();
    }

    @Override
    public boolean bidirectional() {
        return true;
    }
}
