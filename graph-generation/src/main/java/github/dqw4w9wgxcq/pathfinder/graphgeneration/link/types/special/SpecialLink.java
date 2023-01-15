package github.dqw4w9wgxcq.pathfinder.graphgeneration.link.types.special;

import github.dqw4w9wgxcq.pathfinder.graphgeneration.link.Link;
import github.dqw4w9wgxcq.pathfinder.graphgeneration.link.requirement.LinkRequirement;
import net.runelite.cache.region.Position;

import java.util.ArrayList;
import java.util.List;

public record SpecialLink(
        Position origin,
        Position destination
) implements Link {
    @Override
    public int cost() {
        return 10;
    }

    @Override
    public List<LinkRequirement> requirements() {
        return new ArrayList<>();
    }
}
