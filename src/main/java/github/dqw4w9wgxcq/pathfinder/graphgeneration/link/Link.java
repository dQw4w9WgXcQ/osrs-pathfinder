package github.dqw4w9wgxcq.pathfinder.graphgeneration.link;

import net.runelite.cache.region.Position;

public interface Link {
    Position destination();

    /**
     * approximate cost in terms of walking tile units
     */
    int cost();
}
