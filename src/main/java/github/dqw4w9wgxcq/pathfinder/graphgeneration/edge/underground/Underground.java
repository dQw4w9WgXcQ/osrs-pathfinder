package github.dqw4w9wgxcq.pathfinder.graphgeneration.edge.underground;

import lombok.AllArgsConstructor;
import net.runelite.cache.ObjectManager;
import net.runelite.cache.region.RegionLoader;

/**
 * searches for stairs/ladders on plane 0 going down and links them to a stair/ladder in the underground area (100 regions aka 6400 tiles up)
 */
@AllArgsConstructor
public class Underground {
    private final RegionLoader regionLoader;
    private final ObjectManager objectManager;

}
