package github.dqw4w9wgxcq.pathfinder.graphgeneration.link.types.stair;

import github.dqw4w9wgxcq.pathfinder.graphgeneration.link.Link;
import github.dqw4w9wgxcq.pathfinder.graphgeneration.link.requirement.LinkRequirement;
import net.runelite.cache.region.Position;

import java.util.List;

public record StairLink(
        Position source,
        Position destination,
        List<LinkRequirement> requirements,
        int objectId,
        boolean up
) implements Link {
    @Override
    public int cost() {
        return 10;
    }
}
