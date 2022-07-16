package github.dqw4w9wgxcq.pathfinder.graphgeneration.link;

import github.dqw4w9wgxcq.pathfinder.graphgeneration.link.requirement.LinkRequirement;
import net.runelite.cache.region.Position;

import java.util.List;

public interface Link {
    Position destination();

    /**
     * approximate cost in terms of walking tile units
     */
    int cost();

    List<LinkRequirement> requirements();
}
