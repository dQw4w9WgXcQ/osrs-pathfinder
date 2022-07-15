package github.dqw4w9wgxcq.pathfinder.graphgeneration.link;

import net.runelite.cache.region.Position;

public interface Link {
    Position destination();

    /**
     * approximate cost of traversing this link in terms of walking tile units
     */
    int cost();
}
